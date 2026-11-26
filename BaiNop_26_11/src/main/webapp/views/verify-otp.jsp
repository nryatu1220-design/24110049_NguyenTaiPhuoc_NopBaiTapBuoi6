<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Kích hoạt tài khoản | Shopping</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/login.css'/>">
</head>
<body>
    <main class="login-page">
        <section class="login-card">
            <div class="logo">S</div>
            <h1>Kích hoạt tài khoản</h1>
            <p>Nhập mã OTP gồm 6 chữ số đã gửi đến <strong><c:out value="${maskedEmail}"/></strong>.</p>
            <c:if test="${not empty info}"><div class="login-info"><c:out value="${info}"/></div></c:if>
            <c:if test="${not empty error}"><div class="login-error"><c:out value="${error}"/></div></c:if>
            <form action="<c:url value='/verify-otp'/>" method="post">
                <label for="otp">Mã OTP</label>
                <input id="otp" class="otp-input" name="otp" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" required autofocus>
                <button type="submit">Kích hoạt</button>
            </form>
            <small>Chưa nhận được mã?
                <form action="<c:url value='/verify-otp'/>" method="post" style="display:inline">
                    <input type="hidden" name="action" value="resend">
                    <button type="submit" class="link-btn">Gửi lại mã</button>
                </form>
                &nbsp;•&nbsp; <a href="<c:url value='/login'/>">Quay lại đăng nhập</a></small>
        </section>
    </main>
</body>
</html>
