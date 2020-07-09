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

/** Tests CourseListServlet */
@RunWith(JUnit4.class)
public final class TestPlanner {
  @Test
  public void servletResponseIsCorrect() throws Exception {
    HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    String test = "{\"selectedClasses\":\"CMSC101,CMSC106,CMSC122,CMSC131\", \"semesters\":4}";
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
    Assert.assertEquals(
        stringWriter.toString().trim(), "[[\"CMSC101\",\"CMSC106\",\"CMSC122\",\"CMSC131\"]]");
  }
}
