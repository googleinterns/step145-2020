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
// limitations under the License.

package com.google.collegeplanner.servlets;

import com.google.collegeplanner.data.Schedule;
import com.google.collegeplanner.data.Section;
import com.google.collegeplanner.data.SemesterScheduler;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** Servlet that returns a list of possible schedules containing the given courses.*/
@WebServlet("/api/scheduler")
public class SchedulerServlet extends BaseServlet {
  private ArrayList<String> courseList;
  private ArrayList<ArrayList<Section>> courses;

  /**
   * Organizes courses from POST request into a given number of semesters
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JSONObject body;
    JSONArray selectedClasses;
    try {
      body = getPostRequestBody(request);
      selectedClasses = (JSONArray) body.get("selectedClasses");
    } catch (NumberFormatException | ClassCastException | ParseException | NullPointerException e) {
      respondWithError(
          "Invalid body for POST request.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }

    if (!prepareLists(selectedClasses, response)) {
      return;
    }

    JSONObject schedules = getSchedules();
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(schedules));
  }

  /**
   * This method loads the courses and courseList ArrayLists with correct
   * information. courses is loaded with the lists of sections for each
   * course while courseList is loaded with the courseID's of the selected
   * courses. Returns true if lists were prepared correctly, false otherwise.
   * @param classes The JSONArray of courseIds of the selected classes
   * @param response The HttpServletResponse object
   */
  private boolean prepareLists(JSONArray classes, HttpServletResponse response) throws IOException {
    String courseId;
    URI uri;
    JSONArray jsonArray;
    ApiUtil apiUtil = new ApiUtil();

    courseList = new ArrayList<String>();
    courses = new ArrayList<ArrayList<Section>>();
    for (Object obj : classes) {
      courseId = (String) obj;
      courseList.add(courseId);
      try {
        uri = new URI("https://api.umd.io/v1/courses/"
            + URLEncoder.encode(courseId, StandardCharsets.UTF_8.toString()) + "/sections");
      } catch (URISyntaxException e) {
        respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        return false;
      }

      jsonArray = apiUtil.getJsonArray(uri);
      if (jsonArray == null) {
        respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        return false;
      }
      courses.add(convertSectionJsonToSectionArrayList(jsonArray));
    }
    return true;
  }

  /**
   * Returns a JSONArray representing the schedules the
   * SemesterScheduler returns.
   */
  private JSONObject getSchedules() {
    JSONObject json = new JSONObject();
    JSONArray schedulesJson = new JSONArray();
    SemesterScheduler scheduler = new SemesterScheduler(courses);
    ArrayList<Schedule> possibleSchedules = scheduler.getPossibleSchedules();

    for (Schedule schedule : possibleSchedules) {
      schedulesJson.add(schedule.toJSON());
    }

    json.put("schedules", schedulesJson);

    return json;
  }

  /**
   * Converts a given JSONArray of sections into an ArrayList of
   * sections.
   * @param json The JSONArray of Sections that will be converted into an
   * ArrayList of Sections
   */
  private ArrayList<Section> convertSectionJsonToSectionArrayList(JSONArray json) {
    ArrayList<Section> list = new ArrayList<Section>();
    for (Object obj : json) {
      try {
        list.add(new Section((JSONObject) obj));
      } catch (java.text.ParseException e) {
        continue;
      }
    }
    return list;
  }
}
