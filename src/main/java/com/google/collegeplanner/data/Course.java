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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
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
   * departmentId represents the 4 character ID of the
   * department that the class falls into. Example, ENGL
   */
  private String departmentId;
  /*
   * description represents the description of
   * the class found on the course catalog.
   */
  private String description;
  /*
   * corequisites represents the corequisite classes for this
   * course. Example, BIO106 is the lab for BIO101 and
   * must be taken together
   */
  private String corequisites;
  /*
   * prerequisites represents the prerequisite classes for
   * this course. Example, MATH101, MATH201, and
   * either MATH105 or MATH115.
   */
  private String prerequisites;
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

  public Course(String courseId, String name, String semester, int credits, String departmentId,
      String description, String corequisites, String prerequisites, String restrictions,
      String additionalInfo, String creditGrantedFor) {
    this.courseId = courseId;
    this.name = name;
    this.semester = semester;
    this.credits = credits;
    this.departmentId = departmentId;
    this.description = description;
    this.corequisites = corequisites;
    this.prerequisites = prerequisites;
    this.restrictions = restrictions;
    this.additionalInfo = additionalInfo;
    this.creditGrantedFor = creditGrantedFor;
  }

  public Course(JSONObject json) throws Exception {
    try {
      this.credits = Integer.parseInt((String) json.get("credits"));
    } catch (NumberFormatException e) {
      this.credits = 0;
    }

    this.courseId = (String) json.get("course_id");
    if (this.courseId == null) {
      throw new ParseException("Null course_id.", 0);
    }
    this.courseId = this.courseId.toUpperCase();

    this.name = (String) json.get("name");
    this.semester = (String) json.get("semester");
    this.departmentId = (String) json.get("department_id");
    this.description = (String) json.get("description");

    JSONObject relationships = (JSONObject) json.get("relationships");
    if (relationships == null) {
      this.corequisites = null;
      this.prerequisites = null;
      this.restrictions = null;
      this.additionalInfo = null;
      this.creditGrantedFor = null;
      return;
    }

    this.corequisites = (String) relationships.get("coreqs");
    this.prerequisites = (String) relationships.get("prereqs");
    this.restrictions = (String) relationships.get("restrictions");
    this.additionalInfo = (String) relationships.get("additional_info");
    this.creditGrantedFor = (String) relationships.get("credit_granted_for");
  }

  /*
   * Returns the season of the semester
   * The format of the semester String is the four digit year followed by
   * the two digit start month.
   */
  public String getSemesterSeason() throws ParseException {
    if (semester.length() != 6) {
      throw new ParseException("Invalid Semester Format", 0);
    }

    int startMonth;
    // startMonth stores the last two digits of the semester String as an int
    try {
      startMonth = Integer.parseInt(semester) % 100;
    } catch (NumberFormatException e) {
      throw new ParseException("Invalid Start Month Format", 4);
    }

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
        throw new ParseException("Invalid Start Month", 4);
    }
  }

  /*
   * Returns the year of the semester, which is the first four digits of the
   * semester String as an int
   */
  public int getSemesterYear() throws ParseException {
    GregorianCalendar cal = new GregorianCalendar();
    int currentYear = cal.get(GregorianCalendar.YEAR);
    int semesterYear;
    if (semester.length() != 6) {
      throw new ParseException("Invalid Semester Format", 0);
    }
    try {
      semesterYear = Integer.parseInt(semester.substring(0, 4));
      if (semesterYear > currentYear + 20 || semesterYear < currentYear - 5) {
        throw new ParseException("Invalid Year. (Too far in the future or too far in the past)", 0);
      }
    } catch (NumberFormatException e) {
      throw new ParseException("Invalid Semester Year Format", 0);
    }

    return semesterYear;
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

  public String getDepartmentId() {
    return departmentId;
  }

  public String getDescription() {
    return description;
  }

  public String getCorequisites() {
    return corequisites;
  }

  public String getPrerequisites() {
    return prerequisites;
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
