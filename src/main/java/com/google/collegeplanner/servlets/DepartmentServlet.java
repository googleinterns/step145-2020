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

/** Servlet that returns list of departments.*/
@WebServlet("/api/departments")
public class DepartmentServlet extends HttpServlet {
  ApiUtil apiUtil;

  public DepartmentServlet() {
    this(new ApiUtil());
  }

  public DepartmentServlet(ApiUtil apiUtil) {
    this.apiUtil = apiUtil;
  }

  /**
   * Calls API and returns response with a list of all departments. This is not dependent on any
   * input.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create the URI and specify the parameters.
    URI uri;
    try {
      URIBuilder builder = new URIBuilder("https://api.umd.io/v1/courses/departments");
      builder.setParameter("semester", "202008");
      uri = builder.build();
    } catch (URISyntaxException e) {
      respondWithError(
          "Internal server error.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
      return;
    }

    JSONArray jsonArray = apiUtil.getJsonArray(uri);
    if (jsonArray == null) {
      respondWithError(
          "Internal server error.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
      return;
    }

    JSONObject schoolDeptInfo = new JSONObject();
    schoolDeptInfo.put("departments", jsonArray);

    response.setContentType("applications/json;");
    response.getWriter().println(schoolDeptInfo);
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
