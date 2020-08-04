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

import {Auth} from '../lib/auth.js';
import {CourseSelector} from '../lib/courseSelector.js';
import {Util} from '../lib/util.js';

/**
 * Gets results from /planner servlet to results
 */
async function getPlan() {
  const courseContainer = document.getElementById('order-area');
  Util.attachNewSpinner(courseContainer);
  const selectedClasses = []
  CourseSelector.getSelected().forEach(
      course => selectedClasses.push(CourseSelector.getCourseInfo()[course]));
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
    Util.createAlert('An error occurred', 'danger', courseContainer);
    return;
  }
  if (response.ok) {
    const courseData = courseList.semester_plan;
    const creditsData = courseList.semester_credits;
    if (!courseData.length) {
      Util.createAlert(
          'These courses did not fit in the given number of semesters.',
          'primary', courseContainer);
    } else if (courseData.length == creditsData.length) {
      Util.createPlanTable(courseData, creditsData, courseContainer);
      attachSaveButton(document.getElementById('plan-header'));
      document.getElementById('plan-name').addEventListener('submit', () => {
        savePlan(courseList);
      });
    } else {
      Util.createAlert(
          'An invalid response was recieved.', 'warning', courseContainer);
    }
  } else {
    Util.createAlert(courseList.message, 'warning', courseContainer);
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
    document.getElementById('save-button-text').innerText = 'Saved!';
    document.getElementById('save-plan-prompt')
        .classList.remove('btn-secondary');
    document.getElementById('save-plan-prompt').classList.add('btn-primary');

    $('#savePlanModal').modal('hide');
  } catch (err) {
    CourseSelector.createAlert(
        'Could not save this plan', 'warning',
        document.getElementById('plan-name'));
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
  button.setAttribute('class', 'btn btn-secondary btn-icon-split float-right');
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
  text.setAttribute('id', 'save-button-text');
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

document.getElementById('submit-plan').addEventListener('submit', () => {
  getPlan();
});

window.addEventListener('load', () => {
  CourseSelector.getDepartmentOptions();
});

document.getElementById('add-selected').addEventListener('click', () => {
  CourseSelector.addToSelected();
});

document.getElementById('departments').addEventListener('change', () => {
  CourseSelector.getCourseOptions();
});

document.getElementById('see-saved').addEventListener('click', () => {
  if (gapi.auth2.getAuthInstance().isSignedIn.get()) {
    window.location.href = 'planner/saved';
  } else {
    $('#signInModal').modal();
  }
});

document.getElementById('see-saved').addEventListener('click', () => {
  if (gapi.auth2.getAuthInstance().isSignedIn.get()) {
    window.location.href = 'planner/saved';
  } else {
    $('#signInModal').modal();
  }
});

document.getElementById('signout-button').addEventListener('click', () => {
  Auth.signOut();
});
