// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.package com.google.collegeplanner.data;
package com.google.collegeplanner.data;

import java.lang.InstantiationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Course {
  /*
   * courseId represents the name of the course that
   * can be found on the course catalog. Example, ENGL101
   */
  private String courseId;
  /*
   * name represents the full name of the course.
   * Example, Academic Writing
   */
  private String name;
  /*
   * semester represents the 4 digit year followed by
   * the two digit month when the course starts. Example, 202008
   */
  private String semester;
  /*
   * credits represents the number of credits the course is worth.
   */
  private int credits;
  /*
   * deptId represents the 4 character ID of the
   * department that the class falls into. Example, ENGL
   */
  private String deptId;
  /*
   * description represents the description of
   * the class found on the course catalog.
   */
  private String description;
  /*
   * gradingMethod represents the list of possible grading
   * methods students can opt for. Example, regular, pass-fail.
   */
  private String[] gradingMethod;
  /*
   * coreqs represents the corequsite classes for this
   * course. Example, BIO106 is the lab for BIO101 and
   * must be taken together
   */
  private String coreqs;
  /*
   * prereqs represents the prerequsite classes for
   * this course. Example, MATH101, MATH201, and
   * either MATH105 or MATH115.
   */
  private String prereqs;
  /*
   * restrictions represents the restrictions placed on the
   * registration of this course. Example, This
   * class is only avaiable to incoming Freshman.
   */
  private String restrictions;
  /*
   * additionalInfo represents the additional information
   * for this course found on the course catalog.
   */
  private String additionalInfo;
  /*
   *creditGrantedFor represents the classes this course
   * grants credit for. Example, THET285 grants credit for COMM107
   */
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
    if (semester.length() != 6) {
      return "Invalid Semester Format";
    }
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
      case 12:
        return "Winter";
      default:
        return "Invalid Month";
    }
  }

  /*
   * Returns the year of the semester, which is the first four digits of the
   * semester String as an int
   */
  public int getSemesterYear() {
    if (semester.length() != 6) {
      return 0;
    }
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
