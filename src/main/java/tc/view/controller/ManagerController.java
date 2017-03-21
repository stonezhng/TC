package tc.view.controller;

import javafx.collections.ObservableArrayBase;
import org.apache.commons.collections.FastHashMap;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tc.bean.CourseCheckVO;
import tc.bean.FinanceCheckVO;
import tc.bean.ManagerInfoVO;
import tc.model.CourseUpdate;
import tc.service.managerService.ManagerVOManger;
import tc.service.managerService.ManagerVerify;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stonezhang on 2017/3/15.
 */
@Controller
@RequestMapping("/admin")
public class ManagerController {
    @Autowired
    private ManagerVerify managerVerifyImpl;

    @Autowired
    private ManagerVOManger managerVOMangerImpl;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        System.out.println();
        return "manager/managerLogin";
    }

    @RequestMapping(value = "/login/info", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> doLogin(HttpServletRequest request) {
        String name = request.getParameter("managerName");
        String pasword = request.getParameter("password");
        Map<String, Object> map = new HashMap<>();
        if (managerVerifyImpl.exist(name) && managerVerifyImpl.isCorrectPsw(name, pasword)) {
            HttpSession session = request.getSession(false);
            if(session == null) {
                map.put("success", false);
            }
            else {
                session.setAttribute("managerInfoVO", managerVOMangerImpl.getManagerInfoVO(name));
                map.put("success", true);
            }
        }
        else {
            map.put("success", false);
        }
        return map;
    }

    @RequestMapping(value = "/register/info", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> doRegister(HttpServletRequest request) {
        String name = request.getParameter("managerName");
        String password = request.getParameter("password");
        Map<String, Object> map = new HashMap<>();
        if(!managerVerifyImpl.exist(name)) {
            managerVerifyImpl.register(name, password);
            HttpSession session = request.getSession(false);
            if(session ==  null) {
                map.put("success", false);
            }
            else {
                session.setAttribute("managerInfoVO", managerVOMangerImpl.getManagerInfoVO(name));
                map.put("success", true);
            }
        }
        else {
            map.put("success", false);
        }
        return map;
    }

    @RequestMapping(value = "/homepage", method = RequestMethod.GET)
    public String getHomepage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if(session == null) {
            return "redirect: /admin/login";
        }
        ManagerInfoVO managerInfoVO = (ManagerInfoVO) session.getAttribute("managerInfoVO");
        List<CourseCheckVO> courseCheckVOReleaseUnCheckList = managerVOMangerImpl.getReleaseCourseCheckVOUncheck(managerInfoVO.getId());
        List<CourseCheckVO> courseCheckVOUpdateUnCheckList = managerVOMangerImpl.getUpdateCourseCheckVOUncheck(managerInfoVO.getId());
//        List<CourseCheckVO> courseCheckVOPassedList = managerVOMangerImpl.getCourseCheckVOChecked(managerInfoVO.getId());
        System.out.println("from managerController-getHomepage: uncheckList Update: " + courseCheckVOUpdateUnCheckList);
        model.addAttribute("uncheckReleaseList", courseCheckVOReleaseUnCheckList);
        model.addAttribute("uncheckUpdateList", courseCheckVOUpdateUnCheckList);
//        model.addAttribute("checkedList", courseCheckVOCheckedList);
        return "/manager/homepage";
    }

    @RequestMapping(value = "/courseCheck", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> doCheck(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        String comment = request.getParameter("comment");
        int agree = Integer.parseInt(request.getParameter("agree"));
        CourseCheckVO courseCheckVO = new CourseCheckVO();
        courseCheckVO.setId(id);
        courseCheckVO.setComment(comment);
        courseCheckVO.setAgree(agree);

        LocalDateTime checkTime = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        String checkTimeStr = checkTime.format(format);

        courseCheckVO.setCheckTime(checkTimeStr);

        System.out.println("from ManagerController: " + courseCheckVO);

        managerVOMangerImpl.checkCourse(courseCheckVO);

        Map<String, Object> map = new HashMap<>();
        map.put("success", true);

        return map;
    }

    @RequestMapping(value = "/finance", method = RequestMethod.GET)
    public String getFinancePage(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        if(session == null) {
            return "redirect: /admin/login";
        }
        ManagerInfoVO managerInfoVO = (ManagerInfoVO) session.getAttribute("managerInfoVO");
        List<FinanceCheckVO> financeCheckVOList = managerVOMangerImpl.getFinanceCheckUnchecked(managerInfoVO.getId());

        model.addAttribute("uncheckedList", financeCheckVOList);

        return "manager/financePage";
    }

    @RequestMapping(value = "/financeCheck/agree", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> doFinanceAgree(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));

        Map<String, Object> map = new HashMap<>();

        boolean r = managerVOMangerImpl.agreeFinance(id);
        if(r) {
            map.put("success", true);
        }
        else {
            map.put("success", false);
        }
        return map;
    }

    @RequestMapping(value = "/financeCheck/disagree", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> doFinanceDisagree(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));

        Map<String, Object> map = new HashMap<>();

        boolean r = managerVOMangerImpl.disagreeFinance(id);
        if(r) {
            map.put("success", true);
        }
        else {
            map.put("success", false);
        }
        return map;
    }

    @RequestMapping(value = "/course/update/agree", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> doUpdateAgree(HttpServletRequest request) {
        int checkId = Integer.parseInt(request.getParameter("checkId"));
        int cid = Integer.parseInt(request.getParameter("cid"));
        double price = Double.parseDouble(request.getParameter("price"));
        int updateId = Integer.parseInt(request.getParameter("updateId"));
        String cname = request.getParameter("cname");
        String description = request.getParameter("description");
        Map<String, Object> map = new HashMap<>();
        managerVOMangerImpl.agreeUpdate(checkId, updateId, cid, cname, description, price);
        map.put("success", true);
        return map;
    }

    @RequestMapping(value = "/course/update/disagree", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> doUpdateDisagree(HttpServletRequest request) {
        int checkId = Integer.parseInt(request.getParameter("checkId"));
        int updateId = Integer.parseInt(request.getParameter("updateId"));
        int cid = Integer.parseInt(request.getParameter("cid"));
        Map<String, Object> map = new HashMap<>();
        managerVOMangerImpl.disagreeUpdate(checkId, updateId, cid );
        map.put("success", true);
        return map;
    }
}