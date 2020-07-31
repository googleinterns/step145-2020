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
import {Util} from '../lib/utils.js';

async function getSavedPlans(googleUser) {
  let response;
  let plans;
  try {
    response = await fetch(`/api/planner/save?idToken=${
        encodeURIComponent(googleUser.getAuthResponse().id_token)}`);
    plans = await response.json();
  } catch (err) {
    Util.createAlert(
        'An error occurred', 'danger',
        document.getElementById('alert-container'));
    return;
  }
  if (plans.user == googleUser.getBasicProfile().getEmail()) {
    const planList = JSON.parse(plans.plans);
    document.getElementById('plan-column').innerText =
        '';  // Clear the sign in prompt.
    planList.forEach(
        planInfo => createCard(
            planInfo.planName, planInfo.plan.semester_plan,
            planInfo.plan.semester_credits));
  }
}

/**
 * Creates card for the plan and appends it to the page
 * @param {String} planName the name of the plan
 * @param {Object} tableData 2D array with separation of courses
 * @param {Object} creditsData Array with credits for each semester
 */
function createCard(planName, tableData, creditsData) {
  const card = document.createElement('div');
  card.setAttribute('class', 'card shadow mb-4');
  const header = document.createElement('div');
  header.setAttribute(
      'class',
      'card-header py-3 d-flex flex-row align-items-center justify-content-between');
  const title = document.createElement('h6');
  title.setAttribute('class', 'm-0 font-weight-bold text-primary');
  title.innerText = planName;
  header.appendChild(title);
  card.appendChild(header);
  const body = document.createElement('div');
  body.setAttribute('class', 'card-body');
  const table = document.createElement('center');
  Util.createPlanTable(tableData, creditsData, table);
  body.appendChild(table);
  card.appendChild(body);
  document.getElementById('plan-column').appendChild(card);
}

Auth.registerPostSignInHandler(getSavedPlans);

document.getElementById('signout-button').addEventListener('click', () => {
  Auth.signOut();
});
