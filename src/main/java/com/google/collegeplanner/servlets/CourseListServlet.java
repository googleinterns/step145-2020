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
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/** Servlet that returns list of courses.*/
@WebServlet("/api/courses")
public class CourseListServlet extends BaseServlet {
  public CourseListServlet() {
    super(new ApiUtil());
  }

  public CourseListServlet(ApiUtil apiUtil) {
    super(apiUtil);
  }

  /**
   * Reads from Datastore and returns response with course details
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create the URI and specify the parameters.
    String department = request.getParameter("department");
    if (department == null || department == "") {
      respondWithError(HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }
    URI uri;
    try {
      URIBuilder builder = new URIBuilder("https://api.umd.io/v1/courses");
      builder.setParameter("semester", "202008");
      builder.setParameter("dept_id", department);
      uri = builder.build();
    } catch (URISyntaxException e) {
      respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
      return;
    }

    JSONArray jsonArray = apiUtil.getJsonArray(uri);
    if (jsonArray == null) {
      respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
      return;
    }

    JSONObject schoolCourseInfo = new JSONObject();
    schoolCourseInfo.put("courses", jsonArray);

    response.setContentType("applications/json;");
    response.getWriter().println(schoolCourseInfo);
  }
}
