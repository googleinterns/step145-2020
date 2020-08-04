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

import com.google.collegeplanner.servlets.PlannerServlet;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/** Tests PlannerServlet */
@RunWith(JUnit4.class)
public final class PlannerServletTest {
  HttpServletRequest mockedRequest;
  HttpServletResponse mockedResponse;
  StringWriter stringWriter;
  PrintWriter writer;
  @Before
  public void before() throws Exception {
    mockedResponse = mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    when(mockedResponse.getWriter()).thenReturn(writer);

    mockedRequest = mock(HttpServletRequest.class);
  }

  @After
  public void after() {
    writer.flush();
  }

  @Test
  public void servletResponseIsValid() throws Exception {
    String test = "{\"selectedClasses\":[{"
        + "\"course_id\":\"AASP100\","
        + "\"relationships\":{"
        + "  \"coreqs\":null,"
        + "  \"prereqs\":null"
        + "},"
        + "\"credits\":3,"
        + "},"
        + "{"
        + "\"course_id\":\"CMSC106\","
        + "\"coreqs\":\"Take with CMSC120\","
        + "\"prereqs\":\"Must have taken AASP100\","
        + "\"credits\":3,"
        + "},"
        + "{"
        + "\"course_id\":\"CMSC120\","
        + "\"coreqs\":\"Take with CMSC106\","
        + "\"prereqs\":null,"
        + "\"credits\":4,"
        + "},"
        + "{"
        + "\"course_id\":\"CMSC122\","
        + "\"coreqs\":null,"
        + "\"prereqs\":\"AASP100 is a prerequisite\","
        + "\"credits\":3,"
        + "},"
        + "{"
        + "\"course_id\":\"AGNR499\","
        + "\"coreqs\":null,"
        + "\"prereqs\":\"Need to take CMSC106\","
        + "\"credits\":3,"
        + "},"
        + "],"
        + "\"semesters\": \"3\"}";
    Reader inputString = new StringReader(test);
    BufferedReader reader = new BufferedReader(inputString);
    when(mockedRequest.getReader()).thenReturn(reader);

    PlannerServlet servlet = new PlannerServlet();
    servlet.doPost(mockedRequest, mockedResponse);
    // Check whether the string output is correct
    JSONAssert.assertEquals(stringWriter.toString(),
        "{\"semester_plan\":[[\"AASP100\"], [ \"CMSC106\",\"CMSC120\", \"CMSC122\"], [\"AGNR499\"]], \"semester_credits\": [3, 10, 3]}",
        JSONCompareMode.STRICT);
  }

  @Test
  public void servletReturnsErrorWhenCycle() throws Exception {
    String test = "{\"selectedClasses\":[{"
        + "\"course_id\":\"AASP100\","
        + "\"coreqs\":null,"
        + "\"prereqs\":\"Must have taken CMSC106\","
        + "\"credits\":3,"
        + "},"
        + "{"
        + "\"course_id\":\"CMSC106\","
        + "\"relationships\":{"
        + "  \"coreqs\":null,"
        + "  \"prereqs\":\"Must have taken AASP100\""
        + "}],"
        + "\"semesters\": \"3\"}";
    Reader inputString = new StringReader(test);
    BufferedReader reader = new BufferedReader(inputString);
    when(mockedRequest.getReader()).thenReturn(reader);

    PlannerServlet servlet = new PlannerServlet();
    servlet.doPost(mockedRequest, mockedResponse);
    // Verifies whether status was set to SC_BAD_REQUEST
    verify(mockedResponse, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockedResponse, never()).setStatus(not(eq(HttpServletResponse.SC_BAD_REQUEST)));
    // Check whether the string output is correct
    JSONAssert.assertEquals(stringWriter.toString(),
        "{\"message\":\"Invalid body for POST request.\",\"status\":\"error\"}",
        JSONCompareMode.STRICT);
  }

