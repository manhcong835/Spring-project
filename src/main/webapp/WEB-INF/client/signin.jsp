<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">

<head>
  <jsp:include page="_meta.jsp"/>
  <title>Đăng nhập - TourBooking</title>
</head>

<body>

<div class="auth-wrapper">
  <div class="auth-card card">
    <div class="card-body">

      <!-- Brand -->
      <div class="auth-brand">
        <div class="brand-icon"><i class="fas fa-globe-americas"></i></div>
        <h2>Chào mừng trở lại!</h2>
        <p>Đăng nhập để tiếp tục khám phá tour du lịch</p>
      </div>

      <!-- Error message -->
      <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger" role="alert">
          <i class="fas fa-exclamation-circle me-1"></i>${requestScope.errorMessage}
        </div>
      </c:if>

      <!-- Success message -->
      <c:if test="${not empty requestScope.successMessage}">
        <div class="alert alert-success" role="alert">
          <i class="fas fa-check-circle me-1"></i>${requestScope.successMessage}
        </div>
      </c:if>

      <!-- Form -->
      <form action="${pageContext.request.contextPath}/signin" method="post">

        <div class="mb-3">
          <label for="username" class="form-label">Tên đăng nhập hoặc Email</label>
          <input id="username" name="username"
                 class="form-control ${not empty requestScope.violations.usernameViolations
                   ? 'is-invalid' : (not empty requestScope.values.username ? 'is-valid' : '')}"
                 placeholder="Nhập tên đăng nhập hoặc email"
                 type="text"
                 autocomplete="off"
                 value="${requestScope.values.username}">
          <c:if test="${not empty requestScope.violations.usernameViolations}">
            <div class="invalid-feedback">
              <c:forEach var="violation" items="${requestScope.violations.usernameViolations}">
                ${violation}<br>
              </c:forEach>
            </div>
          </c:if>
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">Mật khẩu</label>
          <div class="input-password-wrapper">
            <input id="password" name="password"
                   class="form-control ${not empty requestScope.violations.passwordViolations
                     ? 'is-invalid' : (not empty requestScope.values.password ? 'is-valid' : '')}"
                   placeholder="Nhập mật khẩu"
                   type="password"
                   autocomplete="off"
                   value="${requestScope.values.password}">
            <button type="button" class="toggle-password" onclick="togglePasswordVisibility('password', this)">
              <i class="fas fa-eye"></i>
            </button>
            <c:if test="${not empty requestScope.violations.passwordViolations}">
              <div class="invalid-feedback">
                <c:forEach var="violation" items="${requestScope.violations.passwordViolations}">
                  ${violation}<br>
                </c:forEach>
              </div>
            </c:if>
          </div>
        </div>

        <div class="d-flex justify-content-between align-items-center mb-3">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" id="rememberMe" name="rememberMe">
            <label class="form-check-label" for="rememberMe">Ghi nhớ đăng nhập</label>
          </div>
          <a href="${pageContext.request.contextPath}/forgot-password" class="small" style="color: var(--auth-primary); text-decoration: none;">Quên mật khẩu?</a>
        </div>

        <button type="submit" class="btn btn-auth">
          <i class="fas fa-sign-in-alt me-1"></i>Đăng nhập
        </button>
      </form>

      <!-- Divider -->
      <div class="auth-divider"><span>hoặc</span></div>

      <!-- Social login -->
      <button class="btn-social mb-2">
        <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" alt="Google">
        Đăng nhập với Google
      </button>
      <button class="btn-social">
        <i class="fab fa-facebook-f" style="color: #1877f2; font-size: 18px;"></i>
        Đăng nhập với Facebook
      </button>

    </div>
  </div>

  <!-- Footer link -->
  <div class="auth-footer" style="position: absolute; bottom: 30px;">
    Chưa có tài khoản? <a href="${pageContext.request.contextPath}/signup">Đăng ký ngay</a>
  </div>
</div>

<script>
function togglePasswordVisibility(inputId, btn) {
  var input = document.getElementById(inputId);
  var icon = btn.querySelector('i');
  if (input.type === 'password') {
    input.type = 'text';
    icon.classList.replace('fa-eye', 'fa-eye-slash');
  } else {
    input.type = 'password';
    icon.classList.replace('fa-eye-slash', 'fa-eye');
  }
}
</script>
</body>

</html>
