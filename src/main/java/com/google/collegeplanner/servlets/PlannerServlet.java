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
import java.util.LinkedList;
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
public class PlannerServlet extends BaseServlet {
  private HashMap<String, Integer> indegree; // Number of prerequisites for a course
  private HashMap<String, HashSet<String>>
      nextCourses; // Courses that come after the given course in the graph
  private HashMap<String, HashSet<String>> corequisites;
  private HashMap<String, Integer> heights; // height of a course in the graph
  private HashMap<String, Integer> credits; // Number of credits for each course
  private LinkedList<String> courseList;
  private int totalCredits;

  /**
   * Organizes courses from POST request into a given number of semesters
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JSONObject body;
    int numSemesters;
    JSONArray selectedClasses;
    try {
      body = getPostRequestBody(request);
      numSemesters = Integer.parseInt((String) body.get("semesters"));
      selectedClasses = (JSONArray) body.get("selectedClasses");
    } catch (NumberFormatException | ClassCastException | ParseException | NullPointerException e) {
      respondWithError(
          "Invalid body for POST request.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }

    constructGraphs(selectedClasses);

    JSONObject coursePlan = getPlan(numSemesters);
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(coursePlan));
  }

  /**
   * Creates and returns a valid plan for the given number of semesters
   * @param numSemesters The number of semesters remaining
   */
  private JSONObject getPlan(int numSemesters) {
    JSONObject coursePlan = new JSONObject();
    ArrayList<ArrayList<String>> semesters = new ArrayList<>();
    ArrayList<Integer> creditsPerSemester = new ArrayList<>();
    // if there is a valid ordering, create ordering
    if (courseList.size() <= 0 || heights.get(courseList.get(0)) <= numSemesters) {
      for (int i = numSemesters; i > 0; i--) {
        int avgCredits = totalCredits / i;
        ArrayList<String> semester = new ArrayList<>();
        int semesterCredits = 0;
        for (int j = 0; j < courseList.size(); j++) {
          String course = courseList.get(j);
          if (indegree.get(course) == 0) {
            // Check if all corequisites also have an indegree of 0
            if (isValidToAdd(course)) {
              // If valid, add all to semester
              courseList.remove(j);
              j--;
              semesterCredits += credits.get(course);
              totalCredits -= credits.get(course);
              semester.add(course);
              for (String coreq : corequisites.get(course)) {
                courseList.remove(coreq);
                semesterCredits += credits.get(coreq);
                totalCredits -= credits.get(coreq);
                semester.add(coreq);
              }
            }
          }
          // Guarantees that all classes that must be taken in this semester are added
          if (heights.get(course) != i && avgCredits <= semesterCredits) {
            // Breaks out if there are no more required classes and we exceeded our credit limit
            break;
          }
        }
        // Update indegrees for all next courses
        for (String course : semester) {
          for (String nextCourse : nextCourses.get(course)) {
            indegree.replace(nextCourse, indegree.get(nextCourse) - 1);
          }
        }
        semesters.add(semester);
        creditsPerSemester.add(semesterCredits);
      }
    }
    coursePlan.put("semester_plan", semesters);
    coursePlan.put("semester_credits", creditsPerSemester);
    return coursePlan;
  }

  /**
   * Given a course, returns whether all of its corequisites have a indegree of 0
   * @param course The course ID for the course
   */
  private boolean isValidToAdd(String course) {
    for (String coreq : corequisites.get(course)) {
      if (indegree.get(coreq) != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Creates the representation for the graph and reads/stores necessary information from
   * selectedClasses
   * @param selectedClasses JSONArray that contains details for all of the courses selected by the
   *     user
   */
  private void constructGraphs(JSONArray selectedClasses)
      throws IOException, NumberFormatException {
    // Initialize graph representation and courseList
    indegree = new HashMap<String, Integer>();
    corequisites = new HashMap<String, HashSet<String>>();
    heights = new HashMap<String, Integer>();
    credits = new HashMap<String, Integer>();
    nextCourses = new HashMap<String, HashSet<String>>();
    courseList = new LinkedList<>();
    HashSet<String> courseLookup = new HashSet<>(); // To avoid duplicate courses in O(1)

    // creates CourseList and initialize nextCourses HashSets
    for (Object course : selectedClasses) {
      String key = (String) ((JSONObject) course).get("course_id");
      if (!courseLookup.contains(key)) {
        courseList.add(key);
      }
      courseLookup.add(key);
      nextCourses.put(key, new HashSet<>());
    }

    // populate class variables
    for (Object course : selectedClasses) {
      JSONObject courseJson = (JSONObject) course;
      String key = (String) courseJson.get("course_id");
      int creditVal = ((Long) courseJson.get("credits")).intValue();
      credits.put(key, creditVal);
      totalCredits += creditVal;

      HashSet<String> prereqs = getCoursesFromString((String) (courseJson.get("prereqs")));
      indegree.put(key, prereqs.size());

      HashSet<String> coreqs = getCoursesFromString((String) (courseJson.get("coreqs")));
      corequisites.put(key, coreqs);

      for (String prereq : prereqs) {
        nextCourses.get(prereq).add(key);
      }
    }

    fillHeights();

    // sort courseList from greatest height to lowest height
    Collections.sort(courseList,
        (String course1, String course2) -> heights.get(course2).compareTo(heights.get(course1)));
  }

  /**
   * Populate this.heights with the height of each course
   */
  private void fillHeights() {
    // Calls recursive function to find heights
    for (String course : courseList) {
      if (!heights.containsKey(course)) {
        getHeight(course);
      }
    }
  }

  /**
   * Dynamic Programming Algorithm to find the height of a course
   * @param course The course ID for the course
   */
  private int getHeight(String course) {
    if (heights.containsKey(course)) {
      return heights.get(course);
    }
    if (nextCourses.get(course).size() == 0) {
      heights.put(course, 1);
      return 1;
    }
    int maxHeight = 0;
    for (String nextCourse : nextCourses.get(course)) {
      int newHeight = getHeight(nextCourse);
      if (newHeight > maxHeight) {
        maxHeight = newHeight;
      }
    }
    int courseHeight = maxHeight + 1;
    heights.put(course, courseHeight);
    return courseHeight;
  }

  /**
   * Takes English representation of course prerequisites/corequisites and returns a HashSet with
   * all the contained courses
   *
   * @param courseRelationshipDescription The natural language string to get courses from
   */
  private HashSet<String> getCoursesFromString(String courseRelationshipDescription)
      throws IOException {
    HashSet<String> prereqs = new HashSet<>();
    if (courseRelationshipDescription == null) {
      return prereqs;
    }
    String[] words = courseRelationshipDescription.split("[\\p{Punct}\\s]+");
    for (String word : words) {
      if (courseList.contains(word)) {
        prereqs.add(word);
      }
    }
    return prereqs;
  }
}
