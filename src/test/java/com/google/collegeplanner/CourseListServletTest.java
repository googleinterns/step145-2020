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
package com.google.collegeplanner;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.collegeplanner.servlets.ApiUtil;
import com.google.collegeplanner.servlets.CourseListServlet;
import com.google.collegeplanner.servlets.DatastoreServlet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/** Tests CourseListServlet */
@RunWith(JUnit4.class)
public final class CourseListServletTest {
  HttpServletRequest mockedRequest;
  HttpServletResponse mockedResponse;
  StringWriter stringWriter;
  PrintWriter writer;
  JSONParser parser;
  DatastoreService datastore;
  LocalServiceTestHelper helper;
  ApiUtil apiUtil;

  JSONArray firstCoursesJson;
  JSONArray firstCourseJson;
  JSONArray firstSectionJson;
  JSONArray secondSectionJson;
  JSONArray emptyJson;

  String firstCourses = "[{"
      + "\"course_id\":\"AASP100\","
      + "\"core\":[\"SH\",\"D\"],"
      + "\"relationships\":{"
      + "  \"coreqs\":null,"
      + "  \"additional_info\":null,"
      + "  \"restrictions\":null,"
      + "  \"credit_granted_for\":null,"
      + "  \"also_offered_as\":null,"
      + "  \"formerly\":null,"
      + "  \"prereqs\":null"
      + "},"
      + "\"credits\":\"3\","
      + "\"name\":\"Introduction to African American Studies\","
      + "\"description\":\"Significant aspects of the history of African Americans.\","
      + "\"semester\":\"202008\","
      + "\"gen_ed\":[[\"DSHS\",\"DVUP\"]],"
      + "\"dept_id\":\"AASP\","
      + "\"department\":\"African American Studies\","
      + "\"grading_method\":[\"Regular\",\"Pass-Fail\",\"Audit\"],"
      + "\"sections\":["
      + "  \"AASP100-0101\","
      + "  \"AASP100-0201\","
      + "  \"AASP100-0301\","
      + "  \"AASP100-0401\","
      + "  \"AASP100-0501\","
      + "  \"AASP100-0601\","
      + "  \"AASP100-0701\""
      + "]"
      + "},{"
      + "\"course_id\":\"AASP100H\","
      + "\"core\":[\"SH\",\"D\"],"
      + "\"relationships\":{"
      + "  \"coreqs\":null,"
      + "  \"additional_info\":null,"
      + "  \"restrictions\":null,"
      + "  \"credit_granted_for\":null,"
      + "  \"also_offered_as\":null,"
      + "  \"formerly\":null,"
      + "  \"prereqs\":null"
      + "},"
      + "\"credits\":\"3\","
      + "\"name\":\"Introduction to African American Studies\","
      + "\"description\":\"Significant aspects of the history of African Americans.\","
      + "\"semester\":\"202008\","
      + "\"gen_ed\":[[\"DSHS\",\"DVUP\"]],"
      + "\"dept_id\":\"AASP\","
      + "\"department\":\"African American Studies\","
      + "\"grading_method\":[\"Regular\",\"Pass-Fail\",\"Audit\"],"
      + "\"sections\":["
      + "  \"AASP100H-0101\""
      + "]"
      + "}]";

  String firstCourse = "[{"
      + "\"course_id\":\"AASP100\","
      + "\"core\":[\"SH\",\"D\"],"
      + "\"relationships\":{"
      + "  \"coreqs\":null,"
      + "  \"additional_info\":null,"
      + "  \"restrictions\":null,"
      + "  \"credit_granted_for\":null,"
      + "  \"also_offered_as\":null,"
      + "  \"formerly\":null,"
      + "  \"prereqs\":null"
      + "},"
      + "\"credits\":\"3\","
      + "\"name\":\"Introduction to African American Studies\","
      + "\"description\":\"Significant aspects of the history of African Americans.\","
      + "\"semester\":\"202008\","
      + "\"gen_ed\":[[\"DSHS\",\"DVUP\"]],"
      + "\"dept_id\":\"AASP\","
      + "\"department\":\"African American Studies\","
      + "\"grading_method\":[\"Regular\",\"Pass-Fail\",\"Audit\"],"
      + "\"sections\":["
      + "  \"AASP100-0101\","
      + "  \"AASP100-0201\","
      + "  \"AASP100-0301\","
      + "  \"AASP100-0401\","
      + "  \"AASP100-0501\","
      + "  \"AASP100-0601\","
      + "  \"AASP100-0701\""
      + "]"
      + "}]";

