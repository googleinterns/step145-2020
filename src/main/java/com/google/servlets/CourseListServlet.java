// Copyright 2019 Google LLC
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

package com.google.sps.servlets;


import java.io.File; 
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.simple.JSONObject;


/** Servlet that returns list of courses.*/
@WebServlet("/courselist")
public class CourseListServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    // TODO: Store and retrieve from Datastore
    String [] courseList = new String[]{"CMSC101", "CMSC106", "CMSC122", "CMSC131",
        "CMSC132","CMSC133","CMSC216","CMSC250","CMSC298A","CMSC320","CMSC330","CMSC351",
        "CMSC388J","CMSC389A","CMSC389B","CMSC389E","CMSC389N","CMSC390O"};
    
    response.setContentType("text/html;");
    response.getWriter().println(convertToJson(courseList));
  }

  /**
   * Converts a ServerStats instance into a JSON string using the Gson library. Note: We first added
   * the Gson library dependency to pom.xml.
   */
  private String convertToJson(String [] courses) {
    Gson gson = new Gson();
    String json = gson.toJson(courses);
    return json;
  }
}
