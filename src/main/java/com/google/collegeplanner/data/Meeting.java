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

/*
 * This Meeting class depicts a session a class takes place during. For example, a
 * bio class can have a discussion on wednesday afternoons and also
 * a seminar every weekday in the morning. Those sessions, the seminar and
 * discussion, are two separate meetings for the same class.
 */
public class Meeting {
  private ArrayList<DayOfWeek> days;
  private String room;
  private String building;
  private int startTime;
  private int endTime;

  public Meeting(String days, String room, String building, String startTime, String endTime)
      throws ParseException {
    if (room != "") {
      this.room = room;
    }
    if (building != "") {
      this.building = building;
    }
    if (startTime != "") {
      this.startTime = timeInMins(startTime);
    }
    if (endTime != "") {
      this.endTime = timeInMins(endTime);
    }

    if (days == "") {
      return;
    }
    this.days = new ArrayList<DayOfWeek>();
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
      this.days.add(DayOfWeek.THURSDAY);
    }
    if (days.toUpperCase().contains("F")) {
      this.days.add(DayOfWeek.FRIDAY);
    }
    if (days.toUpperCase().contains("SA") || days.toUpperCase().contains("SU")) {
      // Since this is an academic scheduler, meetings cannot happen on weekends.
      throw new ParseException("Invalid Day, no Weekends on Academic Calendar", 0);
    }
  }

  public Meeting(JSONObject json) throws ParseException {
    this((String) json.get("days"), (String) json.get("room"), (String) json.get("building"),
        (String) json.get("start_time"), (String) json.get("end_time"));
  }

  public boolean conflictsWith(Meeting other) {
    // Checks to see if the two meetings occur on the same day
    // If not, no need to check the time as they cannot conflict
    boolean canSkip = true;
    for (DayOfWeek day : other.getDays()) {
      if (days.contains(day)) {
        canSkip = false;
        break;
      }
    }

    if (canSkip) {
      return false;
    }

    // Checks to see if this meeting's start time happens after the other meeting's start time
    if (startTime > other.getStartTime()) {
      // Checks to see if this meeting's start time happens after the other meeting's end time
      if (startTime >= other.getEndTime()) {
        // Two meetings do not conflict as this meeting occurs after the other meeting
        return false;
      }
    } else {
      // Checks to see if this meeting's end time happens before the other meeting's start time
      if (endTime <= other.getStartTime()) {
        // Two meetings do not conflict as this meeting occurs before the other meetings
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

  public String getReadableStartTime() {
    return timeInString(startTime);
  }

  public int getStartTime() {
    return startTime;
  }

  public String getReadableEndTime() {
    return timeInString(endTime);
  }

  public int getEndTime() {
    return endTime;
  }

  @Override
  public String toString() {
    String output = "";
    for (DayOfWeek str : days) {
      output += str + " ";
    }

    output += "From " + Integer.toString(startTime) + " to " + Integer.toString(endTime);

    return output;
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    String daysString = "";

    if (days.contains(DayOfWeek.MONDAY)) {
      daysString += "M";
    }
    if (days.contains(DayOfWeek.TUESDAY)) {
      daysString += "TU";
    }
    if (days.contains(DayOfWeek.WEDNESDAY)) {
      daysString += "W";
    }
    if (days.contains(DayOfWeek.THURSDAY)) {
      daysString += "TH";
    }
    if (days.contains(DayOfWeek.FRIDAY)) {
      daysString += "F";
    }
    // Do not need to check for Saturday or Sunday as the constructor throws an error
    // if user tries to add those days into the list.

    json.put("days", daysString);
    json.put("room", room);
    json.put("building", building);
    json.put("start_time", startTime);
    json.put("end_time", endTime);
    return json;
  }

  /**
   * Converts user readable time to total minutes after midnight
   * @param time Time in readable format (2:00pm, 11:00am)
   */
  private int timeInMins(String time) {
    String[] hourMin = time.split(":");
    int hour = Integer.parseInt(hourMin[0]);
    int mins = Integer.parseInt(hourMin[1].substring(0, 2));
    if (hourMin[1].toUpperCase().contains("P") && hour != 12) {
      hour += 12;
    }
    int hoursInMins = hour * 60;
    return hoursInMins + mins;
  }

  /**
   * Converts total minutes after midnight
   * @param time Total minutes after 00:00
   */
  private String timeInString(int time) {
    String amPm = "am";
    int hours = time / 60;
    int minutes = time % 60;
    if (hours > 12) {
      hours -= 12;
    } else if (hours == 0) {
      hours = 12;
    }
    if (hours >= 12) {
      amPm = "pm";
    }
    return (Integer.toString(hours) + ":" + Integer.toString(minutes) + amPm);
  }
}
