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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Schedule {
  private ArrayList<Section> sections;

  public Schedule() {
    sections = new ArrayList<Section>();
  }

  // This method adds a section to the existing schedule of sections
  // Returns true if there are no conflicts and the section has been added
  // correctly. Returns false if there is a conflict and the section cannot
  // be added into the schedule.
  public boolean addClass(Section section) {
    if (conflictsWithSchedule(section)) {
      return false;
    }

    sections.add(section);

    return true;
  }

  // This method checks to see if the provided TimeRange object conflicts
  // with the other TimeRange objects in the schedule ArrayList.
  // Method is true if there is a conflict, false otherwise.
  private boolean conflictsWithSchedule(Section section) {
    for (Section scheduledSection : sections) {
      if (scheduledSection.conflicts(section)) {
        return true;
      }
    }
    return false;
  }

  public ArrayList<Section> getSections() {
    return sections;
  }

  @Override
  public String toString() {
    String toString = "Schedule\n";

    for (Section section : sections) {
      toString += section.toString() + "\n";
    }

    return toString;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Schedule)) {
      return false;
    }

    if (((Schedule) other).getSections().size() != sections.size()) {
      return false;
    }

    for (Section section : ((Schedule) other).getSections()) {
      if (!sections.contains(section)) {
        return false;
      }
    }

    return true;
  }

  public JSONObject toJSON() {
    JSONObject json = new JSONObject();
    JSONArray array = new JSONArray();

    for (Section section : sections) {
      array.add(section.toJSON());
    }

    json.add(array);

    return json;
  }
}
