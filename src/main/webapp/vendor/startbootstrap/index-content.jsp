<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix ="fmt" %>
<!-- Begin Page Content -->
<div class="container-fluid">
  <!-- Page Heading -->
  <div class="d-sm-flex align-items-center justify-content-between mb-4" id="alert-container">
  </div>
  <div class="row">
    <!-- Course Selection Area -->
    <div class="col-xl-12">
      <div class="card shadow mb-4">
        <div class="card-body">
          <center>
            <div class="sidebar-brand-icon rotate-n-15 large-icon">
              <i class="fas fa-calendar"></i>
            </div>
            <img src="${param.directory}img/Logo-dark.png" width="500">
            <br>
            <p class="index-text">A one-stop-shop for all of your course scheduling needs!</p>
          </center>
        </div>
      </div>
    </div>
  </div>
  <div class="row">
    <!-- Course Selection Area -->
    <div class="col-xl-4">
      <div class="card shadow mb-4">
        <!-- Card Header - Dropdown -->
        <div
          class="card-header py-3 d-flex flex-row align-items-center justify-content-between"
        >
          <h6 class="m-0 font-weight-bold text-primary">Scheduler</h6>
        </div>
        <div class="card-body">
          <center>
            <div class="sidebar-brand-icon large-icon">
              <i class="fas fa-calendar"></i>
            </div>
            <br>
            <p class="index-text">Use our scheduler to get automatically generated schedules for the courses that you want to take.</p>
          </center>
        </div>
      </div>
    </div>
    <div class="col-xl-4">
      <div class="card shadow mb-4">
        <!-- Card Header - Dropdown -->
        <div
          class="card-header py-3 d-flex flex-row align-items-center justify-content-between"
        >
          <h6 class="m-0 font-weight-bold text-primary">Planner</h6>
        </div>
        <div class="card-body">
          <center>
            <div class="sidebar-brand-icon large-icon">
              <i class="fas fa-book"></i>
            </div>
            <br>
            <p class="index-text">Use our planner to plan your entire academic career, accounting for prerequisites, corequisites, and other restrictions.</p>
          </center>
        </div>
      </div>
    </div>
    <div class="col-xl-4">
      <div class="card shadow mb-4">
        <!-- Card Header - Dropdown -->
        <div
          class="card-header py-3 d-flex flex-row align-items-center justify-content-between"
        >
          <h6 class="m-0 font-weight-bold text-primary">Sign Up!</h6>
        </div>
        <div class="card-body">
          <center>
            <div class="sidebar-brand-icon large-icon">
              <i class="fas fa-star"></i>
            </div>
            <br>
            <p class="index-text">Sign in to save plans, so you can access them next week, next month, or next semester!</p>
          </center>
        </div>
      </div>
    </div>
  </div>
</div>
<!-- /.container-fluid -->
