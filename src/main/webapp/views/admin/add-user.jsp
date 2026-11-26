<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Thêm người dùng</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/dashboard.css'/>"><link rel="stylesheet" href="<c:url value='/assets/css/dashboard-layout.css'/>"></head>
<body><main class="main-content" style="margin-left:0"><section class="page-content">
    <h1>Thêm người dùng</h1>
    <c:if test="${not empty error}"><p class="status status-pending"><c:out value="${error}"/></p></c:if>
    <form class="panel" action="<c:url value='/admin/user/add'/>" method="post" style="padding:24px;max-width:720px">
        <label>Tên đăng nhập <input class="search-input" name="username" maxlength="30" required value="<c:out value='${username}'/>"></label><br><br>
        <label>Họ tên <input class="search-input" name="fullname" maxlength="100" required value="<c:out value='${fullname}'/>"></label><br><br>
        <label>Email <input class="search-input" type="email" name="email" maxlength="100" required value="<c:out value='${email}'/>"></label><br><br>
        <label>Điện thoại <input class="search-input" name="phone" maxlength="20" value="<c:out value='${phone}'/>"></label><br><br>
        <label>Mật khẩu tạm thời (tối thiểu 6 ký tự) <input class="search-input" type="password" name="password" minlength="6" required autocomplete="new-password"></label><br><br>
        <label><input type="checkbox" name="active" value="1" <c:if test="${active}">checked</c:if>> Kích hoạt tài khoản</label><br><br>
        <button class="button button-primary" type="submit">Tạo người dùng</button>
        <a class="button button-secondary" href="<c:url value='/admin/user/list'/>">Hủy</a>
    </form>
</section></main></body></html>
