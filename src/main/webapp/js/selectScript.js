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
export const CollegePlanner = (() => {
  let selected = [];  // Courses selected by the user
  let courses = [];   // List with all courses
  let courseIdNameMap = {};  // JSON mapping of course ID {string} to course name {string}

  /**
   * Gets courses from /courselist servlet to populate dropdown list
   */
  async function getOptions() {
    const response = await fetch('/api/courses');
    const courseList = await response.json();
    const courseContainer = document.getElementById('courses');
    courseContainer.innerHTML = '';
    // Add default option to course list
    const option = document.createElement('option');
    option.innerText = 'Select a Course';
    option.selected = true;
    option.hidden = true;
    courseContainer.appendChild(option);
    // Add each course to course list
    const coursesDetailed = courseList.courses_detailed;
    coursesDetailed.forEach(course => addOption(course, courseContainer));
  }

  /**
   * Creates options in select list
   * @param {Object} course The JSON Object for the course to add to the
   *     dropdown
   * @param {string} container The id name of the container you want to add
   *     options to
   */
  function addOption(course, courseContainer) {
    courseIdNameMap[`${course.course_id}`] = course.name;
    const option = document.createElement('option');
    option.innerText = course.course_id;
    courses.push(course.course_id);
    option.value = course.course_id;
    courseContainer.appendChild(option);
  }

  /**
   * Adds a course to the list of selected courses
   */
  function addToSelected() {
    const courseContainer = document.getElementById('selected-classes');
    const courseSelection = document.getElementById('courses');
    const selectedCourse =
        courseSelection.options[courseSelection.selectedIndex].value;
    if (!selected.includes(selectedCourse) &&
        courses.includes(selectedCourse)) {
      selected.push(selectedCourse);
      courseContainer.appendChild(createCourseListElement(selectedCourse));
    }
  }

  /**
   * Creates a list element for a course
   * @param {string} course The name of the course to add to the list of
   *     selected courses
   */
  function createCourseListElement(course) {
    const liElement = document.createElement('li');
    liElement.setAttribute('class', 'list-group-item');

    const courseId = document.createElement('b');
    courseId.innerText = `${course}: `;
    liElement.append(courseId);
    liElement.append(document.createTextNode(courseIdNameMap[course]))

    const deleteButtonElement = document.createElement('button');
    const buttonImage = document.createElement('i');
    buttonImage.setAttribute('class', 'fas fa-trash-alt');
    deleteButtonElement.appendChild(buttonImage);
    deleteButtonElement.setAttribute(
        'class', 'float-right rounded-circle border-0');
    deleteButtonElement.addEventListener('click', () => {
      liElement.remove();
      selected = selected.filter(value => value != course);
    });
    liElement.appendChild(deleteButtonElement);
    return liElement;
  }
  window.addEventListener('load', () => {
    getOptions();
  });
  document.getElementById('add-selected').addEventListener('click', () => {
    addToSelected();
  });


  return {
    selected: selected,
    courses: courses,
  };
})();
