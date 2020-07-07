package com.google.collegeplanner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.collegeplanner.servlets.CourseListServlet;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
    Assert.assertEquals(coursesDetailed.size(), 18);
    // Checks whether the first one is CMSC101
    String firstCourse = coursesDetailed.get(0).toString();
    Assert.assertEquals(firstCourse, "{\"name\":\"CMSC101\"}");
  }
}
