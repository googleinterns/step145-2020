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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests Schedule */
@RunWith(JUnit4.class)
public final class ScheduleTest {
  private Section compSciClass;
  private Section compSciClass2;
  private Section mathClass;
  private Section mathClass2;
  private Section chemClass;
  private Section chemClass2;
  private Section englishClass;
  private Section englishClass2;

  @Before
  public void before() throws ParseException {
    String TIME_0900AM = "9:00am";
    String TIME_1000AM = "10:00am";
    String TIME_1030AM = "10:30am";
    String TIME_1100AM = "11:00am";
    String TIME_1200PM = "12:00pm";
    String TIME_0100PM = "1:00pm";
    String TIME_0200PM = "2:00pm";
    Meeting MWFMorning = new Meeting("MWF", "Room 5", "Building 2", TIME_1000AM, TIME_1100AM);
    Meeting TuThuMorning = new Meeting("TuThu", "Room 2", "Building 1", TIME_1000AM, TIME_1100AM);
    Meeting MWFAfternoon = new Meeting("MWF", "Room 1", "Building 1", TIME_1200PM, TIME_0100PM);
    Meeting TuThuAfternoon = new Meeting("TuThu", "Room 3", "Building 6", TIME_1200PM, TIME_0100PM);
    Meeting fridayLateMorning = new Meeting("F", "Room 8", "Building 3", TIME_1030AM, TIME_1200PM);
    Meeting TuThuAllDay = new Meeting("TuThu", "Room 4", "Building 5", TIME_0900AM, TIME_0200PM);
    Meeting MWFLateAfternoon = new Meeting("MWF", "Room 7", "Building 8", TIME_0100PM, TIME_0200PM);
    compSciClass = new Section(
        "CMSC101-0101", "CMSC101", null, "10", "30", null, new Meeting[] {MWFAfternoon});
    compSciClass2 = new Section(
        "CMSC101-0201", "CMSC101", null, "14", "30", null, new Meeting[] {fridayLateMorning});
    mathClass = new Section(
        "MATH140-0101", "MATH140", null, "15", "20", null, new Meeting[] {TuThuAfternoon});
    mathClass2 =
        new Section("MATH140-0201", "MATH140", null, "4", "20", null, new Meeting[] {TuThuMorning});
    chemClass = new Section(
        "CHEM135-0101", "CHEM135", null, "130", "200", null, new Meeting[] {MWFMorning});
    chemClass2 = new Section(
        "CHEM135-0201", "CHEM135", null, "73", "200", null, new Meeting[] {TuThuAllDay});
    englishClass = new Section(
        "ENGL101-0101", "ENGL101", null, "5", "20", null, new Meeting[] {TuThuAfternoon});
    englishClass2 = new Section(
        "ENGL101-0201", "ENGL101", null, "19", "20", null, new Meeting[] {MWFLateAfternoon});
  }

  @Test
  public void noConflicts() {
    ArrayList<Section> sections = new ArrayList<Section>();
    Schedule schedule = new Schedule();
    sections.add(compSciClass);
    sections.add(chemClass);
    sections.add(englishClass);
    sections.add(mathClass2);

    for (Section section : sections) {
      Assert.assertTrue(schedule.addClass(section));
    }
  }

  @Test
  public void oneConflict() {
    ArrayList<Section> sections = new ArrayList<Section>();
    Schedule schedule = new Schedule();
    sections.add(compSciClass);
    sections.add(chemClass);
    sections.add(englishClass);
    for (Section section : sections) {
      Assert.assertTrue(schedule.addClass(section));
    }
    Assert.assertFalse(schedule.addClass(mathClass));
  }

  @Test
  public void overlappingClasses() {
    Schedule schedule = new Schedule();
    schedule.addClass(chemClass);
    Assert.assertFalse(schedule.addClass(compSciClass2));
    // Checking the reverse case as well
    schedule = new Schedule();
    schedule.addClass(compSciClass2);
    Assert.assertFalse(schedule.addClass(chemClass));
  }

  @Test
  public void nestedClasses() {
    Schedule schedule = new Schedule();
    schedule.addClass(chemClass2);
    Assert.assertFalse(schedule.addClass(englishClass));
    // Checking the reverse case as well
    schedule = new Schedule();
    schedule.addClass(englishClass);
    Assert.assertFalse(schedule.addClass(chemClass2));
  }

  @Test
  public void backToBackClasses() {
    Schedule schedule = new Schedule();
    schedule.addClass(compSciClass);
    Assert.assertTrue(schedule.addClass(englishClass2));
    // Checking the reverse case as well
    schedule = new Schedule();
    schedule.addClass(englishClass2);
    Assert.assertTrue(schedule.addClass(compSciClass));
  }

  @Test
  public void removeClass() {
    Schedule actual = new Schedule();
    Assert.assertTrue(actual.addClass(compSciClass));
    Assert.assertTrue(actual.addClass(englishClass));
    actual.removeLastClass();

    Schedule expected = new Schedule();
    Assert.assertTrue(expected.addClass(compSciClass));
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void removeSizeZero() {
    Schedule schedule = new Schedule();
    schedule.removeLastClass();
    Assert.assertTrue(schedule.getSections().isEmpty());
  }
}
