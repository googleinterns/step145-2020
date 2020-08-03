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
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.collegeplanner.data.Course;
import com.google.collegeplanner.data.Section;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/** Servlet that returns list of course sections.*/
@WebServlet("/api/sections")
public class SectionServlet extends BaseServlet {
  DatastoreService datastore;
  public SectionServlet() {
    this(DatastoreServiceFactory.getDatastoreService());
  }

  public SectionServlet(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /**
   * Reads from Datastore and returns response with section details
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String courseId = request.getParameter("course_id");
    String sectionId = request.getParameter("section_id");
    if (courseId == null || courseId == "") {
      respondWithError(
          "Invalid or missing course id.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }
    if (sectionId == null || sectionId == "") {
      respondWithError(
          "Invalid or missing section id.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }

    Query query = new Query("Course").setFilter(
        new FilterPredicate("course_id", FilterOperator.EQUAL, courseId));
    PreparedQuery preparedQuery = datastore.prepare(query);
    List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withDefaults());

    if (results.size() == 0) {
      respondWithError(HttpServletResponse.SC_NOT_FOUND, response);
      return;
    }

    // Convert Section entities to Section objects to serialize into json.
    ArrayList<Section> sections = new ArrayList<Section>();
    Entity courseEntity = results.get(0);
    ArrayList<EmbeddedEntity> sectionEntities =
        (ArrayList<EmbeddedEntity>) courseEntity.getProperty("sections");
    for (EmbeddedEntity sectionEntity : sectionEntities) {
      String section_id = (String) sectionEntity.getProperty("section_id");
      if (section_id.equals(sectionId)) {
        Section section;
        try {
          section = new Section(sectionEntity);
        } catch (ParseException e) {
          respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
          return;
        }
        sections.add(section);
      }
    }

    JSONObject sectionsInfo = new JSONObject();
    sectionsInfo.put("sections", sections);

    response.setContentType("application/json;");
    Gson gson = new GsonBuilder().serializeNulls().create();
    response.getWriter().println(gson.toJson(sectionsInfo));
  }
}
