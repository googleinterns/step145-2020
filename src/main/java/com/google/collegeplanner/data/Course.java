package com.google.collegeplanner.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class Course {
  /*
   * courseId represents the name of the course that can be found on the course catalog. Example, ENGL101
   * name represents the full name of the course. Example, Academic Writing
   * semester represents the 4 digit year follow by the two digit month when the course starts. Example, 202008
   * credits represents the number of credits the course is worth. 
   * deptId represents the 4 character ID of the department that the class falls into. Example, ENGL
   * description represents the description of the class found on the course catalog.
   * gradingMethod represents the list of possible grading methods students can opt for. Example, regular, pass-fail.
   * coreqs represents the corequsite classes for this course. Example, BIO106 is the lab for BIO101 and must be taken together
   * prereqs represents the prerequsite classes for this course. Example, MATH101, MATH201, and either MATH105 or MATH115.
   * restrictions represents the restrictions placed on the registration of this course. Example, This class is only avaiable to incoming Freshman.
   * additionalInfo represents the additional information for this course found on the course catalog.
   * creditGrantedFor represents the classes this course grants credit for. Example, THET285 grants credit for COMM107
   */
  private String courseId;
  private String name;
  private String semester;
  private int credits;
  private String deptId;
  private String description;
  private String[] gradingMethod;
  private String coreqs;
  private String prereqs;
  private String restrictions;
  private String additionalInfo;
  private String creditGrantedFor;

  public Course(String courseId, String name, String semester, int credits, String deptId,
      String description, String[] gradingMethod, String coreqs, String prereqs,
      String restrictions, String additionalInfo, String creditGrantedFor) {
    this.courseId = courseId;
    this.name = name;
    this.semester = semester;
    this.credits = credits;
    this.deptId = deptId;
    this.description = description;
    this.gradingMethod = gradingMethod;
    this.coreqs = coreqs;
    this.prereqs = prereqs;
    this.restrictions = restrictions;
    this.additionalInfo = additionalInfo;
    this.creditGrantedFor = creditGrantedFor;
  }

  public Course(JSONObject json) {
    this((String) json.get("course_id"), (String) json.get("name"), (String) json.get("semester"),
        (int) json.get("credits"), (String) json.get("dept_id"), (String) json.get("description"),
        null, (String) json.get("coreqs"), (String) json.get("prereqs"),
        (String) json.get("restrictions"), (String) json.get("additional_info"),
        (String) json.get("credit_granted_for"));

    this.gradingMethod = (String[]) ((JSONArray) json.get("grading_method")).toArray();
  }

  /*
   * Returns the season of the semester
   * The format of the semester String is the four digit year followed by
   * the two digit start month.
   */
  public String getSemesterSeason() {
    // startMonth stores the last two digits of the semester String as an int
    int startMonth = Integer.parseInt(semester) % 100;

    switch (startMonth) {
      case 1:
      case 2:
        return "Spring";
      case 6:
        return "Summer I";
      case 7:
        return "Summer II";
      case 8:
      case 9:
        return "Fall";
      default:
        return "Winter";
    }
  }

  /*
   * Returns the year of the semester, which is the first four digits of the
   * semester String as an int
   */
  public int getSemesterYear() {
    return Integer.parseInt(semester.substring(0, 4));
  }

  // Getter Methods

  public String getCourseId() {
    return courseId;
  }

  public String getName() {
    return name;
  }

  public String getSemester() {
    return semester;
  }

  public int getCredits() {
    return credits;
  }

  public String getDeptId() {
    return deptId;
  }

  public String getDescription() {
    return description;
  }

  public String[] getGradingMethod() {
    return gradingMethod;
  }

  public String getCoreqs() {
    return coreqs;
  }

  public String getPrereqs() {
    return prereqs;
  }

  public String getRestrictions() {
    return restrictions;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public String getCreditGrantedFor() {
    return creditGrantedFor;
  }
}
