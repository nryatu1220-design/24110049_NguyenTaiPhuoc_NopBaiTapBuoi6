<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập | Shopping Admin</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/login.css'/>">
</head>
<body>
    <main class="login-page">
        <section class="login-card">
            <div class="logo">S</div>
            <h1>Shopping Admin</h1>
            <p>Đăng nhập để tiếp tục quản lý cửa hàng.</p>
            <c:if test="${not empty success}"><div class="login-success"><c:out value="${success}"/></div></c:if>
            <c:if test="${not empty error}"><div class="login-error"><c:out value="${error}"/></div></c:if>
            <form action="<c:url value='/login'/>" method="post">
                <label for="username">Tên đăng nhập</label>
                <input id="username" name="username" autocomplete="username" required maxlength="30" autofocus value="<c:out value='${username}'/>">
                <label for="password">Mật khẩu</label>
                <input id="password" type="password" name="password" autocomplete="current-password" required>
                <a class="forgot" href="<c:url value='/forgot-password'/>">Quên mật khẩu?</a>
                <button type="submit">Đăng nhập</button>
            </form>
            <small>Chưa có tài khoản? <a href="<c:url value='/register'/>">Đăng ký ngay</a></small>
        </section>
    </main>
</body>
</html>
