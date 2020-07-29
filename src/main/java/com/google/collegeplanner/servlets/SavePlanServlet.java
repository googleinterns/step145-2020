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

package com.google.collegeplanner.servlets;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.gson.GsonFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/** Servlet that returns list of course sections.*/
@WebServlet("/api/planner/save")
public class SavePlanServlet
    extends BaseServlet { /**
                           * Reads from Datastore and returns response with section details
                           */
  GoogleIdTokenVerifier verifier;
  DatastoreService datastore;

  public SavePlanServlet() {
    this(DatastoreServiceFactory.getDatastoreService(),
        new GoogleIdTokenVerifier.Builder(UrlFetchTransport.getDefaultInstance(), new GsonFactory())
            .setAudience(Collections.singletonList(
                "267429534228-vvsi2uldmpji3rgs1qd3a41rceciaaaq.apps.googleusercontent.com"))
            .build());
  }

  public SavePlanServlet(DatastoreService datastore, GoogleIdTokenVerifier verifier) {
    this.verifier = verifier;
    this.datastore = datastore;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO(ramyabuva): Get users saved plans.
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String idTokenStr;
    String plan;
    String planName;
    JSONObject body;
    try {
      body = getBody(request);
      idTokenStr = (String) body.get("idToken");
      planName = (String) body.get("planName");
      plan = ((JSONObject) body.get("plan")).toString();
    } catch (ClassCastException | ParseException | NullPointerException e) {
      respondWithError(
          "Invalid body for POST request.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }

    GoogleIdToken idToken;
    try {
      idToken = verifier.verify(idTokenStr);
    } catch (GeneralSecurityException e) {
      return;
    }

    if (idToken != null) {
      Payload payload = idToken.getPayload();
      addToDatastore(payload.getEmail(), plan, planName);
    } else {
      respondWithError("Invalid user.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }
  }

  private void addToDatastore(String email, String plan, String planName) {
    long timestamp = System.currentTimeMillis();
    Entity planEntity = new Entity("Plan");
    planEntity.setProperty("plan", plan);
    planEntity.setProperty("planName", planName);
    planEntity.setProperty("timestamp", timestamp);
    planEntity.setProperty("user", email);

    datastore.put(planEntity);
  }
}