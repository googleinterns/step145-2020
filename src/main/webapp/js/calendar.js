
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

// TODO(savsa): Add ability to remove courses from the calendar
// TODO(#34): Add ability to choose which section the user wants, not just
//  automatically choosing the first section

/**
 * The id of the next schedule that will be added to the calendar.
 * @type {number}
 */
let id = 1;

/**
 * The calendar object.
 * @type {Calendar}
 */
let calendar;

/**
 * An alias for the Luxon DateTime object.
 * @type {DateTime}
 */
const DateTime = luxon.DateTime;

/**
 * Days of the school week.
 * @enum {number}
 */
const enumDays = {
  DATE_SUNDAY: 0,
  DATE_MONDAY: 1,
  DATE_TUESDAY: 2,
  DATE_WEDNESDAY: 3,
  DATE_THURSDAY: 4,
  DATE_FRIDAY: 5,
  DATE_SATURDAY: 6,
};

/**
 * Maps a course id to the array of the corresponding schedule ids on the
 * calendar. Note that a single course with 3 meeting days has 3 different
 * schedule ids. This is why we need an array to house them.
 * @type {{courseId: string}, {scheduleIds: array}}
 */
let scheduleInfo = {};

export const Calendar = (() => {
  return {addCourse: addCourse, removeCourse: removeCourse};
})();

/**
 * Initializes the calendar and moves the view to a hardcoded date in the
 * past.
 */
function initCalendar() {
  // The timezone takes into account the Easter Time zone offset + 1 hour of
  // dayling savings. Since it's a set day in the past, scheduling dates on this
  // calendar shouldn't change because of the time zone.
  calendar = new tui.Calendar('#calendar', {
    defaultView: 'week',
    useCreationPopup: true,
    useDetailPopup: true,
    disableDblClick: true,
    disableClick: true,
    isReadOnly: true,
    scheduleView: ['time'],
    taskView: false,
    timezones: [{
      timezoneOffset: -300,
      tooltip: 'EDT',
    }],
  });
  // The hard coded date that all scheduled events should fall around
  // Date: Sunday January 2nd, 2000 @ 00:00 EST
  calendar.setDate(new Date('2000-01-02T00:00:00'));
}

/**
 * Adds a course to the calendar.
 * @param {Object} course The JSON Object for the course.
 */
async function addCourse(course) {
  if (course == null) {
    return;
  }

  const course_id = course.course_id;
  const response =
      await fetch(`/api/sections?course_id=${encodeURIComponent(course_id)}`);
  const json = await response.json();

  // For now, just choose the first section out of the available ones
  if (json.sections == null) {
    return;
  }
  const firstSection = json.sections[0];
  if (firstSection.meetings == null) {
    return;
  }
  const firstMeetingInfo = firstSection.meetings[0];
  const meetingDays = firstMeetingInfo.days;

  const startTime = firstMeetingInfo.start_time;
  const endTime = firstMeetingInfo.end_time;



  if (meetingDays.includes('Su')) {
    addCourseToCalendar(course, startTime, endTime, enumDays.DATE_SUNDAY);
  }
  if (meetingDays.includes('M')) {
    addCourseToCalendar(course, startTime, endTime, enumDays.DATE_MONDAY);
  }
  if (meetingDays.includes('Tu')) {
    addCourseToCalendar(course, startTime, endTime, enumDays.DATE_TUESDAY);
  }
  if (meetingDays.includes('W')) {
    addCourseToCalendar(course, startTime, endTime, enumDays.DATE_WEDNESDAY);
  }
  if (meetingDays.includes('Th')) {
    addCourseToCalendar(course, startTime, endTime, enumDays.DATE_THURSDAY);
  }
  if (meetingDays.includes('F')) {
    addCourseToCalendar(course, startTime, endTime, enumDays.DATE_FRIDAY);
  }
  if (meetingDays.includes('Sa')) {
    addCourseToCalendar(course, startTime, endTime, enumDays.DATE_SATURDAY);
  }
}

/**
 * Adds a course to the calendar given the day of the week and a time.
 * @param {Object} course The JSON Object for the course.
 * @param {string} startTime The string representation of a 12-hour clock
 *     time eg. 8:30pm.
 * @param {string} endTime The string representation of a 12-hour clock
 *     time eg. 8:30pm.
 * @param {enumDays} day The day that the course falls on.
 */
function addCourseToCalendar(course, startTime, endTime, day) {
  const startDate = createDateFromTimeString(startTime, day);
  const endDate = createDateFromTimeString(endTime, day);
  createSchedule(course, startDate, endDate);
}

/**
 * Creates a schedule on the calendar using the toast API.
 * @param {Object} course The JSON Object for the course.
 * @param {DateTime} startDate The DateTime object of the start time.
 * @param {DateTime} endDate The DateTime object of the end time.
 */
function createSchedule(course, startDate, endDate) {
  // TODO(savsa): Have each schedule be a different color. The color is
  // currently hard coded to be blue so all schedules look the same.
  calendar.createSchedules([{
    id: id.toString(),
    color: '#ffffff',
    bgColor: '#00a9ff',
    borderColor: '#00a9ff',
    calendarId: '1',
    title: course.name,
    category: 'time',
    location: course.building,
    start: startDate.toISO(),
    end: endDate.toISO(),
    isReadOnly: true,
  }]);
  // Each schedule needs a unique ID. We'll start at 1 and go up from there.
  // const scheduleIds = scheduleInfo[course.course_id];
  if (scheduleInfo[course.course_id] == null) {
    scheduleInfo[course.course_id] = [];
  }
  scheduleInfo[course.course_id].push(id);
  id++;
}

/**
 * Removes a schedule on the calendar using the toast API.
 * @param {string} course_id The course_id of the course we want to remove.
 */
function removeCourse(course_id) {
  const scheduleIds = scheduleInfo[course_id];
  if (scheduleIds == null) {
    return;
  }

  scheduleIds.forEach(scheduleId => {
    calendar.deleteSchedule(scheduleId.toString(), '1');
  });
}

/**
 * Creates a schedule on the calendar using the toast API.
 * @param {enumDays} day The day that the course falls on.
 * @param {number} hour The army-time hour portion of the time/date we want
 *     to create.
 * @param {hour} min The army-time minute portion of the time/date we want
 *     to create.
 * @return {DateTime} The new DateTime object.
 */
function createDate(day, hour, min) {
  const sunday = DateTime.fromObject({
    month: 1,
    day: 2,
    year: 2000,
    hour: 0,
    second: 0,
    zone: 'America/New_York',
  });
  return sunday.plus({days: day, hours: hour, minutes: min});
}

/**
 * Creates a Date object from the string representation of a time.
 * @param {string} time The string representation of a 12-hour clock time eg.
 *     8:30pm.
 * @param {enumDays} day The day that the course falls on.
 * @return {DateTime} The new DateTime object.
 */
function createDateFromTimeString(time, day) {
  const dt = DateTime.fromFormat(time, 'h:mma');
  const hour = dt.hour;
  const minutes = dt.minute;
  return createDate(day, hour, minutes);
}

window.addEventListener('load', () => {
  initCalendar();
});
