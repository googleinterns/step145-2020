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
 * Creates planner table from a 2D array
 * @param {Object} tableData 2D array with separation of courses
 * @param {Object} creditsData Array with credits for each semester
 * @param {Element} courseContainer container for course list
 */
function createPlanTable(tableData, creditsData, courseContainer) {
  rectangularize2dMatrix(tableData);
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

/**
 * Rectangularizes matrix by ensuring each row has an equal number of elements.
 * @param {Object} data The 2d array to rectangularize
 */
function rectangularize2dMatrix(data) {
  const longestRow = Math.max(...data.map(arr => arr.length));
  data.forEach((row) => {
    const numElementsToAdd = longestRow - row.length;
    for (let i = 0; i < numElementsToAdd; i++) {
      row.push('');
    }
  });
}

/**
 * Creates an alert with the specified message in the container
 * @param {string} message The message string you want to be displayed
 * @param {string} type type of alert you want to display (primary, secondary,
 *     success, warning, danger)
 * @param {Element} container the container you want to display the alert in
 */
function createAlert(message, type, container) {
  const alert = document.createElement('div');
  alert.setAttribute('class', `alert alert-${type}`);
  alert.setAttribute('role', 'alert');
  alert.appendChild(document.createTextNode(message));
  container.appendChild(alert);
}

export const Util = {
  attachNewSpinner: attachNewSpinner,
  createPlanTable: createPlanTable,
  createAlert: createAlert
};