  String firstSection = "[{"
      + "\"course\": \"AASP100\","
      + "\"section_id\": \"AASP100-0101\","
      + "\"semester\": \"202008\","
      + "\"number\": \"0101\","
      + "\"seats\": \"21\","
      + "\"meetings\": [{"
      + "  \"days\": \"MWF\","
      + "  \"room\": \"1101\","
      + "  \"building\": \"SQH\","
      + "  \"classtype\": \"\","
      + "  \"start_time\": \"10:00am\","
      + "  \"end_time\": \"10:50am\""
      + "}, {"
      + "  \"days\": \"MWF\","
      + "  \"room\": \"2205\","
      + "  \"building\": \"LEF\","
      + "  \"classtype\": \"\","
      + "  \"start_time\": \"10:00am\","
      + "  \"end_time\": \"10:50am\""
      + "}],"
      + "\"open_seats\": \"8\","
      + "\"waitlist\": \"01\","
      + "\"instructors\": [\"Shane Walsh\"]"
      + "}]";

  String secondSection = "[{"
      + "\"course\": \"AAST200\","
      + "\"section_id\": \"AAST200-0101\","
      + "\"semester\": \"202008\","
      + "\"number\": \"0101\","
      + "\"seats\": \"40\","
      + "\"meetings\": [{"
      + "  \"days\": \"TuTh\","
      + "  \"room\": \"1103\","
      + "  \"building\": \"SQH\","
      + "  \"classtype\": \"\","
      + "  \"start_time\": \"3:30pm\","
      + "  \"end_time\": \"4:45pm\""
      + "}, {"
      + "  \"days\": \"TuTh\","
      + "  \"room\": \"ONLINE\","
      + "  \"building\": \"\","
      + "  \"classtype\": \"\","
      + "  \"start_time\": \"3:30pm\","
      + "  \"end_time\": \"4:45pm\""
      + "}],"
      + "\"open_seats\": \"0\","
      + "\"waitlist\": \"80\","
      + "\"instructors\": [\"Terry Park\"]"
      + "}]";

  @Before
  public void before() throws Exception {
    mockedRequest = mock(HttpServletRequest.class);
    mockedResponse = mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    parser = new JSONParser();
    when(mockedResponse.getWriter()).thenReturn(writer);
    datastore = DatastoreServiceFactory.getDatastoreService();
    apiUtil = mock(ApiUtil.class);
    when(mockedRequest.getParameter("department")).thenReturn("AASP");

    firstCoursesJson = (JSONArray) parser.parse(firstCourses);
    firstCourseJson = (JSONArray) parser.parse(firstCourse);
    firstSectionJson = (JSONArray) parser.parse(firstSection);
    secondSectionJson = (JSONArray) parser.parse(secondSection);
    emptyJson = (JSONArray) parser.parse("[]");

    helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    helper.setUp();
  }

  @After
  public void after() {
    writer.flush();
    helper.tearDown();
  }

