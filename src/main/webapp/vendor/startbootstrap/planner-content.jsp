<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix ="fmt" %>
<!-- Begin Page Content -->
<div class="container-fluid">
  <!-- Page Heading -->
  <div class="d-sm-flex align-items-center justify-content-between mb-4" id="alert-container">
    <h1 class="h3 mb-0 text-gray-800">Planner</h1>
    <button
        href="#"
        class="btn btn-primary btn-icon-split"
        id="see-saved"
    >
      <span class="icon text-white-50">
        <i class="fas fa-star"></i>
      </span>
      <span class="text">See Saved Plans</span>
    </button>
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
          <div id="plan-header">
          </div>
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
<!-- Save Planner Modal-->
<div class="modal fade" id="savePlanModal" tabindex="-1" role="dialog" aria-labelledby="savePlanModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="savePlanModalLabel">Save this plan:</h5>
      </div>
      <div class="modal-body">
        <form id="plan-name" onsubmit="return false">
          <input type="text" class="form-control" placeholder="Type name here" id="save-plan-as" required></input>
        </form>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" type="button" data-dismiss="modal">Cancel</button>
        <button class="btn btn-primary" type="submit" form="plan-name">Submit</button>
      </div>
    </div>
  </div>
</div>
<!-- Sign in Prompt Modal -->
<div class="modal fade" id="signInModal" tabindex="-1" role="dialog" aria-labelledby="signInModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="signInModalLabel">Please sign in to save a schedule.</h5>
      </div>
      <div class="modal-body">
        <center>
          <div class="g-signin2" data-onsuccess="onSignIn"></div>
        </center>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" type="button" data-dismiss="modal">Dismiss</button>
      </div>
    </div>
  </div>
</div>
<!-- Sign in Prompt Modal -->
<div class="modal fade" id="signInModal" tabindex="-1" role="dialog" aria-labelledby="signInModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="signInModalLabel">Sign in to see your saved schedules.</h5>
      </div>
      <div class="modal-body">
        <center>
          <div class="g-signin2" data-onsuccess="onSignIn"></div>
        </center>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" type="button" data-dismiss="modal">Dismiss</button>
      </div>
    </div>
  </div>
</div>
