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
  private Section mathClass1;
  private Section mathClass2;
  private Section chemClass;
  private Section englishClass;
  @Before
  public void before() {
    int TIME_0900AM = 60 * 9;
    int TIME_1000AM = 60 * 10;
    int TIME_1100AM = 60 * 11;
    int TIME_1200PM = 60 * 12;
    int TIME_0100PM = 60 * 13;
    int TIME_0200PM = 60 * 14;
    Meeting MWFMorning = new Meeting("MWF", "Room 5", "Building 2", TIME_0900AM, TIME_1000AM);
    Meeting TuThuMorning = new Meeting("TuThu", "Room 2", "Building 1", TIME_1000AM, TIME_1100AM);
    Meeting MWFAfternoon = new Meeting("MWF", "Room 1", "Building 1", TIME_1200PM, TIME_0100PM);
    Meeting TuThuAfternoon = new Meeting("TuThu", "Room 3", "Building 6", TIME_0100PM, TIME_0200PM);
    compSciClass =
        new Section("CMSC101-0101", "CMSC101", null, 10, 30, null, new Meeting[] {MWFAfternoon});
    mathClass1 =
        new Section("MATH140-0101", "MATH140", null, 15, 20, null, new Meeting[] {TuThuAfternoon});
    mathClass2 =
        new Section("MATH140-0201", "MATH140", null, 4, 20, null, new Meeting[] {TuThuMorning});
    chemClass =
        new Section("CHEM135-0101", "CHEM135", null, 130, 200, null, new Meeting[] {MWFMorning});
    englishClass =
        new Section("ENGL101-0101", "ENGL101", null, 5, 20, null, new Meeting[] {TuThuAfternoon});
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
      if (!schedule.addClass(section)) {
        Assert.fail();
      }
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
      if (!schedule.addClass(section)) {
        Assert.fail();
      }
    }
    Assert.assertFalse(schedule.addClass(mathClass1));
  }
}
