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

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** Provides an interface for making outside API calls. */
public class APIUtil {
  /**
   * Returns a json object given a URIBuilder.
   *
   * @param builder The endpoint that will be requested
   */
  public JSONArray requestAPIAndGetJsonArray(URIBuilder builder) {
    if (builder == null) {
      return null;
    }

    HttpGet apiRequest;
    try {
      apiRequest = new HttpGet(builder.build());
    } catch (URISyntaxException e) {
      return null;
    }

    String json = requestAPI(apiRequest);
    JSONParser parser = new JSONParser();
    JSONArray jsonArray;
    try {
      jsonArray = (JSONArray) parser.parse(json);
    } catch (ParseException e) {
      return null;
    }
    return jsonArray;
  }

  /**
   * Returns a json object given a URIBuilder.
   *
   * @param builder The endpoint that will be requested
   */
  public JSONObject requestAPIAndGetJsonObject(URIBuilder builder) {
    if (builder == null) {
      return null;
    }

    HttpGet apiRequest;
    try {
      apiRequest = new HttpGet(builder.build());
    } catch (URISyntaxException e) {
      return null;
    }

    String json = requestAPI(apiRequest);
    JSONParser parser = new JSONParser();
    JSONObject jsonObject;
    try {
      jsonObject = (JSONObject) parser.parse(json);
    } catch (ParseException e) {
      return null;
    }
    return jsonObject;
  }

  /**
   * Makes a GET request and returns the reponse json.
   *
   * @param apiRequest The HttpGet object that will be executed
   */
  private String requestAPI(HttpGet apiRequest) {
    // Ignore any SSL certificate validations.
    String json;
    try (CloseableHttpClient httpClient =
             HttpClients.custom()
                 .setSSLContext(new SSLContextBuilder()
                                    .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                                    .build())
                 .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                 .build();
         CloseableHttpResponse apiResponse = httpClient.execute(apiRequest)) {
      HttpEntity entity = apiResponse.getEntity();
      json = EntityUtils.toString(entity);
      if (entity == null
          || apiResponse.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
        return "";
      }
    } catch (
        NoSuchAlgorithmException | KeyManagementException | KeyStoreException | IOException e) {
      return "";
    }
    return json;
  }
}
