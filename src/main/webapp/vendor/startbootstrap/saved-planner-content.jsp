<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix ="fmt" %>
<!-- Begin Page Content -->
<div class="container-fluid">
  <!-- Page Heading -->
  <div class="d-sm-flex align-items-center justify-content-between mb-4" id="alert-container">
    <h1 class="h3 mb-0 text-gray-800">Planner: My Saved Plans</h1>
  </div>
  <div class="row">
    <!-- Course Selection Area -->
    <div class="col-xl-12" id= "plan-column">
        To view your saved plans, please sign in. 
    </div>
  </div>
</div>
<!-- Delete Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="deleteModalLabel">Are you sure you want to delete the following plan?</h5>
      </div>
      <div class="modal-body">
        <center id="plan-name"></center>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" type="button" data-dismiss="modal">Cancel</button>
        <button class="btn btn-primary" id="confirm-delete">Delete</button>
      </div>
    </div>
  </div>
</div>
