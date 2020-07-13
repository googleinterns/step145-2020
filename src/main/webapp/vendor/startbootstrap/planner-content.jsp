<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix ="fmt" %>
<!-- Begin Page Content -->
<div class="container-fluid">
  <!-- Page Heading -->
  <div class="d-sm-flex align-items-center justify-content-between mb-4">
    <h1 class="h3 mb-0 text-gray-800">Planner</h1>
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
      <!-- list Area -->
      <div class="card shadow mb-4">
        <!-- Card Header - Dropdown -->
        <div
          class="card-header py-3 d-flex flex-row align-items-center justify-content-between"
        >
          <h6 class="m-0 font-weight-bold text-primary">Proposed Plan:</h6>
        </div>
        <!-- Card Body -->
        <div class="card-body">
          <center id="order-area">
            Your plan will appear here.
          </center>
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
          <form id="submit-plan" onsubmit="return false">
            <label for="semesters"><b>Number of Remaining Semesters:</b></label>
            <input
              type="number"
              class="form-control"
              id="semesters"
              min="1"
              max="12"
              required
            />
            <br />
            <b>Selected Courses:</b>
            <ul class="list-group" id="selected-classes"></ul>
            <br />
            <button
              type="submit"
              href="#"
              class="btn btn-primary btn-icon-split"
            >
              <span class="icon text-white-50">
                <i class="fas fa-arrow-right"></i>
              </span>
              <span class="text">Get Plan</span>
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>
