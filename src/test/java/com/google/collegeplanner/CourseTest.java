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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests Course */
@RunWith(JUnit4.class)
public final class CourseTest {
  private String[] gradingSystem;
  private Course course;

  @Before
  public void before() {
    gradingSystem = new String[] {"Regular", "Pass-Fail"};
  }

  /*
   * This method tests the getSemesterSeason() method of the Class class
   */
  @Test
  public void getSemesterSeasonSuccessTest() {
    course = new Course("CMSC101", "Introduction to Computer Science", "202008", 4, "CMSC",
        "Introductory class to Computer Science", gradingSystem, null, null, null, null, null);
    Assert.assertEquals("Fall", course.getSemesterSeason());
  }

  /*
   * This method tests the getSemesterYear() method of the Class class
   */
  @Test
  public void getSemesterYearSuccessTest() {
    course = new Course("CMSC101", "Introduction to Computer Science", "202008", 4, "CMSC",
        "Introductory class to Computer Science", gradingSystem, null, null, null, null, null);
    Assert.assertEquals(2020, course.getSemesterYear());
  }

  @Test
  public void invalidMonthTest() {
    course = new Course("CMSC101", "Introduction to Computer Science", "202003", 4, "CMSC",
        "Introductory class to Computer Science", gradingSystem, null, null, null, null, null);
    Assert.assertEquals("Invalid Month", course.getSemesterSeason());
  }

  @Test
  public void invalidFormatTest() {
    course = new Course("CMSC101", "Introduction to Computer Science", "03", 4, "CMSC",
        "Introductory class to Computer Science", gradingSystem, null, null, null, null, null);
    Assert.assertEquals("Invalid Semester Format", course.getSemesterSeason());
  }
}
