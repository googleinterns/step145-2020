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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.collegeplanner.servlets.BaseServlet;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/** Servlet responsible for deleting plans. */
@WebServlet("/api/planner/delete")
public class DeletePlanServlet extends BaseServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      long id = Long.parseLong(request.getParameter("id"));

      Key planEntityKey = KeyFactory.createKey("Plan", id);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.delete(planEntityKey);
      // Write success message to response.
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("message", "Deletion was successful.");
      jsonObject.put("status", "ok");
      response.setContentType("application/json;");
      response.getWriter().println(new Gson().toJson(jsonObject));
    } catch (NumberFormatException e) {
      respondWithError(
          "Invalid id for datastore deletion.", HttpServletResponse.SC_BAD_REQUEST, response);
    }
  }
}
