<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix ="fmt" %>
<!-- Begin Page Content -->
<div class="container-fluid">
  <!-- Page Heading -->
  <div class="d-sm-flex align-items-center justify-content-between mb-4" id="alert-container">
    <h1 class="h3 mb-0 text-gray-800">Scheduler</h1>
  </div>
  <div class="row">
    <!-- Course Selection Area -->
    <div class="col-xl-8 col-lg-7">
      <div class="card shadow mb-4">
        <!-- Card Header - Dropdown -->
        <div
          class="card-header py-3 d-flex flex-row align-items-center justify-content-between"
        >
          <h6 class="m-0 font-weight-bold text-primary">Choose a Course:</h6>
        </div>
        <!-- Card Body -->
        <div class="card-body">
          <form onsubmit="return false">
            <div class="form-row">
              <div class="form-group col-md-2">
                <label for="semesters"><b>Department:</b></label>
                <select id="departments" class="form-control"></select>
              </div>
              <div class="form-group col-md-8">
                <label for="semesters"><b>Course:</b></label>
                <select id="courses" class="form-control"></select>
              </div>
              <div class="form-group col-md-2 text-center">
                <br>
                <button
                  class="rounded-circle border-0 btn-lg"
                  id="add-selected"
                >
                  <i class="fas fa-plus"></i>
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
    <!-- Pie Chart -->
    <div class="col-xl-4 col-lg-5">
      <div class="card shadow mb-4">
        <!-- Card Header - Dropdown -->
        <div
          class="card-header py-3 d-flex flex-row align-items-center justify-content-between"
        >
          <h6 class="m-0 font-weight-bold text-primary">Selected Courses</h6>
        </div>
        <!-- Card Body -->
        <div class="card-body">
          <form class="course-list">
            <ul class="list-group" id="selected-classes"></ul>
            <br />
            <a href="#" class="btn btn-primary btn-icon-split">
              <span class="icon text-white-50">
                <i class="fas fa-arrow-right"></i>
              </span>
              <span class="text">Get Schedule</span>
            </a>
          </form>
        </div>
      </div>
    </div>
  </div>
  <!-- Calendar -->
  <div class="row">
    <div class="container-fluid">
      <div class="card shadow mb-4">
        <div
          class="card-header py-3 d-flex flex-row align-items-center justify-content-between"
        >
          <h6 class="m-0 font-weight-bold text-primary">Calendar:</h6>
          <ul class="pagination" id="calendar-pagination">
          </ul>
        </div>
        <div class="card-body">
          <div id="calendar"></div>
        </div>
      </div>
    </div>
  </div>
</div>
<!-- /.container-fluid -->
