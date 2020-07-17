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
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.net.URI;
import org.apache.http.client.utils.URIBuilder;
import java.net.URISyntaxException;

/** Provides an interface for making outside API calls. */
public class DatastoreServlet {
 
  public void test() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ApiUtil apiUtil = new ApiUtil();
    JSONArray coursesArray;
    Entity entity;
    int page = 0;
    while (true) {
      // loop through pages
      URI uri;
      try {
        URIBuilder builder = new URIBuilder("https://api.umd.io/v1/courses");
        builder.setParameter("page", Integer.toString(page++));
        uri = builder.build();
      } catch (URISyntaxException e) {
        break;
      }
      coursesArray = apiUtil.getJsonArray(uri);
      if (coursesArray == null) {
        break;
      }
      
      addCourses(coursesArray);
  }

  private void addCourses(JSONArray coursesArray) {
    if (coursesArray == null) {
      return;
    }


    JSONObject courseJson;
    Entity entity;
    URI uri;
    JSONArray sectionsArray;
    for (int i = 0; i < coursesArray.size(); i++) {
      // loop through courses

      // we have a course
      courseJson = coursesArray.getJSONObject(i);
      Course course = new Course(courseJson);

      entity = new Entity("Course");
      entity.setProperty("course_id", course.getCourseId());
      entity.setProperty("name", course.getName());
      entity.setProperty("semester", course.getSemester());
      entity.setProperty("credits", course.getCredits());
      entity.setProperty("department_id", course.getDepartmentId());
      entity.setProperty("description", course.getDescription());
      entity.setProperty("coreqs", course.getCorequirements());
      entity.setProperty("prereqs", course.getPrerequirements());
      entity.setProperty("restrictions", course.getRestrictions());
      entity.setProperty("additional_info", course.getAdditionalInfo());
      entity.setProperty("credit_granted_for", course.getCreditGrantedFor());
      entity.setProperty("grading_method", course.getGradingMethod());

      try {
        uri = new URI("https://api.umd.io/v1/courses/" + course.getCourseId() + "/sections");
      } catch (URISyntaxException e) {
        return;
      }

      sectionsArray = apiUtil.getJsonArray(uri);
      if (sectionsArray == null) {
        return;
      }
      addSectionsToCourse(entity, sectionsArray);
    }
  } 

  private void addSectionsToCourse(Entity entity, JSONArray sectionsArray) {
    if (entity == null || sectionsArray == null) {
      return;
    }

    for (int j = 0; j < sectionsArray.size(); j++) {
      // loop through sections
      JSONObject sectionJson = sectionsArray.getJSONObject(i);
    }
  }

}
