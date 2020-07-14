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
  static HashMap<String, HashSet<String>> prerequisites;
  static HashMap<String, HashSet<String>> corequisites;
  static HashMap<String, Integer> longestChains;
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
    constructGraphs(selectedClasses);

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
    prerequisites = new HashMap<String, HashSet<String>>();
    corequisites = new HashMap<String, HashSet<String>>();
    longestChains = new HashMap<String, Integer>();
    credits = new HashMap<String, Integer>();

    HashSet<String> courseList = new HashSet<>();
    HashMap<String, HashSet<String>> postrequisites = new HashMap<String, HashSet<String>>();
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
      HashSet<String> prereqs = getCoursesFromString(prereqStr, courseList);
      HashSet<String> coreqs = getCoursesFromString(coreqStr, courseList);
      prerequisites.put(key, prereqs);
      corequisites.put(key, coreqs);
      credits.put(key, creditVal);
      for (String prereq : prereqs) {
        postrequisites.get(prereq).add(key);
      }
    }
    getLongestChains(postrequisites, courseList);
    System.out.println(courseList);
    System.out.println(prerequisites);
    System.out.println(corequisites);
    System.out.println(postrequisites);
    System.out.println(longestChains);
    System.out.println(credits);
  }

  private static void getLongestChains(
      HashMap<String, HashSet<String>> postrequisites, HashSet<String> courseList) {
    for (String course : courseList) {
      if (!longestChains.containsKey(course) ) {
        getLongestChain(postrequisites, course);
      }
    }
  }

  private static int getLongestChain(HashMap<String, HashSet<String>> postrequisites, String course){
      if (longestChains.containsKey(course)) {
        return longestChains.get(course);
      }
      if (postrequisites.get(course).size() == 0) {
        longestChains.put(course, 1);
        return 1;
      }
      HashSet<String> coursePrereqs = postrequisites.get(course);
      ArrayList<Integer> childrenHeights = new ArrayList<>();
      for (String prereq : coursePrereqs) {
         childrenHeights.add(getLongestChain(postrequisites, prereq));
      }
      System.out.println(childrenHeights);
      int myHeight = Collections.max(childrenHeights) + 1;
      longestChains.put(course, myHeight);
      return myHeight;
  }

  private static HashSet<String> getCoursesFromString(
      String prereqString, HashSet<String> courseList) throws IOException {
    HashSet<String> prereqs = new HashSet<>();
    System.out.println(prereqString);
    try {
      String[] words = prereqString.split("[\\p{Punct}\\s]+");
      for (String word : words) {
        System.out.println(word);
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
