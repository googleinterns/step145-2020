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

import CollegePlanner from '../lib/courseSelector.js';
import Auth from '../lib/login.js';

/**
 * Gets results from /planner servlet to results
 */
async function getPlan() {
  const courseContainer = document.getElementById('order-area');
  attachNewSpinner(courseContainer);
  const selectedClasses = []
  CollegePlanner.getSelected().forEach(
      course => selectedClasses.push(CollegePlanner.getCourseInfo()[course]));
  const data = {
    selectedClasses: selectedClasses,
    semesters: document.getElementById('semesters').value
  };
  courseContainer.innerText = '';  // Clears card body to get rid of spinner.
  let response;
  let courseList;
  // Clear save button from header.
  document.getElementById('plan-header').innerText = '';
  // Replace form to clear all event listeners so it doesn't save old schedules.
  const oldNameForm = document.getElementById('plan-name');
  const newNameForm = oldNameForm.cloneNode(true);
  oldNameForm.parentNode.replaceChild(newNameForm, oldNameForm);
  try {
    response = await fetch('/api/planner', {
      method: 'POST',
      body: JSON.stringify(data),
    });
    courseList = await response.json();
  } catch (err) {
    CollegePlanner.createAlert('An error occurred', 'danger', courseContainer);
    return;
  }
  if (response.ok) {
    const courseData = courseList.semester_plan;
    const creditsData = courseList.semester_credits;
    if (!courseData.length) {
      CollegePlanner.createAlert(
          'These courses did not fit in the given number of semesters.',
          'primary', courseContainer);
    } else if (courseData.length == creditsData.length) {
      createTable(courseData, creditsData, courseContainer);
      attachSaveButton(document.getElementById('plan-header'));
      document.getElementById('plan-name').addEventListener('submit', () => {
        savePlan(courseList);
      });
    } else {
      CollegePlanner.createAlert(
          'An invalid response was recieved.', 'warning', courseContainer);
    }
  } else {
    CollegePlanner.createAlert(courseList.message, 'warning', courseContainer);
  }
}

/**
 * Sends user id token to backend and saves plans in Datastore.
 * @param {Object} courseList JSON Object containing the courseplan returned by
 *     the servlet.
 */
async function savePlan(courseList) {
  if (!gapi.auth2.getAuthInstance().isSignedIn.get()) {
    return;
  }
  const data = {
    idToken: gapi.auth2.getAuthInstance()
                 .currentUser.get()
                 .getAuthResponse()
                 .id_token,
    plan: courseList,
    planName: document.getElementById('save-plan-as').value
  };
  try {
    await fetch('/api/planner/save', {
      method: 'POST',
      body: JSON.stringify(data),
    });
    $('#savePlanModal').modal('hide');
  } catch (err) {
    return;
  }
}

/**
 * Creates save button for plan.
 * @param {Element} headerContainer Container for header for proposed plan.
 */
function attachSaveButton(headerContainer) {
  const button = document.createElement('button');
  button.setAttribute('type', 'submit');
  button.setAttribute('href', '#');
  button.setAttribute('class', 'btn btn-secondary btn-icon-split float:right');
  button.setAttribute('id', 'save-plan-prompt');
  const icon = document.createElement('span');
  icon.setAttribute('class', 'icon text-white-50')
  const fabIcon = document.createElement('i');
  fabIcon.setAttribute('class', 'fa fa-star');
  icon.appendChild(fabIcon);
  button.appendChild(icon);
  const text = document.createElement('span');
  text.setAttribute('class', 'text');
  text.innerText = 'Save Plan';
  button.appendChild(text);
  // If user isn't signed in, prompt them to sign in.
  button.addEventListener('click', () => {
    if (gapi.auth2.getAuthInstance().isSignedIn.get()) {
      $('#savePlanModal').modal();
    } else {
      $('#signInModal').modal();
    }
  });
  headerContainer.appendChild(button);
}

/**
 * Creates spinner to signify loading and adds to the courseContainer
 * @param {Element} courseContainer container for course list
 */
function attachNewSpinner(courseContainer) {
  const spinner = document.createElement('i');
  spinner.setAttribute('class', 'fas fa-sync fa-spin');
  courseContainer.innerText = '';
  courseContainer.appendChild(spinner);
}

/**
 * Creates table from a 2D array
 * @param {Object} tableData 2D array with separation of courses
 * @param {Object} creditsData Array with credits for each semester
 * @param {Element} courseContainer container for course list
 */
function createTable(tableData, creditsData, courseContainer) {
  courseContainer.innerText = '';
  const table = document.createElement('table');
  const tableBody = document.createElement('tbody');
  table.setAttribute('class', 'table table-hover mb-0;');
  tableData.forEach((rowData, i) => {
    const row = document.createElement('tr');
    const cell = document.createElement('td');
    const semesterLabel = document.createElement('b');
    semesterLabel.innerText = `Semester ${i + 1} (${creditsData[i]} Credits):`;
    cell.appendChild(semesterLabel);
    row.appendChild(cell);

    rowData.forEach((cellData) => {
      const cell = document.createElement('td');
      cell.appendChild(document.createTextNode(cellData));
      row.appendChild(cell);
    });

    tableBody.appendChild(row);
  });

  table.appendChild(tableBody);
  courseContainer.appendChild(table);
}

document.getElementById('submit-plan').addEventListener('submit', () => {
  getPlan();
});

window.addEventListener('load', () => {
  CollegePlanner.getDepartmentOptions();
});

document.getElementById('add-selected').addEventListener('click', () => {
  CollegePlanner.addToSelected();
});

document.getElementById('departments').addEventListener('change', () => {
  CollegePlanner.getCourseOptions();
});

document.getElementById('signout-button').addEventListener('click', () => {
  Auth.signOut();
});
