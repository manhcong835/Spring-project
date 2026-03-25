<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">

<head>
  <jsp:include page="_meta.jsp"/>
  <title>Đăng nhập Admin - TourBooking</title>
</head>

<body>

<div class="auth-wrapper admin-auth">
  <div class="auth-card card">
    <div class="card-body">

      <!-- Brand -->
      <div class="auth-brand">
        <div class="brand-icon"><i class="fas fa-shield-alt"></i></div>
        <h2>Quản trị viên</h2>
        <p>Đăng nhập vào hệ thống quản trị TourBooking</p>
      </div>

      <!-- Error message -->
      <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger" role="alert">
          <i class="fas fa-exclamation-circle me-1"></i>${requestScope.errorMessage}
        </div>
      </c:if>

      <!-- Form -->
      <form action="${pageContext.request.contextPath}/admin/signin" method="post">

        <div class="mb-3">
          <label for="username" class="form-label">Tên đăng nhập</label>
          <div class="input-group">
            <span class="input-group-text" style="border-radius: 10px 0 0 10px; border: 1.5px solid #dee2e6;">
              <i class="fas fa-user text-muted"></i>
            </span>
            <input id="username" name="username"
                   class="form-control ${not empty requestScope.violations.usernameViolations
                     ? 'is-invalid' : (not empty requestScope.values.username ? 'is-valid' : '')}"
                   placeholder="Nhập tên đăng nhập"
                   type="text"
                   autocomplete="off"
                   value="${requestScope.values.username}"
                   style="border-radius: 0 10px 10px 0;">
            <c:if test="${not empty requestScope.violations.usernameViolations}">
              <div class="invalid-feedback">
                <c:forEach var="violation" items="${requestScope.violations.usernameViolations}">
                  ${violation}<br>
                </c:forEach>
              </div>
            </c:if>
          </div>
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">Mật khẩu</label>
          <div class="input-group">
            <span class="input-group-text" style="border-radius: 10px 0 0 10px; border: 1.5px solid #dee2e6;">
              <i class="fas fa-lock text-muted"></i>
            </span>
            <input id="password" name="password"
                   class="form-control ${not empty requestScope.violations.passwordViolations
                     ? 'is-invalid' : (not empty requestScope.values.password ? 'is-valid' : '')}"
                   placeholder="Nhập mật khẩu"
                   type="password"
                   autocomplete="off"
                   value="${requestScope.values.password}"
                   style="border-radius: 0 10px 10px 0;">
            <c:if test="${not empty requestScope.violations.passwordViolations}">
              <div class="invalid-feedback">
                <c:forEach var="violation" items="${requestScope.violations.passwordViolations}">
                  ${violation}<br>
                </c:forEach>
              </div>
            </c:if>
          </div>
        </div>

        <div class="form-check mb-3">
          <input class="form-check-input" type="checkbox" id="rememberMe" name="rememberMe">
          <label class="form-check-label" for="rememberMe">Ghi nhớ đăng nhập</label>
        </div>

        <button type="submit" class="btn btn-auth">
          <i class="fas fa-sign-in-alt me-1"></i>Đăng nhập
        </button>
      </form>

      <!-- Security notice -->
      <div class="text-center mt-4">
        <small class="text-muted">
          <i class="fas fa-lock me-1"></i>Kết nối được bảo mật bằng SSL
        </small>
      </div>

    </div>
  </div>

  <!-- Footer link -->
  <div class="auth-footer" style="position: absolute; bottom: 30px;">
    <a href="${pageContext.request.contextPath}/">← Quay lại trang chủ</a>
  </div>
</div>

</body>

</html>
