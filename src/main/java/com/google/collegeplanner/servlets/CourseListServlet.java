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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.collegeplanner.data.Course;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
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
  DatastoreService datastore;

  public CourseListServlet() {
    this(DatastoreServiceFactory.getDatastoreService());
  }

  public CourseListServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /**
   * Reads from Datastore and returns response with course details
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create the URI and specify the parameters.
    String department = request.getParameter("department");
    if (department == null || department == "") {
      respondWithError(
          "Invalid or missing department.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }

    Query query = new Query("Course").setFilter(
        new FilterPredicate("dept_id", FilterOperator.EQUAL, department));
    PreparedQuery preparedQuery = datastore.prepare(query);
    List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withDefaults());

    ArrayList<Course> courses = new ArrayList<Course>();
    for (Entity courseEntity : results) {
      Course course;
      try {
        course = new Course(courseEntity);
      } catch (ParseException e) {
        respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        return;
      }
      courses.add(course);
    }

    JSONObject schoolCourseInfo = new JSONObject();
    schoolCourseInfo.put("courses", courses);

    response.setContentType("application/json;");
    Gson gson = new GsonBuilder().serializeNulls().create();
    response.getWriter().println(gson.toJson(schoolCourseInfo));
  }
}
