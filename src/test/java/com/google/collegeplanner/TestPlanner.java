package com.google.collegeplanner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.collegeplanner.servlets.PlannerServlet;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests CourseListServlet */
@RunWith(JUnit4.class)
public final class TestPlanner {
  @Test
  public void servletResponseIsCorrect() throws Exception {
    HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    when(mockedRequest.getParameter("selectedClasses")).thenReturn("CMSC101,CMSC106,CMSC122,CMSC131");
    HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(mockedResponse.getWriter()).thenReturn(writer);

    PlannerServlet servlet = new PlannerServlet();
    servlet.doGet(mockedRequest, mockedResponse);
    writer.flush();
    // Check whether the string output is correct
    Assert.assertEquals(stringWriter.toString().trim(), "[[\"CMSC101\",\"CMSC106\",\"CMSC122\",\"CMSC131\"]]");
  }
}
