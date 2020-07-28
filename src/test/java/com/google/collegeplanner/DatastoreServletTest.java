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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.collegeplanner.servlets.ApiUtil;
import com.google.collegeplanner.servlets.DatastoreServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.utils.URIBuilder;
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

/** Tests DatastoreServlet */
@RunWith(JUnit4.class)
public final class DatastoreServletTest {
  HttpServletResponse response;
  StringWriter stringWriter;
  PrintWriter writer;
  JSONParser parser;
  DatastoreService datastore;
  ApiUtil apiUtil;

  JSONArray firstCourseJson;
  JSONArray firstSectionJson;
  JSONArray secondCourseJson;
  JSONArray secondSectionJson;
  JSONArray emptyJson;

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

  String secondCourse = "[{"
      + "\"course_id\": \"AAST200\","
      + "\"semester\": \"202008\","
      + "\"name\": \"Introduction to Asian American Studies\","
      + "\"dept_id\": \"AAST\","
      + "\"department\": \"Asian American Studies\","
      + "\"credits\": \"3\","
      + "\"description\": \"The aggregate experience of Asian Pacific Americans.\","
      + "\"grading_method\": [\"Regular\", \"Pass-Fail\"],"
      + "\"gen_ed\": ["
      + "  [\"DSHS\", \"DVUP\"]"
      + "],"
      + "\"core\": [\"SB\", \"D\"],"
      + "\"relationships\": {"
      + "\"coreqs\": null,"
      + "\"prereqs\": null,"
      + "\"formerly\": null,"
      + "\"restrictions\": null,"
      + "\"additional_info\": \"Cross-listed with: AMST298C.\","
      + "\"also_offered_as\": null,"
      + "\"credit_granted_for\": \"AAST200 or AMST298C.\""
      + "},"
      + "\"sections\": [\"AAST200-0101\"]"
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

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void before() throws Exception {
    datastore = DatastoreServiceFactory.getDatastoreService();
    response = mock(HttpServletResponse.class);
    apiUtil = mock(ApiUtil.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    parser = new JSONParser();
    when(response.getWriter()).thenReturn(writer);

    firstCourseJson = (JSONArray) parser.parse(firstCourse);
    firstSectionJson = (JSONArray) parser.parse(firstSection);
    secondCourseJson = (JSONArray) parser.parse(secondCourse);
    secondSectionJson = (JSONArray) parser.parse(secondSection);
    emptyJson = (JSONArray) parser.parse("[]");

    helper.setUp();
  }

  @After
  public void after() {
    helper.tearDown();
  }

  @Test
  public void servletAddsObjectToDatastore() throws Exception {
    // We're simulating having one course and one section. After they're added to datastore, return
    // a blank array to signal that there are no more courses left.
    when(apiUtil.getJsonArray(any(URI.class)))
        .thenReturn(firstCourseJson, firstSectionJson, emptyJson);
    DatastoreServlet ds = new DatastoreServlet(datastore, apiUtil);
    ds.doPost(null, response);

    Assert.assertEquals(1, datastore.prepare(new Query("Course")).countEntities());
  }

  @Test
  public void testMultiplePages() throws Exception {
    when(apiUtil.getJsonArray(any(URI.class)))
        .thenReturn(
            firstCourseJson, firstSectionJson, secondCourseJson, secondSectionJson, emptyJson);
    DatastoreServlet ds = new DatastoreServlet(datastore, apiUtil);
    ds.doPost(null, response);

    Assert.assertEquals(2, datastore.prepare(new Query("Course")).countEntities());
  }
}
