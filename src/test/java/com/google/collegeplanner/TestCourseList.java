package com.google.collegeplanner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.collegeplanner.servlets.CourseListServlet;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

/** Tests CourseListServlet */
@RunWith(JUnit4.class)
public final class TestCourseList {
  @Test
  public void servletResponseHasCourses() throws Exception {
HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(mockedResponse.getWriter()).thenReturn(writer);
    CourseListServlet servlet = new CourseListServlet();
    servlet.doGet(null, mockedResponse);
    writer.flush();
    JSONParser parser = new JSONParser();
    JSONObject responseObj = (JSONObject) parser.parse(stringWriter.toString());
    JSONArray coursesDetailed = (JSONArray) responseObj.get("courses_detailed");
    // Tests that courses_detailed exists
    Assert.assertNotNull(coursesDetailed);
    // Checks that the correct number of JSON Objects are contained
    Assert.assertEquals(coursesDetailed.size(), 30);
    // Checks whether the first one is CMSC101
    String firstCourse = coursesDetailed.get(0).toString();
    String expectedJson = "{"+
      "\"course_id\":\"AASP100\"," +
      "\"core\":[\"SH\",\"D\"]," +
      "\"relationships\":{" +
        "\"coreqs\":null," +
        "\"additional_info\":null," +
        "\"restrictions\":null," +
        "\"credit_granted_for\":null," +
        "\"also_offered_as\":null," +
        "\"formerly\":null," +
        "\"prereqs\":null" +
      "}," +
      "\"credits\":\"3\"," +
      "\"name\":\"Introduction to African American Studies\"," +
      "\"description\":\"Significant aspects of the history of African Americans " +
        "with particular emphasis on the evolution and development of black communities " +
        "from slavery to the present. Interdisciplinary introduction to social, " +
        "political, legal and economic roots of contemporary problems faced by blacks " +
        "in the United States with applications to the lives of other racial and ethnic " +
        "minorities in the Americas and in other societies.\"," +
      "\"semester\":\"202008\"," +
      "\"gen_ed\":[[\"DSHS\",\"DVUP\"]]," +
      "\"dept_id\":\"AASP\"," +
      "\"department\":\"African American Studies\"," +
      "\"grading_method\":[\"Regular\",\"Pass-Fail\",\"Audit\"]," +
      "\"sections\":[" +
        "\"AASP100-0101\"," +
        "\"AASP100-0201\"," +
        "\"AASP100-0301\"," +
        "\"AASP100-0401\"," +
        "\"AASP100-0501\"," +
        "\"AASP100-0601\"," +
        "\"AASP100-0701\"" +
      "]"+
    "}";
    Assert.assertEquals(firstCourse, expectedJson);
  }
}
