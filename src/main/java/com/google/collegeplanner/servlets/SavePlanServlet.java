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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.collegeplanner.data.Plan;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/** Servlet that saves and returns a users plans.*/
@WebServlet("/api/planner/save")
public class SavePlanServlet
    extends BaseServlet { 

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
    GoogleIdToken idToken;
    try {
      idToken = verifier.verify(request.getParameter("idToken"));
    } catch (GeneralSecurityException e) {
      respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
      return;
    }

    if (idToken == null) {
      respondWithError("Invalid user.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }

    Payload payload = idToken.getPayload();
    Query query =
        new Query("Plan")
            .setFilter(new FilterPredicate("user", FilterOperator.EQUAL, payload.getEmail()))
            .addSort("timestamp", SortDirection.DESCENDING);
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

    ArrayList<Plan> plans = new ArrayList<>();
    for (Entity entity : results) {
      long id = entity.getKey().getId();
      String plan = (String) entity.getProperty("plan");
      String planName = (String) entity.getProperty("planName");
      String timestamp = (String) entity.getProperty("timestamp");
      plans.add(new Plan(id, plan, planName, timestamp));
    }
    JSONObject plansJson = new JSONObject();
    plansJson.put("plans", new Gson().toJson(plans));
    plansJson.put("user", payload.getEmail());
    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(plansJson));
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
      respondWithError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
      return;
    }

    if (idToken == null) {
      respondWithError("Invalid user.", HttpServletResponse.SC_BAD_REQUEST, response);
      return;
    }

    Payload payload = idToken.getPayload();
    addToDatastore(payload.getEmail(), plan, planName);
  }

  /**
   * Writes the saved plan to datastore.
   * @param email The email associated with the plan.
   * @param plan The plan that was saved.
   * @param planName the name the user tried to save their plan with.
   */
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