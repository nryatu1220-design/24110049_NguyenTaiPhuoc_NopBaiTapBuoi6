<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quên mật khẩu | Shopping</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/login.css'/>">
</head>
<body>
    <main class="login-page">
        <section class="login-card">
            <div class="logo">S</div>
            <h1>Quên mật khẩu</h1>
            <p>Nhập email đã đăng ký, chúng tôi sẽ gửi mã OTP để đặt lại mật khẩu.</p>
            <c:if test="${not empty info}"><div class="login-info"><c:out value="${info}"/></div></c:if>
            <c:if test="${not empty error}"><div class="login-error"><c:out value="${error}"/></div></c:if>
            <form action="<c:url value='/forgot-password'/>" method="post">
                <label for="email">Email</label>
                <input id="email" type="email" name="email" autocomplete="email" required autofocus maxlength="100" value="<c:out value='${email}'/>">
                <button type="submit">Gửi mã OTP</button>
            </form>
            <small><a href="<c:url value='/login'/>">Quay lại đăng nhập</a></small>
        </section>
    </main>
</body>
</html>