  @Test
  public void servletReturnsErrorWhenNotEnoughSemesters() throws Exception {
    HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    String test = "{\"selectedClasses\":[{"
        + "\"course_id\":\"AASP100\","
        + "\"coreqs\":null,"
        + "\"prereqs\":null,"
        + "\"credits\":3,"
        + "},"
        + "{"
        + "\"course_id\":\"CMSC106\","
        + "\"coreqs\":\"Take with CMSC120\","
        + "\"prereqs\":\"Must have taken AASP100\","
        + "\"credits\":3,"
        + "},"
        + "{"
        + "\"course_id\":\"CMSC120\","
        + "\"coreqs\":\"Take with CMSC106\","
        + "\"prereqs\":null,"
        + "\"credits\":4,"
        + "},"
        + "{"
        + "\"course_id\":\"CMSC122\","
        + "\"coreqs\":null,"
        + "\"prereqs\":\"AASP100 is a prerequisite\","
        + "\"credits\":3,"
        + "},"
        + "{"
        + "\"course_id\":\"AGNR499\","
        + "\"coreqs\":null,"
        + "\"prereqs\":\"Need to take CMSC106\","
        + "\"credits\":3,"
        + "},"
        + "],"
        + "\"semesters\": \"2\"}";
    Reader inputString = new StringReader(test);
    BufferedReader reader = new BufferedReader(inputString);
    when(mockedRequest.getReader()).thenReturn(reader);
    HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(mockedResponse.getWriter()).thenReturn(writer);

    PlannerServlet servlet = new PlannerServlet();
    servlet.doPost(mockedRequest, mockedResponse);
    // Check whether the string output is correct
    JSONAssert.assertEquals(stringWriter.toString(),
        "{\"semester_plan\":[], \"semester_credits\":[]}", JSONCompareMode.STRICT);
  }

  @Test
  public void servletReturnsErrorWhenInvalidInput() throws Exception {
    HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    BufferedReader reader = new BufferedReader(new StringReader(""));
    when(mockedRequest.getReader()).thenReturn(reader);

    PlannerServlet servlet = new PlannerServlet();
    servlet.doPost(mockedRequest, mockedResponse);
    // Verifies whether status was set to SC_BAD_REQUEST
    verify(mockedResponse, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockedResponse, never()).setStatus(not(eq(HttpServletResponse.SC_BAD_REQUEST)));
    // Check whether the string output is correct
    JSONAssert.assertEquals(stringWriter.toString(),
        "{\"message\":\"Invalid body for POST request.\",\"status\":\"error\"}",
        JSONCompareMode.STRICT);
  }

  @Test
  public void servletReturnsErrorWhenSemestersIsNotString() throws Exception {
    HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    String test = "{\"selectedClasses\":[ ], \"semesters\": 3}";
    Reader inputString = new StringReader(test);
    BufferedReader reader = new BufferedReader(inputString);
    when(mockedRequest.getReader()).thenReturn(reader);

    PlannerServlet servlet = new PlannerServlet();
    servlet.doPost(mockedRequest, mockedResponse);
    // Verifies whether status was set to SC_BAD_REQUEST
    verify(mockedResponse, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockedResponse, never()).setStatus(not(eq(HttpServletResponse.SC_BAD_REQUEST)));
    // Check whether the string output is correct
    JSONAssert.assertEquals(stringWriter.toString(),
        "{\"message\":\"Invalid body for POST request.\",\"status\":\"error\"}",
        JSONCompareMode.STRICT);
  }

  @Test
  public void servletIgnoresDuplicateCourses() throws Exception {
    String test = "{\"selectedClasses\":[{"
        + "\"course_id\":\"AASP100\","
        + "\"relationships\":{"
        + "  \"coreqs\":null,"
        + "  \"prereqs\":null"
        + "},"
        + "\"credits\":3,"
        + "},{"
        + "\"course_id\":\"AASP100\","
        + "  \"coreqs\":null,"
        + "  \"prereqs\":null,"
        + "\"credits\":3,"
        + "}"
        + "],"
        + "\"semesters\": \"1\"}";
    Reader inputString = new StringReader(test);
    BufferedReader reader = new BufferedReader(inputString);
    when(mockedRequest.getReader()).thenReturn(reader);

    PlannerServlet servlet = new PlannerServlet();
    servlet.doPost(mockedRequest, mockedResponse);
    // Check whether the string output is correct
    JSONAssert.assertEquals(stringWriter.toString(),
        "{\"semester_plan\":[[\"AASP100\"]], \"semester_credits\":[3]}", JSONCompareMode.STRICT);
  }
}
