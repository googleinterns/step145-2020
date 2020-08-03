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
import com.google.collegeplanner.servlets.DatastoreServlet;
import com.google.collegeplanner.servlets.SectionServlet;
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

/** Tests SectionServlet */
@RunWith(JUnit4.class)
public final class SectionTest {
  HttpServletRequest mockedRequest;
  HttpServletResponse mockedResponse;
  StringWriter stringWriter;
  PrintWriter writer;
  JSONParser parser;
  DatastoreService datastore;
  LocalServiceTestHelper helper;
  ApiUtil apiUtil;

  JSONArray firstCourseJson;
  JSONArray firstSectionJson;
  JSONArray emptyJson;
  JSONArray multipleMeetingsJson;

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
      + "}],"
      + "\"open_seats\": \"8\","
      + "\"waitlist\": \"01\","
      + "\"instructors\": [\"Shane Walsh\"]"
      + "}]";

  String firstSectionMultipleMeetings = "[{"
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

  @Before
  public void before() throws Exception {
    mockedResponse = mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    parser = new JSONParser();
    when(mockedResponse.getWriter()).thenReturn(writer);
    apiUtil = mock(ApiUtil.class);

    mockedRequest = mock(HttpServletRequest.class);
    when(mockedRequest.getParameter("course_id")).thenReturn("AASP100");
    when(mockedRequest.getParameter("section_id")).thenReturn("AASP100-0101");

    datastore = DatastoreServiceFactory.getDatastoreService();
    helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    helper.setUp();

    firstCourseJson = (JSONArray) parser.parse(firstCourse);
    firstSectionJson = (JSONArray) parser.parse(firstSection);
    multipleMeetingsJson = (JSONArray) parser.parse(firstSectionMultipleMeetings);
    emptyJson = (JSONArray) parser.parse("[]");
  }

  @After
  public void after() {
    writer.flush();
    helper.tearDown();
  }

  @Test
  public void servletResponseHasSections() throws Exception {
    String expectedJson = "[{"
        + "\"course_id\": \"AASP100\","
        + "\"section_id\": \"AASP100-0101\","
        + "\"seats\": 21,"
        + "\"meetings\": [{"
        + "  \"days\": \"MWF\","
        + "  \"room\": \"1101\","
        + "  \"building\": \"SQH\","
        + "  \"start_time\": 600,"
        + "  \"end_time\": 650"
        + "},{"
        + "  \"days\": \"MWF\","
        + "  \"room\": \"2205\","
        + "  \"building\": \"LEF\","
        + "  \"start_time\": 600,"
        + "  \"end_time\": 650"
        + "}],"
        + "\"open_seats\": 8,"
        + "\"waitlist\": \"01\","
        + "\"instructors\": [\"Shane Walsh\"]"
        + "}]";

    // Add course to datastore.
    when(apiUtil.getJsonArray(any(URI.class)))
        .thenReturn(firstCourseJson, multipleMeetingsJson, emptyJson);
    DatastoreServlet datastoreServlet = new DatastoreServlet(datastore, apiUtil);
    datastoreServlet.doPost(null, mockedResponse);

    SectionServlet servlet = new SectionServlet(datastore);
    servlet.doGet(mockedRequest, mockedResponse);
    JSONObject responseObj = (JSONObject) parser.parse(stringWriter.toString());
    JSONArray sections = (JSONArray) responseObj.get("sections");

    // Tests that response["sections"] exists
    Assert.assertNotNull(sections);
    // Checks that the correct number of JSON Objects are contained
    Assert.assertEquals(1, sections.size());
    // Checks whether the first one is correct
    JSONAssert.assertEquals(expectedJson, sections.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void servletResponseHasSection() throws Exception {
    String expectedJson = "[{"
        + "\"course_id\": \"AASP100\","
        + "\"section_id\": \"AASP100-0101\","
        + "\"seats\": 21,"
        + "\"meetings\": [{"
        + "  \"days\": \"MWF\","
        + "  \"room\": \"1101\","
        + "  \"building\": \"SQH\","
        + "  \"start_time\": 600,"
        + "  \"end_time\": 650"
        + "}],"
        + "\"open_seats\": 8,"
        + "\"waitlist\": \"01\","
        + "\"instructors\": [\"Shane Walsh\"]"
        + "}]";

    // Add course to datastore.
    when(apiUtil.getJsonArray(any(URI.class)))
        .thenReturn(firstCourseJson, firstSectionJson, emptyJson);
    DatastoreServlet datastoreServlet = new DatastoreServlet(datastore, apiUtil);
    datastoreServlet.doPost(null, mockedResponse);

    SectionServlet servlet = new SectionServlet(datastore);
    servlet.doGet(mockedRequest, mockedResponse);
    JSONObject responseObj = (JSONObject) parser.parse(stringWriter.toString());
    JSONArray sections = (JSONArray) responseObj.get("sections");

    // Tests that response["sections"] exists
    Assert.assertNotNull(sections);
    // Checks that the correct number of JSON Objects are contained
    Assert.assertEquals(1, sections.size());
    // Checks whether the first one is correct
    JSONAssert.assertEquals(expectedJson, sections.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void returnsError400() throws Exception {
    when(mockedRequest.getParameter("course_id")).thenReturn(null);
    SectionServlet servlet = new SectionServlet(datastore);
    servlet.doGet(mockedRequest, mockedResponse);
    // Verifies whether status was set to SC_BAD_REQUEST
    verify(mockedResponse, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockedResponse, never()).setStatus(not(eq(HttpServletResponse.SC_BAD_REQUEST)));
    JSONObject responseJson = (JSONObject) parser.parse(stringWriter.toString());
    String expectedJson = "{\"message\":\"Invalid or missing course id.\",\"status\":\"error\"}";
    JSONAssert.assertEquals(expectedJson, responseJson.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void returnsError404() throws Exception {
    SectionServlet servlet = new SectionServlet(datastore);
    servlet.doGet(mockedRequest, mockedResponse);
    // Verifies whether status was set to SC_NOT_FOUND when the query returns no results
    verify(mockedResponse, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
    verify(mockedResponse, never()).setStatus(not(eq(HttpServletResponse.SC_NOT_FOUND)));
    JSONObject responseJson = (JSONObject) parser.parse(stringWriter.toString());
    String expectedJson = "{\"message\":\"Not found.\",\"status\":\"error\"}";
    JSONAssert.assertEquals(expectedJson, responseJson.toString(), JSONCompareMode.STRICT);
  }
}
