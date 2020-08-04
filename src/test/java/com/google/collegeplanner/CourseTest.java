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
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests Course */
@RunWith(JUnit4.class)
public final class CourseTest {
  private Course course;
  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  /*
   * This method tests the getSemesterSeason() method of the Class class
   */
  @Test
  public void getSemesterSeasonSuccess() throws Exception {
    course = new Course("CMSC101", "Introduction to Computer Science", "202008", 4, "CMSC",
        "Introductory class to Computer Science", null, null, null, null, null, null);
    Assert.assertEquals("Fall", course.getSemesterSeason());
  }

  /*
   * This method tests the getSemesterYear() method of the Class class
   */
  @Test
  public void getSemesterYearSuccess() throws Exception {
    course = new Course("CMSC101", "Introduction to Computer Science", "202008", 4, "CMSC",
        "Introductory class to Computer Science", null, null, null, null, null, null);
    Assert.assertEquals(2020, course.getSemesterYear());
  }

  @Test
  public void invalidMonthNumber() throws Exception {
    course = new Course("CMSC101", "Introduction to Computer Science", "202003", 4, "CMSC",
        "Introductory class to Computer Science", null, null, null, null, null, null);
    exceptionRule.expect(ParseException.class);
    exceptionRule.expectMessage("Invalid Start Month");
    course.getSemesterSeason();
  }

  @Test
  public void invalidMonthFormat() throws Exception {
    course = new Course("CMSC101", "Introduction to Computer Science", "2020f3", 4, "CMSC",
        "Introductory class to Computer Science", null, null, null, null, null, null);
    exceptionRule.expect(ParseException.class);
    exceptionRule.expectMessage("Invalid Start Month Format");
    course.getSemesterSeason();
  }

  @Test
  public void invalidYearNumber() throws Exception {
    course = new Course("CMSC101", "Introduction to Computer Science", "177608", 4, "CMSC",
        "Introductory class to Computer Science", null, null, null, null, null, null);
    exceptionRule.expect(ParseException.class);
    exceptionRule.expectMessage("Invalid Year. (Too far in the future or too far in the past)");
    course.getSemesterYear();
  }

  @Test
  public void invalidYearFormat() throws Exception {
    course = new Course("CMSC101", "Introduction to Computer Science", "r8e108", 4, "CMSC",
        "Introductory class to Computer Science", null, null, null, null, null, null);
    exceptionRule.expect(ParseException.class);
    exceptionRule.expectMessage("Invalid Semester Year Format");
    course.getSemesterYear();
  }

  @Test
  public void invalidFormat() throws Exception {
    course = new Course("CMSC101", "Introduction to Computer Science", "03", 4, "CMSC",
        "Introductory class to Computer Science", null, null, null, null, null, null);
    exceptionRule.expect(ParseException.class);
    exceptionRule.expectMessage("Invalid Semester Format");
    course.getSemesterYear();
  }
}
