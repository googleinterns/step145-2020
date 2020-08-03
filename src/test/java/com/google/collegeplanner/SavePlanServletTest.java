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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.collegeplanner.servlets.SavePlanServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.utils.URIBuilder;
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

/** Test SavePlanServlet */
@RunWith(JUnit4.class)
public final class SavePlanServletTest {
  HttpServletResponse response;
  HttpServletRequest request;
  StringWriter stringWriter;
  PrintWriter writer;
  JSONParser parser;
  DatastoreService datastore;
  GoogleIdTokenVerifier verifier;
  SavePlanServlet servlet;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void before() throws Exception {
    datastore = DatastoreServiceFactory.getDatastoreService();
    response = mock(HttpServletResponse.class);
    request = mock(HttpServletRequest.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    parser = new JSONParser();
    when(response.getWriter()).thenReturn(writer);
    verifier = mock(GoogleIdTokenVerifier.class);
    servlet = new SavePlanServlet(datastore, verifier);
    helper.setUp();
  }

  @After
  public void after() {
    helper.tearDown();
  }

  @Test
  public void servletAddsObjectToDatastore() throws Exception {
    // We're simulating a POST request to the servlet.
    String test = "{\"idToken\": \"PERSON_A\", \"planName\": \"plan1\","
        + " \"plan\": {\"semester_plan\":[[\"AASP100\"]], "
        + "\"semester_credits\": [3]}}";
    Reader inputString = new StringReader(test);
    BufferedReader reader = new BufferedReader(inputString);
    when(request.getReader()).thenReturn(reader);
    GoogleIdToken idToken = mock(GoogleIdToken.class);
    when(verifier.verify("PERSON_A")).thenReturn(idToken);
    Payload payload = mock(Payload.class);
    when(idToken.getPayload()).thenReturn(payload);
    when(payload.getEmail()).thenReturn("persona@gmail.com");
    servlet.doPost(request, response);
    Assert.assertEquals(1, datastore.prepare(new Query("Plan")).countEntities());
  }

  @Test
  public void postReturnsErrorInvalidToken() throws Exception {
    // We're simulating a POST request to the servlet.
    String test = "{\"idToken\": \"PERSON_A\", \"planName\": \"plan1\","
        + " \"plan\": {\"semester_plan\":[[\"AASP100\"]], "
        + "\"semester_credits\": [3]}}";
    Reader inputString = new StringReader(test);
    BufferedReader reader = new BufferedReader(inputString);
    when(request.getReader()).thenReturn(reader);
    when(verifier.verify("PERSON_A")).thenReturn(null);
    servlet.doPost(request, response);
    verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response, never()).setStatus(not(eq(HttpServletResponse.SC_UNAUTHORIZED)));
    // Check whether the string output is correct.
    JSONAssert.assertEquals(stringWriter.toString(),
        "{\"message\":\"Invalid user.\",\"status\":\"error\"}", JSONCompareMode.STRICT);
  }

  @Test
  public void postReturnsErrorInvalidBody() throws Exception {
    // We're simulating a POST request to the servlet.
    String test = "{}";
    Reader inputString = new StringReader(test);
    BufferedReader reader = new BufferedReader(inputString);
    when(request.getReader()).thenReturn(reader);
    servlet.doPost(request, response);
    verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(response, never()).setStatus(not(eq(HttpServletResponse.SC_BAD_REQUEST)));
    // Check whether the string output is correct.
    JSONAssert.assertEquals(stringWriter.toString(),
        "{\"message\":\"Invalid body for POST request.\",\"status\":\"error\"}",
        JSONCompareMode.STRICT);
  }

  @Test
  public void servletGetsOnlyUsersPlans() throws Exception {
    // Simulate two post requests from different users.
    String testA = "{\"idToken\": \"PERSON_A\", \"planName\": \"plan1\","
        + " \"plan\": {\"semester_plan\":[[\"AASP100\"]], "
        + "\"semester_credits\": [3]}}";
    Reader inputStringA = new StringReader(testA);
    BufferedReader readerA = new BufferedReader(inputStringA);
    when(request.getReader()).thenReturn(readerA);
    GoogleIdToken idTokenA = mock(GoogleIdToken.class);
    when(verifier.verify("PERSON_A")).thenReturn(idTokenA);
    Payload payloadA = mock(Payload.class);
    when(idTokenA.getPayload()).thenReturn(payloadA);
    when(payloadA.getEmail()).thenReturn("persona@gmail.com");
    servlet.doPost(request, response);
    Assert.assertEquals(1, datastore.prepare(new Query("Plan")).countEntities());

    String testB = "{\"idToken\": \"PERSON_B\", \"planName\": \"plan2\","
        + " \"plan\": {\"semester_plan\":[[\"CMSC101\"]], "
        + "\"semester_credits\": [4]}}";
    Reader inputStringB = new StringReader(testB);
    BufferedReader readerB = new BufferedReader(inputStringB);
    when(request.getReader()).thenReturn(readerB);
    GoogleIdToken idTokenB = mock(GoogleIdToken.class);
    when(verifier.verify("PERSON_B")).thenReturn(idTokenB);
    Payload payloadB = mock(Payload.class);
    when(idTokenB.getPayload()).thenReturn(payloadB);
    when(payloadB.getEmail()).thenReturn("personb@gmail.com");
    servlet.doPost(request, response);
    Assert.assertEquals(2, datastore.prepare(new Query("Plan")).countEntities());

    // Execute get request for PERSON_B.
    when(request.getParameter("idToken")).thenReturn("PERSON_B");
    servlet.doGet(request, response);
    JSONAssert.assertEquals(stringWriter.toString(),
        "{\"plans\":\"[{\\\"id\\\":2,"
            + "\\\"plan\\\":{\\\"semester_credits\\\":[4],"
            + "\\\"semester_plan\\\":[[\\\"CMSC101\\\"]]},"
            + "\\\"planName\\\":\\\"plan2\\\"}]\",\"user\":\"personb@gmail.com\"}",
        JSONCompareMode.STRICT);
  }

  @Test
  public void getReturnsErrorInvalidToken() throws Exception {
    // We're simulating a GET request to the servlet.
    when(request.getParameter("idToken")).thenReturn("PERSON_A");
    when(verifier.verify("PERSON_A")).thenReturn(null);
    servlet.doGet(request, response);
    verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response, never()).setStatus(not(eq(HttpServletResponse.SC_UNAUTHORIZED)));
    // Check whether the string output is correct.
    JSONAssert.assertEquals(stringWriter.toString(),
        "{\"message\":\"Invalid user.\",\"status\":\"error\"}", JSONCompareMode.STRICT);
  }
}
