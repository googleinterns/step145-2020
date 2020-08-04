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

import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.gson.annotations.SerializedName;
import java.text.ParseException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*
 * This class represents a section of a course.
 */
public class Section {
  @SerializedName("section_id") private String sectionId;
  @SerializedName("course_id") private String courseId;
  @SerializedName("waitlist") private String waitlist;
  @SerializedName("open_seats") private int openSeats;
  @SerializedName("seats") private int seats;
  @SerializedName("instructors") private String[] instructors;
  @SerializedName("meetings") private Meeting[] meetings;

  public Section(String sectionId, String courseId, String waitlist, String openSeats, String seats,
      String[] instructors, Meeting[] meetings) throws ParseException {
    this.sectionId = sectionId;
    this.courseId = courseId;
    this.waitlist = waitlist;
    this.openSeats = Integer.parseInt(openSeats);
    this.seats = Integer.parseInt(seats);
    this.instructors = instructors;
    this.meetings = meetings;

    validate();
  }

  public Section(JSONObject json) throws ParseException {
    try {
      this.openSeats = Integer.parseInt((String) json.get("open_seats"));
    } catch (NumberFormatException e) {
      this.openSeats = 0;
    }

    try {
      this.seats = Integer.parseInt((String) json.get("seats"));
    } catch (NumberFormatException e) {
      this.seats = 0;
    }
    this.courseId = (String) json.get("course");
    this.sectionId = (String) json.get("section_id");
    this.waitlist = (String) json.get("waitlist");
    this.courseId = (String) json.get("course");

    JSONArray instructorsArray = (JSONArray) json.get("instructors");
    ArrayList<String> instructors = new ArrayList<String>();
    for (Object jsonObject : instructorsArray) {
      instructors.add((String) jsonObject);
    }
    this.instructors = instructors.toArray(new String[0]);

    JSONArray meetingsArray = (JSONArray) json.get("meetings");
    ArrayList<Meeting> meetings = new ArrayList<Meeting>();
    for (Object jsonObject : meetingsArray) {
      JSONObject meetingJson = (JSONObject) jsonObject;
      Meeting meeting = new Meeting(meetingJson);
      meetings.add(meeting);
    }
    this.meetings = meetings.toArray(new Meeting[0]);
    // TODO (naaoli): handle duplicate meetings if time allows for it
    validate();
  }

  public Section(EmbeddedEntity sectionEntity) throws ParseException {
    this.sectionId = (String) sectionEntity.getProperty("section_id");
    this.courseId = (String) sectionEntity.getProperty("course_id");
    this.waitlist = (String) sectionEntity.getProperty("waitlist");
    this.seats = ((Long) sectionEntity.getProperty("seats")).intValue();
    this.openSeats = ((Long) sectionEntity.getProperty("open_seats")).intValue();

    ArrayList<EmbeddedEntity> meetingEntities =
        (ArrayList<EmbeddedEntity>) sectionEntity.getProperty("meetings");
    if (meetingEntities == null) {
      this.meetings = null;
    } else {
      ArrayList<Meeting> meetings = new ArrayList<Meeting>();
      for (EmbeddedEntity meetingEntity : meetingEntities) {
        Meeting meeting = new Meeting(meetingEntity);
        meetings.add(meeting);
      }
      this.meetings = meetings.toArray(new Meeting[0]);
    }

    ArrayList<String> instructors = (ArrayList<String>) sectionEntity.getProperty("instructors");
    if (instructors == null) {
      this.instructors = null;
    } else {
      this.instructors = instructors.toArray(new String[0]);
    }
  }

  /*
   * Validates the sectionId parameter that is passed into the constructors.
   */
  private void validate() throws ParseException {
    if (this.sectionId == null) {
      throw new ParseException("Null section_id.", 0);
    }
    this.sectionId = this.sectionId.toUpperCase();
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
    for (String instructor : instructors) {
      instructorsArray.add(instructor);
    }
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
