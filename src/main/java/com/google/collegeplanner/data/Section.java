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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Section {
  private String sectionId;
  private String courseId;
  private String waitlist;
  private int openSeats;
  private int seats;
  private String[] instructors;
  private Meeting[] meetings;

  public Section(String sectionId, String courseId, String waitlist, int openSeats, int seats,
      String[] instructors, Meeting[] meetings) {
    this.sectionId = sectionId;
    this.courseId = courseId;
    this.waitlist = waitlist;
    this.openSeats = openSeats;
    this.seats = seats;
    this.instructors = instructors;
    this.meetings = meetings;
  }

  public Section(JSONObject json) {
    this((String) json.get("section_id"), (String) json.get("course_id"),
        (String) json.get("waitlist")(int) json.get("open_seats"), (int) json.get("seats"),
        (String[]) ((JSONArray) json.get("instructors")).toArray(),
        new Meeting[((JSONArray) json.get("meetings")).toArray().length]);

    for (int i = 0; i < meetings.length; i++) {
      meetings[i] = new Meeting((JSONObject) ((JSONArray) json.get("meetings")).toArray()[i]);
    }
  }

  // This method returns true if the two sections conflict with each other and
  // returns false if they do not conflict.
  public boolean conflictsWith(Section other) {
    for (Meeting meeting : meetings) {
      for (Meeting otherMeeting : other.getMeetings()) {
        if (meeting.conflictsWith(otherMeeting)) {
          return true;
        }
      }
    }
    return false;
  }

  // Getter Methods

  public String getSectionId() {
    return sectionId;
  }

  public String getCourseId() {
    return courseId;
  }

  public String getWaitlist() {
    return waitlist;
  }

  public int getOpenSeats() {
    return openSeats;
  }

  public int getSeats() {
    return seats;
  }

  public String[] getInstructors() {
    return instructors;
  }

  public Meeting[] getMeetings() {
    return meetings;
  }

  @Override
  public String toString() {
    String output = "";

    output += sectionId;

    output += "\nMeetings\n";

    for (Meeting meeting : meetings) {
      output += meeting.toString() + "\n";
    }

    return output;
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    JSONArray instructorsArray = new JSONArray();
    JSONArray meetingsArray = new JSONArray();
    instructorsArray.addAll(instructors);
    for (Meeting meeting : meetings) {
      meetingsArray.add(meeting.toJSON());
    }
    json.put("section_id", sectionId);
    json.put("course_id", courseId);
    json.put("waitlist", waitlist);
    json.put("open_seats", openSeats);
    json.put("seats", seats);
    json.put("instructors", instructorsArray);
    json.put("meetings", meetingsArray);

    return json;
  }
}
