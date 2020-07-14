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

import com.google.collegeplanner.servlets.ApiUtil;
import com.google.collegeplanner.servlets.DepartmentServlet;
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

/** Tests DepartmentServlet */
@RunWith(JUnit4.class)
public final class DepartmentTest {
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
  }

  @After
  public void after() {
    writer.flush();
  }

  @Test
  public void servletResponseHasDepartments() throws Exception {
    DepartmentServlet servlet = new DepartmentServlet();
    servlet.doGet(null, mockedResponse);

    JSONObject responseObj = (JSONObject) parser.parse(stringWriter.toString());
    JSONArray departmentsDetailed = (JSONArray) responseObj.get("departments");

    // Tests that departments_detailed exists
    Assert.assertNotNull(departmentsDetailed);
    // Checks that the correct number of JSON Objects are contained
    Assert.assertEquals(departmentsDetailed.size(), 199);
    // Checks whether the first one is correct
    String expectedJson = "{\"dept_id\":\"AAPS\",\"department\":\"Academic Achievement Programs\"}";
    JSONAssert.assertEquals(
        expectedJson, departmentsDetailed.get(0).toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void returnsErrorJson() throws Exception {
    ApiUtil apiUtil = mock(ApiUtil.class);
    when(apiUtil.getJsonArray(any(URI.class))).thenReturn(null);
    DepartmentServlet servlet = new DepartmentServlet(apiUtil);
    servlet.doGet(null, mockedResponse);

    JSONParser parser = new JSONParser();
    JSONObject responseJson = (JSONObject) parser.parse(stringWriter.toString());
    String expectedJson = "{\"message\":\"Internal server error.\",\"status\":\"error\"}";
    JSONAssert.assertEquals(expectedJson, responseJson.toString(), JSONCompareMode.STRICT);
  }
}
