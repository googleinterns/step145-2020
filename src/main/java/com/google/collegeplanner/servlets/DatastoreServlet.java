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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

/** Provides an interface for making outside API calls. */
public class DatastoreServlet {
 
  public void test() {
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    // Create entity
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Course");
    Key key = keyFactory.newKey("john.doe@gmail.com");
    Entity entity = Entity.newBuilder(key)
        .set("name", "John Doe")
        .set("age", 51)
        .set("favorite_food", "pizza")
        .build();
    datastore.put(entity);

    // Query for Data
    Query<Entity> query = Query.newEntityQueryBuilder()
      .setKind("Person")
      .setFilter(PropertyFilter.eq("favorite_food", "pizza"))
      .build();
    QueryResults<Entity> results = datastore.run(query);
    while (results.hasNext()) {
      Entity currentEntity = results.next();
      System.out.println(currentEntity.getString("name") + ", you're invited to a pizza party!");
    }
  }
}
