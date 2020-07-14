package com.google.collegeplanner.data;

public class Course {
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
