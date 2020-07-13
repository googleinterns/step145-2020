package com.google.collegeplanner.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests Class */
@RunWith(JUnit4.class)
public final class TestClass {
  private static final String[] GRADING_SYSTEM = new String[]{"Regular", "Pass-Fail"};
  private static final Class CMSC101 = new Class("CMSC101", "Introduction to Computer Science", 
  "202008", 4, "CMSC", "Introductory class to Computer Science", GRADING_SYSTEM, 
  null, null, null, null, null);

  // This method tests the getSemesterSeason() method of the Class class
  @Test
  public void getSemesterSeasonTest() {
    String actual = CMSC101.getSemesterSeason();
    String expected = "Fall";
    Assert.assertEquals(expected, actual);
  }

  // This method tests the getSemesterYear() method of the Class class
  @Test
  public void getSemesterYearTest() {
    int actual = CMSC101.getSemesterYear();
    int expected = 2020;
    Assert.assertEquals(expected, actual);
  }
}