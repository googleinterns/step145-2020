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

import com.google.collegeplanner.data.Section;
import com.google.collegeplanner.data.SemesterScheduler;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/** Servlet that returns a list of possible schedules containing the given courses.*/
@WebServlet("/api/scheduler")
public class SchedulerServlet extends HttpServlet {
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
      body = getBody(request);
      selectedClasses = (JSONArray) body.get("selectedClasses");
    } catch (NumberFormatException | ClassCastException | ParseException | NullPointerException e) {
      respondWithError(
          "Invalid body for POST request.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }

    prepareLists(selectedClasses);

    JSONArray schedules = getSchedules();
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(schedules));
  }

  /**
   * Gets the JSON Representation of the body of the POST request
   */
  private JSONObject getBody(HttpServletRequest request)
      throws IOException, ParseException, NullPointerException {
    String strBody =
        request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    JSONParser parser = new JSONParser();
    return (JSONObject) parser.parse(strBody);
  }
  
  /**
   * This method loads the courses and courseList ArrayLists with correct
   * information. courses is loaded with the lists of sections for each
   * course while courseList is loaded with the courseID's of the selected
   * courses.
   */
  private void prepareLists(JSONArray classes) {
    String courseId;
    URI uri;
    JSONArray jsonArray;
    ApiUtil apiUtil = new apiUtil();

    courseList = new ArrayList<String>();
    courses = new ArrayList<ArrayList<Section>>();
    for(JSONArray obj : (JSONArray) classes.toArray()) {
      courseId = (String) ((JSONObject) obj).get("course_id");
      courseList.add(courseId);
      try {
        uri = new URI("https://api.umd.io/v1/courses/" + courseId + "/sections");
      } catch (URISyntaxException e) {
        respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        return;
      }

      jsonArray = apiUtil.getJsonArray(uri);
      if (jsonArray == null) {
        respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        return;
      }
      courses.add(convertJSONArraytoArrayList(jsonArray));
    }
  }
  
  /**
   * Returns a JSONArray representing the schedules the
   * SemesterScheduler returns.
   */
  private JSONArray getSchedules() { 
    JSONArray schedulesJson = new JSONArray();
    SemesterScheduler scheduler = new scheduler(courses);
    ArrayList<Schedule> possibleSchedules = scheduler.getPossibleSchedules();

    for(Schedule schedule : possibleSchedules) {
      schedulesJson.add(schedule.toJSON());
    }

    return schedulesJson;
  }

  /**
   * Converts a given JSONArray of sections into an ArrayList of
   * sections.
   */
  private ArrayList<Section> convertJSONArraytoArrayList(JSONArray json) {
    ArrayList<Section> list = new ArrayList<Section>(); 
    for(Object obj : json) {
      list.add(new Section((JSONObject) obj));
    }
    return list;
  }
}
