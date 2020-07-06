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

package com.google.servlets;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/** Servlet that returns list of courses.*/
@WebServlet("/courses")
public class CourseListServlet extends HttpServlet {
  /**
   * Reads from Datastore and returns response with course details
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: Store and retrieve from Datastore
    String[] courseIdList = new String[] {"CMSC101", "CMSC106", "CMSC122", "CMSC131", "CMSC132",
        "CMSC133", "CMSC216", "CMSC250", "CMSC298A", "CMSC320", "CMSC330", "CMSC351", "CMSC388J",
        "CMSC389A", "CMSC389B", "CMSC389E", "CMSC389N", "CMSC390O"};

    ArrayList<JSONObject> courses = new ArrayList<>();
    for (String course : courseIdList) {
      JSONObject newCourse = new JSONObject();
      newCourse.put("name", course);
      courses.add(newCourse);
    }

    JSONObject schoolCourseInfo = new JSONObject();
    schoolCourseInfo.put("courses_detailed", convertToJson(courses));

    response.setContentType("applications/json;");
    response.getWriter().println(schoolCourseInfo);
  }

  /**
   * Converts a String[] instance into a JSON string using the Gson library.
   */
  private String convertToJson(ArrayList<JSONObject> courses) {
    return new Gson().toJson(courses);
  }
}
