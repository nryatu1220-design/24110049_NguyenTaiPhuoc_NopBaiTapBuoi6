<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hồ sơ cá nhân | Shopping</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/site.css'/>">
</head>
<body>
<jsp:include page="/views/web/header.jsp"><jsp:param name="active" value="profile"/></jsp:include>
<main class="container page">
    <div class="page-head">
        <div><h1>Hồ sơ cá nhân</h1><p>Cập nhật thông tin tài khoản của bạn.</p></div>
    </div>
    <c:if test="${not empty profileFlash}"><div class="alert alert-ok"><c:out value="${profileFlash}"/></div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-err"><c:out value="${error}"/></div></c:if>
    <c:set var="p" value="${profile}" />
    <form class="panel profile-panel" action="<c:url value='/profile'/>" method="post" enctype="multipart/form-data">
        <div class="profile-avatar">
            <c:choose>
                <c:when test="${not empty p.avatarUrl}">
                    <img src="<c:out value='${p.avatarUrl}'/>" alt="Ảnh đại diện">
                </c:when>
                <c:otherwise>
                    <div class="avatar-placeholder"><c:out value="${p.fullname.substring(0,1).toUpperCase()}"/></div>
                </c:otherwise>
            </c:choose>
        </div>
        <label for="fullname">Họ tên</label>
        <input id="fullname" name="fullname" type="text" maxlength="100" required value="<c:out value='${p.fullname}'/>">
        <label for="phone">Số điện thoại</label>
        <input id="phone" name="phone" type="text" maxlength="20" value="<c:out value='${p.phone}'/>">
        <label for="email">Email</label>
        <input id="email" name="email" type="email" maxlength="100" required value="<c:out value='${p.email}'/>">
        <p class="hint">Khi đổi email, mã OTP sẽ được gửi đến email mới để xác nhận.</p>
        <label for="avatar">Ảnh đại diện</label>
        <input id="avatar" name="avatar" type="file" accept="image/*">
        <p class="hint">Tối đa 5 MB. Ảnh được tải lên Cloudinary.</p>
        <div class="form-actions">
            <button class="btn" type="submit">Cập nhật hồ sơ</button>
            <a class="btn btn-ghost" href="<c:url value='/home'/>">Hủy</a>
        </div>
    </form>
</main>
<footer class="site-footer">© Shopping</footer>
</body>
</html>
