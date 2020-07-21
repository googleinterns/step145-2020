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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.collegeplanner.servlets.PlannerPageServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests PlannerPageServlet */
@RunWith(JUnit4.class)
public final class PlannerPageTest {
  private final LocalServiceTestHelper helper =
      spy(new LocalServiceTestHelper(new LocalUserServiceTestConfig()));

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void servletResponseIsCorrect() throws IOException, ServletException {
    helper.setEnvIsLoggedIn(true);
    helper.setEnvEmail("hi@gmail.com");
    helper.setEnvAuthDomain("");
    RequestDispatcher mockRequestDispatcher = mock(RequestDispatcher.class);
    HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    when(mockedRequest.getRequestDispatcher("planner.jsp")).thenReturn(mockRequestDispatcher);
    HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    PlannerPageServlet servlet = new PlannerPageServlet();
    servlet.doGet(mockedRequest, mockedResponse);
    verify(mockedRequest, times(1)).setAttribute("userEmail", "hi@gmail.com");
  }
}
