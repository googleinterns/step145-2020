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
import java.net.URI;
import java.net.URISyntaxException;
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
  DatastoreService datastore;

  public DatastoreServlet() {
    this(DatastoreServiceFactory.getDatastoreService(), new ApiUtil());
  }

  public DatastoreServlet(DatastoreService datastore, ApiUtil apiUtil) {
    super(apiUtil);
    this.datastore = datastore;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Loop through the pages.
    int page = 0;
    do {
      URI uri;
      try {
        URIBuilder builder = new URIBuilder("https://api.umd.io/v1/courses");
        builder.setParameter("page", Integer.toString(page++));
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
        // We don't know how many pages there are beforehand. We stop looping when the new page
        // doesn't have any results.
        respondWithError(HttpServletResponse.SC_OK, response);
        return;
      }
      addCourses(coursesArray, response);
      count++;
    } while (count < 2);
  }

  private void addCourses(JSONArray coursesArray, HttpServletResponse response) throws IOException {
    if (coursesArray == null) {
      return;
    }

    // Loop through each page's courses.
    for (Object jsonObject : coursesArray) {
      JSONObject courseJson = (JSONObject) jsonObject;

      // Create a Course object from the JSONObject for parsing.
      Course course = new Course(courseJson);
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

      URI uri;
      try {
        uri = new URI("https://api.umd.io/v1/courses/" + course.getCourseId() + "/sections");
      } catch (URISyntaxException e) {
        respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
        return;
      }

      JSONArray sectionsArray = apiUtil.getJsonArray(uri);
      addSectionsToCourse(entity, sectionsArray);
      return;
    }
  }

  private void addSectionsToCourse(Entity entity, JSONArray sectionsArray) {
    if (entity == null || sectionsArray == null) {
      return;
    }

    // Loop through each course's sections.
    ArrayList<EmbeddedEntity> sectionEntities = new ArrayList<EmbeddedEntity>();
    for (Object jsonObject : sectionsArray) {
      // TODO(savsa): create Section class to handle json object parsing.
      // For now, just store the section id.
      JSONObject sectionJson = (JSONObject) jsonObject;
      EmbeddedEntity sectionEntity = new EmbeddedEntity();
      sectionEntity.setProperty("section_id", (String) sectionJson.get("section_id"));
      sectionEntities.add(sectionEntity);
    }
    entity.setProperty("sections", sectionEntities);
    datastore.put(entity);
  }
}
