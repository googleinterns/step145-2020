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

/** Tests CourseListServlet */
@RunWith(JUnit4.class)
public final class DatastoreTest {
  HttpServletResponse response;
  StringWriter stringWriter;
  PrintWriter writer;
  JSONParser parser;
  DatastoreService datastore;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void before() throws Exception {
    datastore = DatastoreServiceFactory.getDatastoreService();
    response = mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);
    helper.setUp();
  }

  @After
  public void after() {
    helper.tearDown();
  }

  /* Run this test twice to prove we're not leaking any state across tests. */
  @Test
  public void testDatastoreInsertion1() {
    doTest();
  }

  @Test
  public void testDatastoreInsertion2() {
    doTest();
  }

  @Test
  public void servletAddsObjectToDatastore() throws Exception {
    ApiUtil apiUtil = mock(ApiUtil.class);
    JSONParser parser = new JSONParser();

    String courses = "[{"
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
        + "\"description\":\"Significant aspects of the history of African Americans "
        + "with particular emphasis on the evolution and development of black communities "
        + "from slavery to the present. Interdisciplinary introduction to social, "
        + "political, legal and economic roots of contemporary problems faced by blacks "
        + "in the United States with applications to the lives of other racial and ethnic "
        + "minorities in the Americas and in other societies.\","
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

    String sections = "[{"
        + "\"course\": \"AASP100\","
        + "\"section_id\": \"AASP100-0101\","
        + "\"semester\": \"202008\","
        + "\"number\": \"0101\","
        + "\"seats\": \"21\","
        + "\"meetings\": [{"
        + "\"days\": \"MWF\","
        + "\"room\": \"1101\","
        + "\"building\": \"SQH\","
        + "\"classtype\": \"\","
        + "\"start_time\": \"10:00am\","
        + "\"end_time\": \"10:50am\""
        + "}, {"
        + "\"days\": \"MWF\","
        + "\"room\": \"2205\","
        + "\"building\": \"LEF\","
        + "\"classtype\": \"\","
        + "\"start_time\": \"10:00am\","
        + "\"end_time\": \"10:50am\""
        + "}],"
        + "\"open_seats\": \"8\","
        + "\"waitlist\": \"01\","
        + "\"instructors\": [\"Shane Walsh\"]"
        + "}]";

    JSONArray coursesJson = (JSONArray) parser.parse(courses);
    JSONArray sectionsJson = (JSONArray) parser.parse(sections);
    JSONArray emptyJson = (JSONArray) parser.parse("[]");

    when(apiUtil.getJsonArray(any(URI.class))).thenReturn(coursesJson, sectionsJson, emptyJson);

    DatastoreServlet ds = new DatastoreServlet(datastore, apiUtil);
    ds.doPost(null, response);

    Assert.assertEquals(1, datastore.prepare(new Query("Course")).countEntities());
  }

  private void insertionTest() {
    Assert.assertEquals(0, datastore.prepare(new Query("Course")).countEntities());
    datastore.put(new Entity("Course"));
    datastore.put(new Entity("Course"));
    Assert.assertEquals(2, datastore.prepare(new Query("Course")).countEntities());
  }
}
