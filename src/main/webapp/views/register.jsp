<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng ký | Shopping Admin</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/login.css'/>">
</head>
<body>
    <main class="login-page">
        <section class="login-card">
            <div class="logo">S</div>
            <h1>Tạo tài khoản</h1>
            <p>Đăng ký để quản lý cửa hàng của bạn.</p>
            <c:if test="${not empty error}"><div class="login-error"><c:out value="${error}"/></div></c:if>
            <form action="<c:url value='/register'/>" method="post">
                <label for="fullname">Họ và tên</label>
                <input id="fullname" name="fullname" autocomplete="name" required autofocus maxlength="100" value="<c:out value='${fullname}'/>">
                <label for="email">Email</label>
                <input id="email" type="email" name="email" autocomplete="email" required maxlength="100" value="<c:out value='${email}'/>">
                <label for="username">Tên đăng nhập</label>
                <input id="username" name="username" autocomplete="username" required pattern="[A-Za-z0-9_]{3,30}" title="3-30 ký tự: chữ không dấu, số hoặc dấu gạch dưới" value="<c:out value='${username}'/>">
                <label for="password">Mật khẩu (ít nhất 6 ký tự)</label>
                <input id="password" type="password" name="password" autocomplete="new-password" required minlength="6">
                <label for="confirm">Nhập lại mật khẩu</label>
                <input id="confirm" type="password" name="confirm" autocomplete="new-password" required minlength="6">
                <button type="submit">Đăng ký</button>
            </form>
            <small>Đã có tài khoản? <a href="<c:url value='/login'/>">Đăng nhập</a></small>
        </section>
    </main>
</body>
</html>
