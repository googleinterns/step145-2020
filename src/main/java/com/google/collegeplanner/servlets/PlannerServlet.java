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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** Servlet that returns the mutli-semester plan for the given courses.*/
@WebServlet("/api/planner")
public class PlannerServlet extends HttpServlet {

  static HashMap<String, HashSet<String>> prerequisiteGraph;
  static HashMap<String, HashSet<String>> postreqGraph;
  static HashMap<String, Integer> credits;

  /**
   * Organizes courses into the given number of semesters
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO(ramyabuva): Implement algorithm to split into the number of semesters
    JSONObject body;
    try {
      body = getBody(request);
    } catch (ParseException | NullPointerException e) {
      respondWithError(
          "Invalid body for POST request.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }
    JSONArray selectedClasses = (JSONArray) body.get("selectedClasses");
    // Returns the courses in a single semester
    ArrayList<HashSet<String>> semesters = new ArrayList<>();
    HashSet<String> semester = new HashSet<>();
    for (Object course : selectedClasses) {
      semester.add((String) ((JSONObject) course).get("course_id"));
    }
    semesters.add(semester);

    response.getWriter().println(new Gson().toJson(semesters));
  }

  private static JSONObject getBody(HttpServletRequest request)
      throws IOException, ParseException, NullPointerException {
    String strBody =
        request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    JSONParser parser = new JSONParser();
    return (JSONObject) parser.parse(strBody);
  }

  private static void constructGraphs(JSONArray selectedClasses) throws IOException {
    // Initialize maps and courseList
    HashSet<String> courseList = new HashSet<>();
    for (Object course : selectedClasses) {
      String key = (String) ((JSONObject) course).get("course_id");
      courseList.add(key);
      prerequisiteGraph.put(key, new HashSet<String>());
      postreqGraph.put(key, new HashSet<String>());
      credits.put(key, 0);
    }
    for (int i = 0; i < selectedClasses.size(); i++) {
    }
  }

  private static HashSet<String> getPrerequisites(String prereqString, HashSet<String> courseList) throws IOException {
    HashSet<String> prereqs = new HashSet<>();
    String[] words = prereqString.split("\\P{L}+");
    for (String word : words) {
      if (courseList.contains(word)) {
        prereqs.add(word);
      }
    }
    return prereqs;
  }

  private void respondWithError(String message, int errorType, HttpServletResponse response)
      throws IOException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("message", message);
    jsonObject.put("status", "error");
    response.setStatus(errorType);
    response.getWriter().println(new Gson().toJson(jsonObject));
  }
}
