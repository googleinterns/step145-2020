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

import static com.google.common.truth.Truth.assertThat;

import java.text.ParseException;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests SemesterScheduler */
@RunWith(JUnit4.class)
public final class SemesterSchedulerTest {
  private Meeting MWFMorning;
  private Meeting TuThuMorning;
  private Meeting MWFLateMorning;
  private Meeting TuThuLateMorning;
  private Meeting MWFAfternoon;
  private Meeting TuThuAfternoon;
  private Meeting MWFLateAfternoon;
  private Meeting TuThuLateAfternoon;
  private Meeting MWAllDay;
  private Meeting TuThuAllDay;
  private Meeting fridayOnlyAfternoon;
  private SemesterScheduler scheduler;
  private ArrayList<Section> compSciClasses;
  private ArrayList<Section> englishClasses;
  private ArrayList<Section> mathClasses;
  private ArrayList<Section> chemClasses;
  private ArrayList<ArrayList<Section>> allClasses;

  @Before
  public void before() throws ParseException {
    String TIME_0900AM = "9:00am";
    String TIME_1000AM = "10:00am";
    String TIME_1100AM = "11:00am";
    String TIME_1200PM = "12:00pm";
    String TIME_0100PM = "1:00pm";
    String TIME_0200PM = "2:00pm";
    String TIME_0300PM = "3:00pm";
    String TIME_0400PM = "4:00pm";
    MWFMorning = new Meeting("MWF", "Room 5", "Building 2", TIME_0900AM, TIME_1000AM);
    TuThuMorning = new Meeting("TuThu", "Room 2", "Building 1", TIME_0900AM, TIME_1000AM);
    MWFLateMorning = new Meeting("MWF", "Room 2", "Building 3", TIME_1100AM, TIME_1200PM);
    TuThuLateMorning = new Meeting("TuThu", "Room 7", "Building 2", TIME_1100AM, TIME_1200PM);
    MWFAfternoon = new Meeting("MWF", "Room 1", "Building 1", TIME_0100PM, TIME_0200PM);
    TuThuAfternoon = new Meeting("TuThu", "Room 3", "Building 6", TIME_0100PM, TIME_0200PM);
    MWFLateAfternoon = new Meeting("MWF", "Room 1", "Building 1", TIME_0300PM, TIME_0400PM);
    TuThuLateAfternoon = new Meeting("MWF", "Room 1", "Building 1", TIME_0300PM, TIME_0400PM);
    MWAllDay = new Meeting("MW", "Room 2", "Building 10", TIME_0900AM, TIME_0200PM);
    TuThuAllDay = new Meeting("TuThu", "Room 14", "Building 1", TIME_0900AM, TIME_0200PM);
    fridayOnlyAfternoon = new Meeting("F", "Room 1", "Building 9", TIME_0100PM, TIME_0200PM);
    compSciClasses = new ArrayList<Section>();
    englishClasses = new ArrayList<Section>();
    mathClasses = new ArrayList<Section>();
    chemClasses = new ArrayList<Section>();
    allClasses = new ArrayList<ArrayList<Section>>();
    allClasses.add(compSciClasses);
    allClasses.add(englishClasses);
    allClasses.add(mathClasses);
    allClasses.add(chemClasses);
  }

  @Test
  public void oneMeetingPerSectionOnePossibleSchedule() throws Exception {
    compSciClasses.add(new Section(
        "CMSC101 (MWF Morning)", "CMSC101", null, "10", "30", null, new Meeting[] {MWFMorning}));

    englishClasses.add(new Section("ENGL101 (TuThu Morning)", "ENGL101", null, "10", "30", null,
        new Meeting[] {TuThuMorning}));

    mathClasses.add(new Section("MATH101 (MWF Afternoon)", "MATH101", null, "10", "30", null,
        new Meeting[] {MWFAfternoon}));

    chemClasses.add(new Section("CHEM101 (TuThu Late Morning)", "CHEM101", null, "10", "30", null,
        new Meeting[] {TuThuLateMorning}));

    scheduler = new SemesterScheduler(allClasses);

    ArrayList<Schedule> expectedSchedules = new ArrayList<Schedule>();
    expectedSchedules.add(new Schedule());

    expectedSchedules.get(0).addClass(compSciClasses.get(0));
    expectedSchedules.get(0).addClass(englishClasses.get(0));
    expectedSchedules.get(0).addClass(mathClasses.get(0));
    expectedSchedules.get(0).addClass(chemClasses.get(0));

    Assert.assertEquals(expectedSchedules, scheduler.getPossibleSchedules());
  }

