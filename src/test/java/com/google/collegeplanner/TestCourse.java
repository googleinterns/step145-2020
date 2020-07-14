package com.google.collegeplanner.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests Course */
@RunWith(JUnit4.class)
public final class TestCourse {
  private String[] gradingSystem;
  private Course course;

  @Before
  public void before() {
    gradingSystem = new String[] {"Regular", "Pass-Fail"};
    course = new Course("CMSC101", "Introduction to Computer Science", "202008", 4, "CMSC",
        "Introductory class to Computer Science", gradingSystem, null, null, null, null, null);
  }

  /*
   * This method tests the getSemesterSeason() method of the Class class
   */
  @Test
  public void getSemesterSeasonTest() {
    String actual = course.getSemesterSeason();
    String expected = "Fall";
    Assert.assertEquals(expected, actual);
  }

  /*
   * This method tests the getSemesterYear() method of the Class class
   */
  @Test
  public void getSemesterYearTest() {
    int actual = course.getSemesterYear();
    int expected = 2020;
    Assert.assertEquals(expected, actual);
  }
}