  @Test
  public void servletResponseHasCourse() throws Exception {
    String expectedJsonResponse = "[{"
        + "\"course_id\":\"AASP100\","
        + "\"coreqs\":null,"
        + "\"additional_info\":null,"
        + "\"restrictions\":null,"
        + "\"credit_granted_for\":null,"
        + "\"prereqs\":null,"
        + "\"credits\":3,"
        + "\"name\":\"Introduction to African American Studies\","
        + "\"description\":\"Significant aspects of the history of African Americans.\","
        + "\"semester\":\"202008\","
        + "\"dept_id\":\"AASP\","
        + "\"section_ids\":["
        + "  \"AASP100-0101\","
        + "  \"AASP100-0201\","
        + "  \"AASP100-0301\","
        + "  \"AASP100-0401\","
        + "  \"AASP100-0501\","
        + "  \"AASP100-0601\","
        + "  \"AASP100-0701\""
        + "]"
        + "}]";

    // Add course to datastore.
    when(apiUtil.getJsonArray(any(URI.class)))
        .thenReturn(firstCourseJson, firstSectionJson, emptyJson);
    DatastoreServlet datastoreServlet = new DatastoreServlet(datastore, apiUtil);
    datastoreServlet.doPost(null, mockedResponse);

    CourseListServlet servlet = new CourseListServlet(datastore);
    servlet.doGet(mockedRequest, mockedResponse);
    JSONObject responseObj = (JSONObject) parser.parse(stringWriter.toString());
    JSONArray coursesDetailed = (JSONArray) responseObj.get("courses");

    // Tests that response[courses] exists
    Assert.assertNotNull(coursesDetailed);
    // Checks that the correct number of JSON Objects are contained
    Assert.assertEquals(1, coursesDetailed.size());
    // Checks whether output is correct
    JSONAssert.assertEquals(
        expectedJsonResponse, coursesDetailed.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void servletResponseHasCourses() throws Exception {
    String expectedJsonResponse = "[{"
        + "\"course_id\":\"AASP100\","
        + "\"coreqs\":null,"
        + "\"additional_info\":null,"
        + "\"restrictions\":null,"
        + "\"credit_granted_for\":null,"
        + "\"prereqs\":null,"
        + "\"credits\":3,"
        + "\"name\":\"Introduction to African American Studies\","
        + "\"description\":\"Significant aspects of the history of African Americans.\","
        + "\"semester\":\"202008\","
        + "\"dept_id\":\"AASP\","
        + "\"section_ids\":["
        + "  \"AASP100-0101\","
        + "  \"AASP100-0201\","
        + "  \"AASP100-0301\","
        + "  \"AASP100-0401\","
        + "  \"AASP100-0501\","
        + "  \"AASP100-0601\","
        + "  \"AASP100-0701\""
        + "]"
        + "},{"
        + "\"course_id\":\"AASP100H\","
        + "\"coreqs\":null,"
        + "\"additional_info\":null,"
        + "\"restrictions\":null,"
        + "\"credit_granted_for\":null,"
        + "\"prereqs\":null,"
        + "\"credits\":3,"
        + "\"name\":\"Introduction to African American Studies\","
        + "\"description\":\"Significant aspects of the history of African Americans.\","
        + "\"semester\":\"202008\","
        + "\"dept_id\":\"AASP\","
        + "\"section_ids\":["
        + "  \"AASP100H-0101\""
        + "]"
        + "}]";

    // Add course to datastore.
    when(apiUtil.getJsonArray(any(URI.class)))
        .thenReturn(firstCoursesJson, firstSectionJson, secondSectionJson, emptyJson);
    DatastoreServlet datastoreServlet = new DatastoreServlet(datastore, apiUtil);
    datastoreServlet.doPost(null, mockedResponse);

    CourseListServlet servlet = new CourseListServlet(datastore);
    servlet.doGet(mockedRequest, mockedResponse);
    JSONObject responseObj = (JSONObject) parser.parse(stringWriter.toString());
    JSONArray coursesDetailed = (JSONArray) responseObj.get("courses");

    // Tests that response[courses] exists
    Assert.assertNotNull(coursesDetailed);
    // Checks that the correct number of JSON Objects are contained
    Assert.assertEquals(2, coursesDetailed.size());
    // Checks whether output is correct
    JSONAssert.assertEquals(
        expectedJsonResponse, coursesDetailed.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void returnsError400() throws Exception {
    ApiUtil apiUtil = mock(ApiUtil.class);
    when(mockedRequest.getParameter("department")).thenReturn(null);
    CourseListServlet servlet = new CourseListServlet(datastore);
    servlet.doGet(mockedRequest, mockedResponse);
    // Verifies whether status was set to SC_BAD_REQUEST
    verify(mockedResponse, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockedResponse, never()).setStatus(not(eq(HttpServletResponse.SC_BAD_REQUEST)));
    JSONObject responseJson = (JSONObject) parser.parse(stringWriter.toString());
    String expectedJson = "{\"message\":\"Invalid or missing department.\",\"status\":\"error\"}";
    JSONAssert.assertEquals(expectedJson, responseJson.toString(), JSONCompareMode.STRICT);
  }
}