  @Test
  public void oneMeetingPerSectionNoPossibleSchedules() throws Exception {
    compSciClasses.add(new Section(
        "CMSC101 (MW All Day)", "CMSC101", null, "10", "30", null, new Meeting[] {MWAllDay}));

    englishClasses.add(new Section("ENGL101 (TuThu Morning)", "ENGL101", null, "10", "30", null,
        new Meeting[] {TuThuMorning}));

    mathClasses.add(new Section("MATH101 (MWF Afternoon)", "MATH101", null, "10", "30", null,
        new Meeting[] {MWFAfternoon}));

    chemClasses.add(new Section("CHEM101 (TuThu Late Morning)", "CHEM101", null, "10", "30", null,
        new Meeting[] {TuThuLateMorning}));

    scheduler = new SemesterScheduler(allClasses);

    Assert.assertTrue(scheduler.getPossibleSchedules().isEmpty());
  }

  @Test
  public void multipleMeetingsPerSectionOnePossibleSchedule() throws Exception {
    compSciClasses.add(new Section("CMSC101 (MWF Morning and Friday Afternoon)", "CMSC101", null,
        "10", "30", null, new Meeting[] {MWFMorning, fridayOnlyAfternoon}));

    englishClasses.add(new Section("ENGL101 (TuThu Morning, TuThuAfternoon)", "ENGL101", null, "10",
        "30", null, new Meeting[] {TuThuMorning, TuThuAfternoon}));

    mathClasses.add(new Section("MATH101 (MWF Late Morning)", "MATH101", null, "10", "30", null,
        new Meeting[] {MWFLateMorning}));

    chemClasses.add(new Section("CHEM101 (TuThu Late Morning)", "CHEM101", null, "10", "30", null,
        new Meeting[] {TuThuLateMorning}));

    scheduler = new SemesterScheduler(allClasses);

    ArrayList<Schedule> expectedSchedules = new ArrayList<Schedule>();
    expectedSchedules.add(new Schedule());

    expectedSchedules.get(0).addClass(compSciClasses.get(0));
    expectedSchedules.get(0).addClass(englishClasses.get(0));
    expectedSchedules.get(0).addClass(mathClasses.get(0));
    expectedSchedules.get(0).addClass(chemClasses.get(0));

    Assert.assertEquals(expectedSchedules, scheduler.getPossibleSchedules());
  }

  @Test
  public void multipleMeetingsPerSectionMultiplePossibleSchedules() throws Exception {
    compSciClasses.add(new Section("0CMSC101 (MWF Morning and Friday Afternoon)", "CMSC101", null,
        "10", "30", null, new Meeting[] {MWFMorning, fridayOnlyAfternoon}));

    compSciClasses.add(new Section("1CMSC101 (TuThuLateAfternoon)", "CMSC101", null, "10", "30",
        null, new Meeting[] {TuThuLateAfternoon}));

    englishClasses.add(new Section("0ENGL101 (TuThu Morning, TuThuAfternoon)", "ENGL101", null,
        "10", "30", null, new Meeting[] {TuThuMorning, TuThuAfternoon}));

    mathClasses.add(new Section("0MATH101 (MWF Late Morning)", "MATH101", null, "10", "30", null,
        new Meeting[] {MWFLateMorning}));
    mathClasses.add(new Section("1MATH101 (MWF Afternoon)", "MATH101", null, "10", "30", null,
        new Meeting[] {MWFAfternoon}));

    chemClasses.add(new Section("0CHEM101 (TuThu Late Morning)", "CHEM101", null, "10", "30", null,
        new Meeting[] {TuThuLateMorning}));

    scheduler = new SemesterScheduler(allClasses);

    ArrayList<Schedule> expectedSchedules = new ArrayList<Schedule>();
    expectedSchedules.add(new Schedule());
    expectedSchedules.add(new Schedule());
    expectedSchedules.add(new Schedule());

    expectedSchedules.get(0).addClass(compSciClasses.get(0));
    expectedSchedules.get(0).addClass(englishClasses.get(0));
    expectedSchedules.get(0).addClass(mathClasses.get(0));
    expectedSchedules.get(0).addClass(chemClasses.get(0));

    expectedSchedules.get(1).addClass(compSciClasses.get(1));
    expectedSchedules.get(1).addClass(englishClasses.get(0));
    expectedSchedules.get(1).addClass(mathClasses.get(1));
    expectedSchedules.get(1).addClass(chemClasses.get(0));

    expectedSchedules.get(2).addClass(compSciClasses.get(1));
    expectedSchedules.get(2).addClass(englishClasses.get(0));
    expectedSchedules.get(2).addClass(mathClasses.get(0));
    expectedSchedules.get(2).addClass(chemClasses.get(0));

    assertThat(expectedSchedules).containsExactlyElementsIn(scheduler.getPossibleSchedules());
  }
}
