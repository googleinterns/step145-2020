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
  public void getSemesterSeasonSuccess() {
    course = new Course("CMSC101", "Introduction to Computer Science", "202008", 4, "CMSC",
        "Introductory class to Computer Science", gradingSystem, null, null, null, null, null);
    try {
      Assert.assertEquals("Fall", course.getSemesterSeason());
    } catch (Exception e) {
      Assert.fail();
    }
  }

  /*
   * This method tests the getSemesterYear() method of the Class class
   */
  @Test
  public void getSemesterYearSuccess() {
    course = new Course("CMSC101", "Introduction to Computer Science", "202008", 4, "CMSC",
        "Introductory class to Computer Science", gradingSystem, null, null, null, null, null);
    try {
      Assert.assertEquals(2020, course.getSemesterYear());
    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void invalidMonth() {
    course = new Course("CMSC101", "Introduction to Computer Science", "202003", 4, "CMSC",
        "Introductory class to Computer Science", gradingSystem, null, null, null, null, null);
    try {
      course.getSemesterSeason();
      Assert.fail();
    } catch (Exception e) {
      // Passes Test by throwing exception as expected
    }
  }

  @Test
  public void invalidFormat() {
    course = new Course("CMSC101", "Introduction to Computer Science", "03", 4, "CMSC",
        "Introductory class to Computer Science", gradingSystem, null, null, null, null, null);
    try {
      course.getSemesterYear();
      Assert.fail();
    } catch (Exception e) {
      // Passes Test by throwing exception as expected
    }
  }
}
