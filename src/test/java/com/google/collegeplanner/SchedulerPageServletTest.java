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

import com.google.collegeplanner.servlets.SchedulerPageServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests SchedulerPageServlet */
@RunWith(JUnit4.class)
public final class SchedulerPageServletTest {
  @Test
  public void servletResponseIsCorrect() throws IOException, ServletException {
    RequestDispatcher mockRequestDispatcher = mock(RequestDispatcher.class);
    HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    when(mockedRequest.getRequestDispatcher("scheduler.jsp")).thenReturn(mockRequestDispatcher);
    HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    SchedulerPageServlet servlet = new SchedulerPageServlet();
    servlet.doGet(mockedRequest, mockedResponse);
  }
}
