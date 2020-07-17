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

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/** Provides an interface for shared logic between the servlets. */
public abstract class BaseServlet extends HttpServlet {
  protected ApiUtil apiUtil;

  public BaseServlet() {
    this.apiUtil = new ApiUtil();
  }

  public BaseServlet(ApiUtil apiUtil) {
    this.apiUtil = apiUtil;
  }

  /**
   * Responds with an HTTP error.
   * @param status The status int that we want to respond with.
   * @param response The HttpServletResponse object.
   */
  public void respondWithError(int status, HttpServletResponse response) throws IOException {
    String message = "";
    switch (status) {
      case HttpServletResponse.SC_OK:
        // 200
        message = "OK";
        break;
      case HttpServletResponse.SC_BAD_REQUEST:
        // 400
        message = "Bad request.";
        break;
      case HttpServletResponse.SC_UNAUTHORIZED:
        // 401
        message = "Unauthorized.";
        break;
      case HttpServletResponse.SC_NOT_FOUND:
        // 404
        message = "Not found.";
        break;
      case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
        // 500
        message = "Internal server error.";
        break;
      default:
        message = "Error.";
    }

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("message", message);
    jsonObject.put("status", "error");
    response.setStatus(status);
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(jsonObject));
  }

  /**
   * Responds with an HTTP error with a custom message.
   * @param message The custom error message.
   * @param status The status int that we want to respond with.
   * @param response The HttpServletResponse object.
   */
  public void respondWithError(String message, int status, HttpServletResponse response)
      throws IOException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("message", message);
    jsonObject.put("status", "error");
    response.setStatus(status);
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(jsonObject));
  }
}