package tc.data.test;

public class StudentWithBLOBs extends Student {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column student.password
     *
     * @mbggenerated Sat Feb 25 15:12:43 CST 2017
     */
    private String password;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column student.name
     *
     * @mbggenerated Sat Feb 25 15:12:43 CST 2017
     */
    private String name;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column student.password
     *
     * @return the value of student.password
     *
     * @mbggenerated Sat Feb 25 15:12:43 CST 2017
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column student.password
     *
     * @param password the value for student.password
     *
     * @mbggenerated Sat Feb 25 15:12:43 CST 2017
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column student.name
     *
     * @return the value of student.name
     *
     * @mbggenerated Sat Feb 25 15:12:43 CST 2017
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column student.name
     *
     * @param name the value for student.name
     *
     * @mbggenerated Sat Feb 25 15:12:43 CST 2017
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}