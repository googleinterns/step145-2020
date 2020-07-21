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

import {Calendar} from '../lib/calendar.js';
import {CollegePlanner} from '../lib/courseSelector.js';

window.addEventListener('load', () => {
  Calendar.initCalendar();
  CollegePlanner.getDepartmentOptions();
});

document.querySelector('.course-list').addEventListener('click', () => {
  const selected = CollegePlanner.getSelected();
  const courseInfo = CollegePlanner.getCourseInfo();
  selected.forEach(course => {
    Calendar.addCourse(courseInfo[course]);
  });
});

document.getElementById('add-selected').addEventListener('click', () => {
  CollegePlanner.addToSelected();
});

document.getElementById('departments').addEventListener('change', () => {
  CollegePlanner.getCourseOptions();
});
