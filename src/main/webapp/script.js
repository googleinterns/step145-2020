window.addEventListener('load', () => {
  function initCalendar() {
    new tui.Calendar('#calendar', {
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
    });
  }

  initCalendar();
});
