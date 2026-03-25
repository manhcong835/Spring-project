<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">

<head>
  <jsp:include page="_meta.jsp"/>
  <title>Đăng ký - TourBooking</title>
</head>

<body>

<div class="auth-wrapper">
  <div class="auth-card card" style="max-width: 480px;">
    <div class="card-body">

      <!-- Brand -->
      <div class="auth-brand">
        <div class="brand-icon"><i class="fas fa-globe-americas"></i></div>
        <h2>Tạo tài khoản mới</h2>
        <p>Đăng ký để bắt đầu hành trình khám phá</p>
      </div>

      <!-- Error message -->
      <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger" role="alert">
          <i class="fas fa-exclamation-circle me-1"></i>${requestScope.errorMessage}
        </div>
      </c:if>

      <!-- Form -->
      <form action="${pageContext.request.contextPath}/signup" method="post">

        <div class="mb-3">
          <label for="fullName" class="form-label">Họ và tên</label>
          <input id="fullName" name="fullName"
                 class="form-control ${not empty requestScope.violations.fullNameViolations
                   ? 'is-invalid' : (not empty requestScope.values.fullName ? 'is-valid' : '')}"
                 placeholder="Nhập họ và tên"
                 type="text"
                 autocomplete="off"
                 value="${requestScope.values.fullName}">
          <c:if test="${not empty requestScope.violations.fullNameViolations}">
            <div class="invalid-feedback">
              <c:forEach var="violation" items="${requestScope.violations.fullNameViolations}">
                ${violation}<br>
              </c:forEach>
            </div>
          </c:if>
        </div>

        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
          <input id="email" name="email"
                 class="form-control ${not empty requestScope.violations.emailViolations
                   ? 'is-invalid' : (not empty requestScope.values.email ? 'is-valid' : '')}"
                 placeholder="Nhập địa chỉ email"
                 type="email"
                 autocomplete="off"
                 value="${requestScope.values.email}">
          <c:if test="${not empty requestScope.violations.emailViolations}">
            <div class="invalid-feedback">
              <c:forEach var="violation" items="${requestScope.violations.emailViolations}">
                ${violation}<br>
              </c:forEach>
            </div>
          </c:if>
        </div>

        <div class="mb-3">
          <label for="phone" class="form-label">Số điện thoại</label>
          <input id="phone" name="phone"
                 class="form-control ${not empty requestScope.violations.phoneViolations
                   ? 'is-invalid' : (not empty requestScope.values.phone ? 'is-valid' : '')}"
                 placeholder="Nhập số điện thoại"
                 type="tel"
                 autocomplete="off"
                 value="${requestScope.values.phone}">
          <c:if test="${not empty requestScope.violations.phoneViolations}">
            <div class="invalid-feedback">
              <c:forEach var="violation" items="${requestScope.violations.phoneViolations}">
                ${violation}<br>
              </c:forEach>
            </div>
          </c:if>
        </div>

        <div class="row">
          <div class="col-md-6 mb-3">
            <label for="password" class="form-label">Mật khẩu</label>
            <div class="input-password-wrapper">
              <input id="password" name="password"
                     class="form-control ${not empty requestScope.violations.passwordViolations
                       ? 'is-invalid' : (not empty requestScope.values.password ? 'is-valid' : '')}"
                     placeholder="Nhập mật khẩu"
                     type="password"
                     autocomplete="off">
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
          <div class="col-md-6 mb-3">
            <label for="confirmPassword" class="form-label">Xác nhận mật khẩu</label>
            <div class="input-password-wrapper">
              <input id="confirmPassword" name="confirmPassword"
                     class="form-control ${not empty requestScope.violations.confirmPasswordViolations
                       ? 'is-invalid' : ''}"
                     placeholder="Nhập lại mật khẩu"
                     type="password"
                     autocomplete="off">
              <button type="button" class="toggle-password" onclick="togglePasswordVisibility('confirmPassword', this)">
                <i class="fas fa-eye"></i>
              </button>
              <c:if test="${not empty requestScope.violations.confirmPasswordViolations}">
                <div class="invalid-feedback">
                  <c:forEach var="violation" items="${requestScope.violations.confirmPasswordViolations}">
                    ${violation}<br>
                  </c:forEach>
                </div>
              </c:if>
            </div>
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label">Giới tính</label>
          <div class="d-flex gap-3">
            <div class="form-check">
              <input class="form-check-input" type="radio" name="gender" id="genderMale" value="1"
                     ${requestScope.values.gender == '1' ? 'checked' : ''}>
              <label class="form-check-label" for="genderMale">Nam</label>
            </div>
            <div class="form-check">
              <input class="form-check-input" type="radio" name="gender" id="genderFemale" value="0"
                     ${requestScope.values.gender == '0' ? 'checked' : ''}>
              <label class="form-check-label" for="genderFemale">Nữ</label>
            </div>
          </div>
        </div>

        <div class="form-check mb-3">
          <input class="form-check-input" type="checkbox" id="agreeTerms" name="agreeTerms" required>
          <label class="form-check-label" for="agreeTerms">
            Tôi đồng ý với <a href="#" style="color: var(--auth-primary);">Điều khoản dịch vụ</a> và
            <a href="#" style="color: var(--auth-primary);">Chính sách bảo mật</a>
          </label>
        </div>

        <button type="submit" class="btn btn-auth">
          <i class="fas fa-user-plus me-1"></i>Đăng ký
        </button>
      </form>

      <!-- Divider -->
      <div class="auth-divider"><span>hoặc</span></div>

      <!-- Social -->
      <button class="btn-social mb-2">
        <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" alt="Google">
        Đăng ký với Google
      </button>

    </div>
  </div>

  <!-- Footer link -->
  <div class="auth-footer" style="position: absolute; bottom: 30px;">
    Đã có tài khoản? <a href="${pageContext.request.contextPath}/signin">Đăng nhập</a>
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
