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

import com.google.collegeplanner.servlets.ApiUtil;
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

  @Before
  public void before() throws Exception {
    mockedResponse = mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    parser = new JSONParser();
    when(mockedResponse.getWriter()).thenReturn(writer);

    mockedRequest = mock(HttpServletRequest.class);
    when(mockedRequest.getParameter("course_id")).thenReturn("AASP100");
  }

  @After
  public void after() {
    writer.flush();
  }

  @Test
  public void servletResponseHasDepartments() throws Exception {
    String expectedJson = "[{"
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
    JSONParser parser = new JSONParser();
    JSONArray sectionJson = (JSONArray) parser.parse(expectedJson);
    ApiUtil apiUtil = mock(ApiUtil.class);
    when(apiUtil.getJsonArray(any(URI.class))).thenReturn(sectionJson);
    SectionServlet servlet = new SectionServlet(apiUtil);
    servlet.doGet(mockedRequest, mockedResponse);

    JSONObject responseObj = (JSONObject) parser.parse(stringWriter.toString());
    JSONArray sections = (JSONArray) responseObj.get("sections");

    // Tests that response["sections"] exists
    Assert.assertNotNull(sections);
    // Checks that the correct number of JSON Objects are contained
    Assert.assertEquals(sections.size(), 1);
    // Checks whether the first one is correct
    JSONAssert.assertEquals(expectedJson, sections.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void returnsErrorJson() throws Exception {
    ApiUtil apiUtil = mock(ApiUtil.class);
    when(apiUtil.getJsonArray(any(URI.class))).thenReturn(null);
    SectionServlet servlet = new SectionServlet(apiUtil);
    servlet.doGet(mockedRequest, mockedResponse);
    // Verifies whether status was set to SC_INTERNAL_SERVER_ERROR
    verify(mockedResponse, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    verify(mockedResponse, never())
        .setStatus(not(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)));
    JSONParser parser = new JSONParser();
    JSONObject responseJson = (JSONObject) parser.parse(stringWriter.toString());
    String expectedJson = "{\"message\":\"Internal server error.\",\"status\":\"error\"}";
    JSONAssert.assertEquals(expectedJson, responseJson.toString(), JSONCompareMode.STRICT);
  }
}
