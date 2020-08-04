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
import {Calendar} from '../lib/calendar.js';
import {CourseSelector} from '../lib/courseSelector.js';

const MAX_PAGINATION_SCHEDULES = 15;

window.addEventListener('load', () => {
  Calendar.initCalendar();
  CourseSelector.getDepartmentOptions();
});

/**
 * Renders the specified schedule on the calendar.
 * @param {Object} schedule JSON object mapping course to the section id
 * @param {Object} courseInfo JSON object mapping course to course details
 * @param {Array.<string>} selected Array containing all of the course_ids
 *     selected by the user
 */
function addScheduleToCalendar(schedule, courseInfo, selected) {
  Calendar.clear();
  selected.forEach(course => {
    Calendar.addCourse(courseInfo[course], schedule[course]);
  });
}

/**
 * Returns a list of schedules with the selected courses
 * @param {Array.<string>} selected JSON Array of the selected classes to
 *     schedule
 */
async function getSchedules(selected) {
  let response;
  let scheduleObject;
  const data = {selectedClasses: selected};
  try {
    response = await fetch('/api/scheduler', {
      method: 'POST',
      body: JSON.stringify(data),
    });
    scheduleObject = await response.json();
    if (response.ok) {
      return scheduleObject.schedules;
    } else {
      CourseSelector.createAlert(
          'An error occurred', 'danger', courseContainer);
      return null;
    }
  } catch (err) {
    CourseSelector.createAlert('An error occurred', 'danger', courseContainer);
    return null;
  }
}

document.querySelector('.course-list').addEventListener('click', async () => {
  const selected = CourseSelector.getSelected();
  const courseInfo = CourseSelector.getCourseInfo();
  const schedules = await getSchedules(selected);

  // If there are no schedules returned or
  // there was an error in getSchedules(), then exit the function.
  if (schedules == null || schedules.length == 0) {
    return;
  }

  // By default, add the first schedule to the calendar after response is
  // received.
  addScheduleToCalendar(schedules[0], courseInfo, selected);

  // If more than 1 schedule returned, create pagination.
  if (schedules.length == 1) {
    return;
  }
  const pageList = document.getElementById('calendar-pagination');
  pageList.innerText = '';  // Clear any existing children from the element.
  for (let i = 1; i <= schedules.length && i <= MAX_PAGINATION_SCHEDULES; i++) {
    const nextPage = document.createElement('li');
    nextPage.innerText = i;
    // By default, set first page to active.
    if (i == 1) {
      nextPage.setAttribute('class', 'active');
    }
    pageList.appendChild(nextPage);
    const schedule = schedules[i - 1];
    nextPage.addEventListener('click', () => {
      addScheduleToCalendar(schedule, courseInfo, selected);
      const aElements = pageList.getElementsByTagName('li');
      // Remove active label from any other child element.
      for (const element of aElements) {
        element.classList.remove('active');
      }
      // Set current page to active.
      nextPage.classList.add('active');
    });
  }
});

document.getElementById('add-selected').addEventListener('click', () => {
  CourseSelector.addToSelected();
});

document.getElementById('departments').addEventListener('change', () => {
  CourseSelector.getCourseOptions();
});

document.getElementById('signout-button').addEventListener('click', () => {
  Auth.signOut();
});
