
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

export const Calendar = (() => {
  let id = 1;  // The id of the next scheudle that will be added to the calendar
  let cal;     // The calendar object
  const enum_days = {
    DATE_MONDAY: 0,
    DATE_TUESDAY: 1,
    DATE_WEDNESDAY: 2,
    DATE_THURSDAY: 3,
    DATE_FRIDAY: 4,
  };

  /**
   * Initializes the calendar and moves the view to a hardcoded date in the
   * past.
   */
  function initCalendar() {
    cal = new tui.Calendar('#calendar', {
      defaultView: 'week',
      useCreationPopup: true,
      useDetailPopup: true,
      disableDblClick: true,
      disableClick: true,
      isReadOnly: true,
      scheduleView: ['time'],
      taskView: false,
      week: {
        workweek: true,
      },
      timezones: [{
        timezoneOffset: -420,
        tooltip: 'Los Angeles',
      }],
    });
    // The hard coded date that all scheduled events should fall around
    // Date: Sunday January 2nd, 2000 @ 00:00 PST
    cal.setDate(new Date('2000-01-02T00:00:00'));
  }

  /**
   * Adds a course to the calendar
   * @param {Object} course The JSON Object for the course
   */
  async function addCourse(course) {
    if (course == null) {
      return;
    }
    const response = await fetch(
        `/api/sections?course_id=${encodeURIComponent(course.course_id)}`);
    const json = await response.json();

    // For now, just choose the first section out of the available ones
    const firstSection = json.sections[0];
    const firstMeetingInfo = firstSection.meetings[0];
    const meetingDays = firstMeetingInfo.days;

    const startTime = firstMeetingInfo.start_time;
    const endTime = firstMeetingInfo.end_time;

    if (meetingDays.includes('M')) {
      addCourseToCalendar(course, startTime, endTime, enum_days.DATE_MONDAY);
    }
    if (meetingDays.includes('Tu')) {
      addCourseToCalendar(course, startTime, endTime, enum_days.DATE_TUESDAY);
    }
    if (meetingDays.includes('W')) {
      addCourseToCalendar(
          course, startTime, endTime, enum_days.DATE_WEDNESDAY);
    }
    if (meetingDays.includes('Th')) {
      addCourseToCalendar(course, startTime, endTime, enum_days.DATE_THURSDAY);
    }
    if (meetingDays.includes('F')) {
      addCourseToCalendar(course, startTime, endTime, enum_days.DATE_FRIDAY);
    }
  }

  /**
   * Adds a course to the calendar given the day of the week and a time
   * @param {Object} course The JSON Object for the course
   * @param {string} startTime The string representation of a 12-hour clock
   *     time eg. 8:30pm
   * @param {string} endTime The string representation of a 12-hour clock
   *     time eg. 8:30pm
   * @param {enum_days} day The day that the course falls on
   */
  function addCourseToCalendar(course, startTime, endTime, day) {
    const startDate = createDateFromTimeString(startTime, day);
    const endDate = createDateFromTimeString(endTime, day);
    createSchedule(course, startDate, endDate);
  }

  /**
   * Creates a schedule on the calendar using the toast API
   * @param {Object} course The JSON Object for the course
   * @param {Date} startDate The Date object of the start time
   * @param {Date} endDate The Date object of the end time
   */
  function createSchedule(course, startDate, endDate) {
    // TODO(savsa): Have each schedule be a different color. The color is
    // currently hard coded to be blue so all schedules look the same.
    cal.createSchedules([{
      id: id.toString(),
      color: '#ffffff',
      bgColor: '#00a9ff',
      borderColor: '#00a9ff',
      calendarId: '1',
      title: course.name,
      category: 'time',
      location: course.building,
      start: startDate,
      end: endDate,
      isReadOnly: true,
    }]);
    // Each schedule needs a unique ID. We'll start at 1 and go up from there.
    id++;
  }

  /**
   * Creates a schedule on the calendar using the toast API
   * @param {enum_days} day The day that the course falls on
   * @param {int} hour The army-time hour portion of the time/date we want
   *     to create
   * @param {hour} min The army-time minute portion of the time/date we want
   *     to create
   * @return {Date} The new Date object
   */
  function createDate(day, hour, min) {
    const monday = new Date('January 3, 2000 00:00');
    const rightDay = new Date(monday.getTime());
    rightDay.setDate(monday.getDate() + day);
    return addTimeToDate(rightDay, hour, min);
  }

  /**
   * Adds time to a Date object
   * @param {Date} day The Date object
   * @param {int} hour The army-time amount of hours we want to add
   * @param {hour} min The army-time amount of minutes we want to add
   * @return {Date} The new Date object
   */
  function addTimeToDate(date, hours, mins) {
    const newDate = new Date(date.getTime());
    newDate.setHours(date.getHours() + hours);
    newDate.setMinutes(date.getMinutes() + mins);
    return newDate;
  }

  /**
   * Creates a Date object from the string representation of a time
   * @param {string} time The string representation of a 12-hour clock time eg.
   *     8:30pm
   * @param {enum_days} day The day that the course falls on
   * @return {Date} The new Date object
   */
  function createDateFromTimeString(time, day) {
    const split = time.split(':');
    let hour = parseInt(split[0]);
    const secondPart = split[1];  // from ':' onwards
    const minutes = parseInt(secondPart.substring(0, 2));
    const period = secondPart.substring(2, 4);

    // 12:XXam is 00:XX in army time
    if (period == 'am' && hour == 12) {
      hour = 0;
    }

    // 1:XXpm and onward needs an addtional +12 hours to convert to army
    // time
    if (period == 'pm' && hour >= 1 && hour <= 11) {
      hour += 12;
    }
    return createDate(day, hour, minutes);
  }

  window.addEventListener('load', () => {
    initCalendar();
  });

  return {addCourse: addCourse};
})();
