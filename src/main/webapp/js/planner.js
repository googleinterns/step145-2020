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
import {CollegePlanner} from './courseSelector.js'

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
  courseContainer.innerText = '';
  try {
    const response = await fetch('/api/planner', {
      method: 'POST',
      body: JSON.stringify(data),
    });

    const courseList = await response.json();
    if (response.ok) {
      const courseData = courseList.semester_plan;
      if (!courseData.length) {
        createAlert(
            'These courses did not fit in the given number of semesters.',
            'primary', courseContainer);
      } else {
        createTable(courseData, courseContainer);
      }
    } else {
      createAlert(courseList.message, 'warning', courseContainer);
    }
  } catch (err) {
    alert('An error occurred.');
  }
}

/**
 * Creates an alert with the specified message in the container
 * @param {string} message The message string you want to be displayed
 * @param {string} type type of alert you want to display (primary, secondary,
 *     success, warning, danger)
 * @param {Object} container the container you want to display the alert in
 */
function createAlert(message, type, container) {
  const alert = document.createElement('div');
  alert.setAttribute('class', `alert alert-${type}`);
  alert.setAttribute('role', 'alert');
  alert.appendChild(document.createTextNode(message));
  container.appendChild(alert);
}

/**
 * Creates spinner to signify loading and adds to the courseContainer
 * @param {Object} courseContainer container for course list
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
 * @param {Object} courseContainer container for course list
 */
function createTable(tableData, courseContainer) {
  courseContainer.innerText = '';
  const table = document.createElement('table');
  const tableBody = document.createElement('tbody');
  table.setAttribute('class', 'table table-hover mb-0;');
  tableData.forEach((rowData, i) => {
    const row = document.createElement('tr');
    const cell = document.createElement('td');
    const semesterLabel = document.createElement('b');
    semesterLabel.innerText = `Semester ${i + 1}:`;
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
