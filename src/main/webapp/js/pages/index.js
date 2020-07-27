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

import Calendar from '../lib/calendar.js';
import CollegePlanner from '../lib/courseSelector.js';

const MAX_PAGINATION_SCHEDULES = 15;

window.addEventListener('load', () => {
  Calendar.initCalendar();
  CollegePlanner.getDepartmentOptions();
});

/**
 * Renders the specified schedule on the calendar
 * @param {Object} schedule JSON object mapping course to the section id
 * @param {Object} courseInfo JSON object mapping course to course details
 * @param {Object} selected Array containing all of the course_ids selected by
 *     the user
 */
function addScheduleToCalendar(schedule, courseInfo, selected) {
  Calendar.clear();
  selected.forEach(course => {
    Calendar.addCourse(courseInfo[course], schedule[course]);
  });
}

document.querySelector('.course-list').addEventListener('click', () => {
  const selected = CollegePlanner.getSelected();
  const courseInfo = CollegePlanner.getCourseInfo();
  // TODO(naaoli): connect to algorithm servlet
  // hard code return from algorithm servlet
  const sections1 = {};
  selected.forEach(
      course => {sections1[course] = courseInfo[course].sections[0]});
  const sections2 = {};
  selected.forEach(
      course => {sections2[course] = courseInfo[course].sections[1]});
  const schedules = [sections1, sections2];

  // by default, add the first schedule to the calendar after response received
  addScheduleToCalendar(schedules[0], courseInfo, selected);

  // if more than 1 schedule returned, create pagination
  if (schedules.length == 1) {
    return;
  }
  const pageList = document.getElementById('calendar-pagination');
  pageList.innerText = '';  // clear any existing children from the element
  for (let i = 1; i <= schedules.length && i <= MAX_PAGINATION_SCHEDULES; i++) {
    const nextPage = document.createElement('li');
    nextPage.innerText = i;
    // by default, set first page to active
    if (i == 1) {
      nextPage.setAttribute('class', 'active');
    }
    pageList.appendChild(nextPage);
    const schedule = schedules[i - 1];
    nextPage.addEventListener('click', () => {
      addScheduleToCalendar(schedule, courseInfo, selected);
      const aElements = pageList.getElementsByTagName('li');
      // remove active label from any other child element
      for (const element of aElements) {
        element.classList.remove('active');
      }
      // set current page to active
      nextPage.classList.add('active');
    });
  }
});

document.getElementById('add-selected').addEventListener('click', () => {
  CollegePlanner.addToSelected();
});

document.getElementById('departments').addEventListener('change', () => {
  CollegePlanner.getCourseOptions();
});
