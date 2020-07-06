package com.google;

import static org.mockito.Mockito.*;

import com.google.servlets.CourseListServlet;
import java.io.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
  public void servletResponseHasCourses() {
    HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    try {
      when(mockedResponse.getWriter()).thenReturn(writer);
      CourseListServlet servlet = new CourseListServlet();
      servlet.doGet(null, mockedResponse);
      writer.flush();
      JSONParser parser = new JSONParser();
      JSONObject responseObj = (JSONObject) parser.parse(stringWriter.toString());
      Assert.assertNotNull(responseObj.get("courses_detailed"));
    } catch (Exception e) {
      Assert.fail("Course retreival failed");
    }
  }
}
