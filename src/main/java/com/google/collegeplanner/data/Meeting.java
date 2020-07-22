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
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Meeting {
  private ArrayList<DayOfWeek> days;
  private String room;
  private String building;
  private int startTime;
  private int endTime;

  public Meeting(String days, String room, String building, 
  int startTime, int endTime) throws ParseException {
    this.days = new ArrayList<DayOfWeek>();
    this.room = room;
    this.building = building;
    this.startTime = startTime;
    this.endTime = endTime;

    if (days.toUpperCase().contains("M")) {
      this.days.add(DayOfWeek.MONDAY);
    }
    if (days.toUpperCase().contains("TU")) {
      this.days.add(DayOfWeek.TUESDAY);
    }
    if (days.toUpperCase().contains("W")) {
      this.days.add(DayOfWeek.WEDNESDAY);
    }
    if (days.toUpperCase().contains("TH")) {
      this.days.add(DayofWeek.THURSDAY);
    }
    if (days.toUpperCase().contains("F")) {
      this.days.add(DayOfWeek.FRIDAY);
    }
    if (days.toUpperCase().contains("SA") || days.toUpperCase().contains("SU")) {
      throw new ParseException("Invalid Day, no Weekends on Academic Calendar");
    }
  }

  public Meeting(JSONObject json) throws ParseException {
    this((String) json.get("days"), (String) json.get("room"), (String) json.get("building"),
        (int) json.get("start_time"), (int) json.get("end_time"));
  }

  public boolean conflictsWith(Meeting other) {
    // Checks to see if the two meetings occur on the same day
    // If not, no need to check the time as they cannot conflict
    boolean canSkip = true;
    for (String day : other.getDays()) {
      if (days.contains(day)) {
        canSkip = false;
      }
    }

    if (canSkip) {
      return false;
    }

    if (startTime > other.getStartTime()) {
      if (startTime > other.getEndTime()) {
        return false;
      }
    } else {
      if (endTime < other.getStartTime()) {
        return false;
      }
    }

    return true;
  }

  public ArrayList<DayOfWeek> getDays() {
    return days;
  }

  public String getRoom() {
    return room;
  }

  public String getBuilding() {
    return building;
  }

  public int getStartTime() {
    return startTime;
  }

  public int getEndTime() {
    return endTime;
  }

  @Override
  public String toString() {
    String toString = "";
    for (DayOfWeek str : days) {
      toString += str + " ";
    }

    toString += "From " + Integer.toString(startTime) + " to " + Integer.toString(endTime);

    return toString;
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    String daysString = "";

    if(days.contain(DayOfWeek.MONDAY)) {
      daysString += "M";
    }
    if(days.contain(DayOfWeek.TUESDAY)) {
      daysString += "TU";
    }
    if(days.contain(DayOfWeek.WEDNESDAY)) {
      daysString += "W";
    }
    if(days.contain(DayOfWeek.THURSDAY)) {
      daysString += "TH";
    }
    if(days.contain(DayOfWeek.FRIDAY)) {
      daysString += "F";
    }

    json.put("days", daysString);
    json.put("room", room);
    json.put("building", building);
    json.put("start_time", startTime);
    json.put("end_time", endTime);
    return json;
  }
}
