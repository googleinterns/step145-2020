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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns the mutli-semester plan for the given courses.*/
@WebServlet("/planner")
public class PlannerServlet extends HttpServlet {
  /**
   * Organizes courses into the given number of semesters
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: Implement algorithm to split into the number of semesters
    String strClasses = request.getParameter("selectedClasses");
    // Returns the courses in a single semester
    String[][] courses = {strClasses.split(",")};
    response.setContentType("applications/json;");
    response.getWriter().println(new Gson().toJson(courses));
  }
}
