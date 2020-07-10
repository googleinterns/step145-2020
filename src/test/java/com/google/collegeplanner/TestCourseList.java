package com.google.collegeplanner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.collegeplanner.servlets.ApiUtil;
import com.google.collegeplanner.servlets.CourseListServlet;
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
public final class TestCourseList {
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
  public void servletResponseHasCourses() throws Exception {
    CourseListServlet servlet = new CourseListServlet();
    servlet.doGet(null, mockedResponse);

    JSONObject responseObj = (JSONObject) parser.parse(stringWriter.toString());
    JSONArray coursesDetailed = (JSONArray) responseObj.get("courses_detailed");

    // Tests that courses_detailed exists
    Assert.assertNotNull(coursesDetailed);
    // Checks that the correct number of JSON Objects are contained
    Assert.assertEquals(coursesDetailed.size(), 30);
    // Checks whether the first one is correct
    String expectedJson = "{"
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
        + "}";
    JSONAssert.assertEquals(
        expectedJson, coursesDetailed.get(0).toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void returnsErrorJson() throws Exception {
    ApiUtil apiUtil = mock(ApiUtil.class);
    when(apiUtil.getJsonArray(any(URI.class))).thenReturn(null);
    CourseListServlet servlet = new CourseListServlet(apiUtil);
    servlet.doGet(null, mockedResponse);

    JSONParser parser = new JSONParser();
    JSONObject responseJson = (JSONObject) parser.parse(stringWriter.toString());
    String expectedJson = "{\"message\":\"Internal server error.\",\"status\":\"error\"}";
    JSONAssert.assertEquals(expectedJson, responseJson.toString(), JSONCompareMode.STRICT);
  }
}
