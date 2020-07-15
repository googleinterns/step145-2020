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

import static org.mockito.Mockito.mock;
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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/** Tests PlannerServlet */
@RunWith(JUnit4.class)
public final class PlannerTest {
  @Test
  public void servletResponseIsCorrect() throws Exception {
    HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    String test =
        "{\"selectedClasses\":[{\"course_id\":\"AGNR499\",\"relationships\":{\"coreqs\":\"aslfkjs s\",\"prereqs\":\" and s \"},"
        + "\"credits\":\"1\",\"name\":\"Special Problems; Special Problems\",\"dept_id\":\"AGNR\",\"department\":\"Agriculture and Natural Resources\"},{\"course_id\":\"CMSC101\",\"relationships\":{\"coreqs\":\"CMSC120 s\",\"prereqs\":\" and hi AGNR499 \"},"
        + "\"credits\":\"3\",\"name\":\"Special Problems; Special Problems\",\"dept_id\":\"AGNR\",\"department\":\"Agriculture and Natural Resources\"},{\"course_id\":\"CMSC106\",\"relationships\":{\"coreqs\":\"aslfkjs s\",\"prereqs\":\" and hi AGNR499 \"},"
        + "\"credits\":\"3\",\"name\":\"Special Problems; Special Problems\",\"dept_id\":\"AGNR\",\"department\":\"Agriculture and Natural Resources\"},{\"course_id\":\"CMSC120\",\"relationships\":{\"coreqs\":\"CMSC101 s\",\"prereqs\":\" and hi  \"},"
        + "\"credits\":\"3\",\"name\":\"Special Problems; Special Problems\",\"dept_id\":\"AGNR\",\"department\":\"Agriculture and Natural Resources\"},{\"course_id\":\"HELL120\",\"relationships\":{\"coreqs\":\"aslfkjs s\",\"prereqs\":\" and hi CMSC106 and CMSC120 \"},"
        + "\"credits\":\"3\",\"name\":\"Special Problems; Special Problems\",\"dept_id\":\"AGNR\",\"department\":\"Agriculture and Natural Resources\"}], \"semesters\": \"3\"}";
    Reader inputString = new StringReader(test);
    BufferedReader reader = new BufferedReader(inputString);
    when(mockedRequest.getReader()).thenReturn(reader);
    HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(mockedResponse.getWriter()).thenReturn(writer);

    PlannerServlet servlet = new PlannerServlet();
    servlet.doPost(mockedRequest, mockedResponse);
    writer.flush();
    // Check whether the string output is correct
    JSONAssert.assertEquals(stringWriter.toString(),
        "[[\"AGNR499\"], [ \"CMSC106\",\"CMSC120\", \"CMSC101\"], [\"HELL120\"]]",
        JSONCompareMode.STRICT);
  }
}
