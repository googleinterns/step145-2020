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
import com.google.collegeplanner.data.Course;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/** Queries the UMD API and downloads the data to datastore. */
@WebServlet("/api/download")
public class DatastoreServlet extends BaseServlet {
  final int PAGE_LIMIT = 200;

  DatastoreService datastore;

  public DatastoreServlet() {
    this(DatastoreServiceFactory.getDatastoreService(), new ApiUtil());
  }

  public DatastoreServlet(DatastoreService datastore, ApiUtil apiUtil) {
    super(apiUtil);
    this.datastore = datastore;
  }

  /*
   * Downloads all course data to datastore.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Loop through the pages.
    // We don't know how many pages there are beforehand. We stop looping when the new page
    // doesn't have any results.
    int page = 1;
    do {
      URI uri;
      try {
        URIBuilder builder = new URIBuilder("https://api.umd.io/v1/courses");
        builder.setParameter("page", Integer.toString(page));
        uri = builder.build();
      } catch (URISyntaxException e) {
        respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        return;
      }
      JSONArray coursesArray = apiUtil.getJsonArray(uri);
      if (coursesArray == null) {
        respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        return;
      } else if (coursesArray.size() == 0) {
        // Successful. The default status code response is 200.
        return;
      }
      addCourses(coursesArray, response);
    } while (page++ < PAGE_LIMIT);
  }

  /**
   * Creates a Course object from a JSONArray.
   * @param coursesArray The course json from the UMD API.
   * @param response The HttpServletResponse object.
   */
  private void addCourses(JSONArray coursesArray, HttpServletResponse response) throws IOException {
    if (coursesArray == null) {
      return;
    }

    // Loop through each page's courses.
    for (Object jsonObject : coursesArray) {
      JSONObject courseJson = (JSONObject) jsonObject;

      // Create a Course object from the JSONObject for parsing.
      Course course;
      try {
        course = new Course(courseJson);
      } catch (Exception e) {
        respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        return;
      }

      URI uri;
      try {
        uri = new URI("https://api.umd.io/v1/courses/"
            + URLEncoder.encode(course.getCourseId(), StandardCharsets.UTF_8.toString())
            + "/sections");
      } catch (URISyntaxException | UnsupportedEncodingException e) {
        respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        return;
      }
      JSONArray sectionsArray = apiUtil.getJsonArray(uri);
      addSectionsToCourse(sectionsArray, course);
    }
  }

  /**
   * Adds the Course and Section to datastore.
   * @param sectionsArray The section json from the UMD API.
   * @param course The Course object.
   */
  private void addSectionsToCourse(JSONArray sectionsArray, Course course) {
    if (sectionsArray == null || course == null) {
      return;
    }

    // Loop through each course's sections.
    ArrayList<EmbeddedEntity> sectionEntities = new ArrayList<EmbeddedEntity>();
    for (Object jsonObject : sectionsArray) {
      // TODO(savsa): create Section class to handle JSON object parsing.
      // For now, just store the section id.
      JSONObject sectionJson = (JSONObject) jsonObject;
      EmbeddedEntity sectionEntity = new EmbeddedEntity();
      sectionEntity.setProperty("section_id", (String) sectionJson.get("section_id"));
      sectionEntities.add(sectionEntity);
    }

    Entity entity = new Entity("Course");
    entity.setProperty("course_id", course.getCourseId());
    entity.setProperty("name", course.getName());
    entity.setProperty("semester", course.getSemester());
    entity.setProperty("credits", course.getCredits());
    entity.setProperty("department_id", course.getDepartmentId());
    entity.setProperty("description", course.getDescription());
    entity.setProperty("coreqs", course.getCorequisites());
    entity.setProperty("prereqs", course.getCorequisites());
    entity.setProperty("restrictions", course.getRestrictions());
    entity.setProperty("additional_info", course.getAdditionalInfo());
    entity.setProperty("credit_granted_for", course.getCreditGrantedFor());
    entity.setProperty("sections", sectionEntities);

    datastore.put(entity);
  }
}
