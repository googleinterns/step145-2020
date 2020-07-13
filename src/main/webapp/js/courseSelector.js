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
  /**
   * @type {{courseId: string}, {courseName: string}}
   */
  const courseNames = {};

  /**
   * Gets departments from /api/departments servlet to populate dropdown list
   */
  async function getDepartmentOptions() {
    const response = await fetch('/api/departments');
    const departmentList = await response.json();
    const departmentContainer = document.getElementById('departments');
    departmentContainer.innerHTML =
        '';  // Clearing departmentContainer to get rid of previous options
    // Add instruction to courseList dropdown
    const option = document.createElement('option');
    option.innerText = 'Select a Department';
    option.selected = true;
    option.hidden = true;
    departmentContainer.appendChild(option);
    // Add each course to course list
    const departmentsDetailed = departmentList.departments_detailed;
    departmentsDetailed.forEach(department => addDepartmentOption(department, departmentContainer));
  }

  /**
   * Creates options in departments select list
   * @param {Object} department The JSON Object for the course to add to the
   *     dropdown
   * @param {string} departmentContainer The id name of the container you want to add
   *     options to
   */
  function addDepartmentOption(department, departmentContainer) {
    const option = document.createElement('option');
    option.innerText = department.dept_id;
    option.value = department.dept_id;
    departmentContainer.appendChild(option);
  }

  /**
   * Gets courses from /api/courses servlet to populate dropdown list
   */
  async function getCourseOptions() {
    const departmentSelection = document.getElementById('departments');
    const selectedDepartment =
        departmentSelection.options[departmentSelection.selectedIndex].value;
    const response = await fetch(`/api/courses?department=${selectedDepartment}`);
    const courseList = await response.json();
    const courseContainer = document.getElementById('courses');
    courseContainer.innerHTML =
        '';  // Clearing courseContainer to get rid of previous options
    // Add default option to courses dropdown
    const option = document.createElement('option');
    option.innerText = 'Select a Course';
    option.selected = true;
    option.hidden = true;
    courseContainer.appendChild(option);
    // Add each course to course list
    const coursesDetailed = courseList.courses_detailed;
    coursesDetailed.forEach(course => addCourseOption(course, courseContainer));
  }


  /**
   * Creates options in select courses list
   * @param {Object} course The JSON Object for the course to add to the
   *     dropdown
   * @param {string} courseContainer The id name of the container you want to add
   *     options to
   */
  function addCourseOption(course, courseContainer) {
    courseNames[course.course_id] = course.name;
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
    liElement.append(document.createTextNode(courseNames[course]))

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
    getDepartmentOptions();
  });
  document.getElementById('add-selected').addEventListener('click', () => {
    addToSelected();
  });
  document.getElementById('departments').addEventListener('change', () => {
    getCourseOptions();
  });


  return {
    selected: selected,
    courses: courses,
  };
})();
