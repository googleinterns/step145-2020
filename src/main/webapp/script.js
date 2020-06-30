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

let selected = [];
let courses = [];

/**
 * Gets courses from /courselist servlet to populate dropdown list
 */
async function getOptions() {
  const response = await fetch('/courselist');
  courses = await response.json();
  const courseContainer = document.getElementById('courses');
  courseContainer.innerHTML = '';
  createOption('Select a Course', 'courses', /*setValue=*/ false);
  courses.forEach(course => createOption(course, 'courses', /*setValue=*/ true));
}

/**
 * Creates options in select list
 * @param {string} course The name of the course to add to the dropdown
 * @param {string} container The id name of the container you want to add options to
 * @param {boolean} setValue Whether to set the value of the option to the string 
 */
function createOption(course, container, setValue) {
  const courseContainer = document.getElementById(container);
  const opt = document.createElement('option');
  opt.innerText = course;
  if (setValue) {
    opt.value = course;
  } else {
    opt.selected = true;
    opt.hidden = true;
  }
  courseContainer.appendChild(opt);
}

/**
 * Adds a course to the list of selected courses
 */
function addToSelected() {
  const courseContainer = document.getElementById('selectedClasses');
  const courseSelection = document.getElementById("courses");
  const selectedCourse = courseSelection.options[courseSelection.selectedIndex].value;
  if (!selected.includes(selectedCourse) && courses.includes(selectedCourse)) {
    selected.push(selectedCourse);
    courseContainer.appendChild(createListElement(selectedCourse));
  }
}

/**
 * Creates a list element for a course
 * @param {string} course The name of the course to add to the list of selected courses
 */
function createListElement(course) {
  const liElement = document.createElement('li');
  liElement.setAttribute('class', 'list-group-item');

  const courseName = document.createElement('b');
  courseName.innerText = `${course}`;
  liElement.append(courseName);

  const deleteButtonElement = document.createElement('button');
  const buttonImage = document.createElement('i');
  buttonImage.setAttribute('class', 'fas fa-trash-alt');
  deleteButtonElement.appendChild(buttonImage);
  deleteButtonElement.setAttribute('class', 'float-right rounded-circle border-0');
  deleteButtonElement.addEventListener('click', () => {
    liElement.remove();
    selected = selected.filter(function(value){ return value != course;});
  });
  liElement.appendChild(deleteButtonElement);
  return liElement;
}

