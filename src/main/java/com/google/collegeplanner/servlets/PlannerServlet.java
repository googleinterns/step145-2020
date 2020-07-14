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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
  static HashMap<String, Integer> indegree;
  static HashMap<String, HashSet<String>> corequisites;
  static HashMap<String, HashSet<String>> postrequisites;
  static HashMap<String, Integer> depths;
  static HashMap<String, Integer> credits;
  static int totalCredits;
  static ArrayList<String> courseList;

  /**
   * Organizes courses into the given number of semesters
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO(ramyabuva): Implement algorithm to split into the number of semesters
    JSONObject body;
    int numSemesters = 1;
    try {
      body = getBody(request);
    } catch (ParseException | NullPointerException | NumberFormatException e) {
      respondWithError(
          "Invalid body for POST request.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }
    JSONArray selectedClasses = (JSONArray) body.get("selectedClasses");

    constructGraphs(selectedClasses);

    ArrayList<ArrayList<String>> semesters = new ArrayList<>();
    for (int i = numSemesters; i > 0; i--) {
      int avgCredits = totalCredits / i;
      ArrayList<String> semester = new ArrayList<>();
      
      semesters.add(courseList);
    }
    response.getWriter().println(new Gson().toJson(semesters));
  }

  private static JSONObject getBody(HttpServletRequest request)
      throws IOException, ParseException, NullPointerException {
    String strBody =
        request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    JSONParser parser = new JSONParser();
    return (JSONObject) parser.parse(strBody);
  }

  private static void constructGraphs(JSONArray selectedClasses)
      throws IOException, NumberFormatException {
    // Initialize maps and courseList
    indegree = new HashMap<String, Integer>();
    corequisites = new HashMap<String, HashSet<String>>();
    depths = new HashMap<String, Integer>();
    credits = new HashMap<String, Integer>();
    postrequisites = new HashMap<String, HashSet<String>>();
    courseList = new ArrayList<>();

    for (Object course : selectedClasses) {
      String key = (String) ((JSONObject) course).get("course_id");
      courseList.add(key);
      postrequisites.put(key, new HashSet<>());
    }

    for (Object course : selectedClasses) {
      String key = (String) ((JSONObject) course).get("course_id");
      int creditVal = Integer.parseInt((String) ((JSONObject) course).get("credits"));
      String prereqStr =
          (String) ((JSONObject) ((JSONObject) course).get("relationships")).get("prereqs");
      String coreqStr =
          (String) ((JSONObject) ((JSONObject) course).get("relationships")).get("coreqs");
      HashSet<String> prereqs = getCoursesFromString(prereqStr);
      HashSet<String> coreqs = getCoursesFromString(coreqStr);
      indegree.put(key, prereqs.size());
      corequisites.put(key, coreqs);
      credits.put(key, creditVal);
      totalCredits += creditVal;
      for (String prereq : prereqs) {
        postrequisites.get(prereq).add(key);
      }
    }
    getAllDepths();

    // sort courseList from greatest depth to lowest depth
    Collections.sort(courseList, (String m1, String m2) -> 
            depths.get(m2).compareTo(depths.get(m1)));
  }

  private static void getAllDepths() {
    for (String course : courseList) {
      if (!depths.containsKey(course)) {
        getDepth(course);
      }
    }
  }

  private static int getDepth(String course) {
    if (depths.containsKey(course)) {
      return depths.get(course);
    }
    if (postrequisites.get(course).size() == 0) {
      depths.put(course, 1);
      return 1;
    }
    HashSet<String> coursePrereqs = postrequisites.get(course);
    ArrayList<Integer> childrenHeights = new ArrayList<>();
    for (String prereq : coursePrereqs) {
      childrenHeights.add(getDepth(prereq));
    }
    int myHeight = Collections.max(childrenHeights) + 1;
    depths.put(course, myHeight);
    return myHeight;
  }

  private static HashSet<String> getCoursesFromString(String engCourses)
      throws IOException {
    HashSet<String> prereqs = new HashSet<>();
    try {
      String[] words = engCourses.split("[\\p{Punct}\\s]+");
      for (String word : words) {
        if (courseList.contains(word)) {
          prereqs.add(word);
        }
      }
    } catch (NullPointerException e) {
    } finally {
      return prereqs;
    }
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
