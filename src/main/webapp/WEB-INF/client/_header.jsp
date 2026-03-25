<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!-- Client Header - Tour Booking -->
<header>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
    <div class="container">
      <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
        <i class="fas fa-globe-americas me-2"></i>TourBooking
      </a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarClient">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarClient">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/">Trang chủ</a></li>
          <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/tours">Tour du lịch</a></li>
          <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/about">Giới thiệu</a></li>
          <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/contact">Liên hệ</a></li>
        </ul>
        <div class="d-flex align-items-center gap-2">
          <a href="${pageContext.request.contextPath}/signin" class="btn btn-outline-light btn-sm">Đăng nhập</a>
          <a href="${pageContext.request.contextPath}/signup" class="btn btn-primary btn-sm">Đăng ký</a>
        </div>
      </div>
    </div>
  </nav>
</header>
