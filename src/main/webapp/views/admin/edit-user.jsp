<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Cập nhật người dùng</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/dashboard.css'/>"><link rel="stylesheet" href="<c:url value='/assets/css/dashboard-layout.css'/>"></head>
<body><main class="main-content" style="margin-left:0"><section class="page-content">
    <h1>Cập nhật người dùng</h1>
    <c:if test="${not empty error}"><p class="status status-pending"><c:out value="${error}"/></p></c:if>
    <form class="panel" action="<c:url value='/admin/user/edit'/>" method="post" style="padding:24px;max-width:720px">
        <input type="hidden" name="id" value="${user.userId}">
        <p>Tên đăng nhập: <strong><c:out value="${user.username}"/></strong> (không thể thay đổi)</p>
        <label>Họ tên <input class="search-input" name="fullname" maxlength="100" required value="<c:out value='${user.fullname}'/>"></label><br><br>
        <label>Email <input class="search-input" type="email" name="email" maxlength="100" required value="<c:out value='${user.email}'/>"></label><br><br>
        <label>Điện thoại <input class="search-input" name="phone" maxlength="20" value="<c:out value='${user.phone}'/>"></label><br><br>
        <label><input type="checkbox" name="active" value="1" <c:if test="${user.active}">checked</c:if>> Tài khoản đang hoạt động</label><br><br>
        <p>Mật khẩu và dữ liệu OTP không bị thay đổi tại đây.</p>
        <button class="button button-primary" type="submit">Lưu thay đổi</button>
        <a class="button button-secondary" href="<c:url value='/admin/user/list'/>">Hủy</a>
    </form>
</section></main></body></html>
