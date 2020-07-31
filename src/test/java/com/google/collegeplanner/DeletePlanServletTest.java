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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.collegeplanner.servlets.DeletePlanServlet;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Test DeletePlanServlet */
@RunWith(JUnit4.class)
public final class DeletePlanServletTest {
  HttpServletRequest request;
  DatastoreService datastore;
  DeletePlanServlet servlet;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void before() throws Exception {
    datastore = DatastoreServiceFactory.getDatastoreService();
    request = mock(HttpServletRequest.class);
    servlet = new DeletePlanServlet();
    helper.setUp();
  }

  @After
  public void after() {
    helper.tearDown();
  }

  @Test
  public void servletDeletesDatastoreObjects() throws Exception {
    // Put 2 entities in datastore. 
    Entity planEntity1 = new Entity("Plan");
    datastore.put(planEntity1);

    Entity planEntity2 = new Entity("Plan");
    datastore.put(planEntity2);

    // Delete an element and check that there is only 1 element in datastore.
    when(request.getParameter("id")).thenReturn("1");
    servlet.doPost(request, null);
    Assert.assertEquals(1, datastore.prepare(new Query("Plan")).countEntities());
  }

  @Test
  public void servletDoesntDeleteInvalidIds() throws Exception {
    // Put 2 entities in datastore. 
    Entity planEntity1 = new Entity("Plan");
    datastore.put(planEntity1);

    Entity planEntity2 = new Entity("Plan");
    datastore.put(planEntity2);

    // Delete an element and check that there are still 2 elements in datastore.
    when(request.getParameter("id")).thenReturn("3");
    servlet.doPost(request, null);
    Assert.assertEquals(2, datastore.prepare(new Query("Plan")).countEntities());
  }

}
