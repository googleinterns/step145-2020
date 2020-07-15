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
import java.util.Iterator;
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
  static HashMap<String, HashSet<String>>
      postrequisites; // Courses that come after the given course in the graph
  static HashMap<String, Integer>
      depths; // depth of a course (number in a prerequisite "chain" begining at that node)
  static HashMap<String, Integer> credits; // Number of credits for each course
  static int totalCredits;
  static ArrayList<String> courseList;

  /**
   * Organizes courses from POST request into a given number of semesters
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JSONObject body;
    int numSemesters;
    try {
      body = getBody(request);
      numSemesters = Integer.parseInt((String) body.get("semesters"));
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
      for (int j = 0; j < courseList.size(); j++) {
        String course = courseList.get(j);
        if (indegree.get(course) == 0) {
          // Check if all corequisites also have an indegree of 0
          boolean isValid = true;
          for (String coreq : corequisites.get(course)) {
            if (indegree.get(coreq) != 0) {
              isValid = false;
              break;
            }
          }
          if (isValid) {
            // If valid, add all to semester
            courseList.remove(j);
            j--;
            avgCredits -= credits.get(course);
            totalCredits -= credits.get(course);
            semester.add(course);
            for (String coreq : corequisites.get(course)) {
              courseList.remove(coreq);
              avgCredits -= credits.get(coreq);
              totalCredits -= credits.get(coreq);
              semester.add(coreq);
            }
          }
        }
        // Breaks out if there are no more required classes and we exceeded our credit limit
        if (depths.get(course) != i && avgCredits <= 0) {
          break;
        }
      }
      // Update indegrees for all postrequisites
      for (String course : semester) {
        for (String postreq : postrequisites.get(course)) {
          indegree.replace(postreq, indegree.get(postreq) - 1);
        }
      }
      semesters.add(semester);
    }
    // If courseList is not empty, scheduling was not possible
    if (!courseList.isEmpty()) {
      respondWithError(
          "Not enough semesters for schedule.", HttpServletResponse.SC_BAD_REQUEST, response);
    }
    response.setContentType("applications/json;");
    response.getWriter().println(new Gson().toJson(semesters));
  }

  /**
   * Gets the JSON Representation of the body of the POST request
   */
  private static JSONObject getBody(HttpServletRequest request)
      throws IOException, ParseException, NullPointerException {
    String strBody =
        request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    JSONParser parser = new JSONParser();
    return (JSONObject) parser.parse(strBody);
  }

  /**
   * Creates the representation for the graph and reads/stores necessary information from
   * selectedClasses
   */
  private static void constructGraphs(JSONArray selectedClasses)
      throws IOException, NumberFormatException {
    // Initialize graph representation and courseList
    indegree = new HashMap<String, Integer>();
    corequisites = new HashMap<String, HashSet<String>>();
    depths = new HashMap<String, Integer>();
    credits = new HashMap<String, Integer>();
    postrequisites = new HashMap<String, HashSet<String>>();
    courseList = new ArrayList<>();

    // creates CourseList and initialize postrequisites
    for (Object course : selectedClasses) {
      String key = (String) ((JSONObject) course).get("course_id");
      courseList.add(key);
      postrequisites.put(key, new HashSet<>());
    }

    // populate static class variables
    for (Object course : selectedClasses) {
      String key = (String) ((JSONObject) course).get("course_id");
      int creditVal = Integer.parseInt((String) ((JSONObject) course).get("credits"));
      credits.put(key, creditVal);
      totalCredits += creditVal;

      HashSet<String> prereqs = getCoursesFromString(
          (String) ((JSONObject) ((JSONObject) course).get("relationships")).get("prereqs"));
      indegree.put(key, prereqs.size());

      HashSet<String> coreqs = getCoursesFromString(
          (String) ((JSONObject) ((JSONObject) course).get("relationships")).get("coreqs"));
      corequisites.put(key, coreqs);

      for (String prereq : prereqs) {
        postrequisites.get(prereq).add(key);
      }
    }

    fillAllDepths();

    // sort courseList from greatest depth to lowest depth
    Collections.sort(
        courseList, (String m1, String m2) -> depths.get(m2).compareTo(depths.get(m1)));
  }

  /**
   * Populate this.depths with the depth of each course
   */
  private static void fillAllDepths() {
    // Calls recursive function to find depths
    for (String course : courseList) {
      if (!depths.containsKey(course)) {
        getDepth(course);
      }
    }
  }

  /**
   * Dynamic Programming Algorithm to find the depth of a course
   */
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

  /**
   * Takes English representation of course prerequisites/corequisites and returns a HashSet with
   * all the contained courses
   */
  private static HashSet<String> getCoursesFromString(String engCourses) throws IOException {
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
