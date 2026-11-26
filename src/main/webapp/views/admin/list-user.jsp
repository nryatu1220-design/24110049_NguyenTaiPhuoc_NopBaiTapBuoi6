<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý người dùng</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/dashboard.css'/>">
    <link rel="stylesheet" href="<c:url value='/assets/css/dashboard-layout.css'/>">
</head>
<body>
<main class="main-content" style="margin-left:0">
    <header class="topbar"><div class="breadcrumb"><a href="<c:url value='/admin/dashboard'/>">Trang quản trị</a> / <strong>Người dùng</strong></div></header>
    <section class="page-content">
        <h1>Quản lý người dùng</h1>
        <div class="toolbar">
            <a class="button button-primary" href="<c:url value='/admin/user/add'/>">＋ Thêm người dùng</a>
            <form class="search-form" action="<c:url value='/admin/user/list'/>" method="get">
                <input class="search-input" type="search" name="keyword" value="<c:out value='${keyword}'/>" placeholder="Tên đăng nhập, họ tên hoặc email">
                <button class="button button-secondary" type="submit">Tìm kiếm</button>
            </form>
        </div>
        <section class="panel"><div class="table-wrap">
            <table class="category-table">
                <thead><tr><th>STT</th><th>Tên đăng nhập</th><th>Họ tên</th><th>Email</th><th>Điện thoại</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
                <tbody>
                <c:forEach items="${users}" var="user" varStatus="row">
                    <tr>
                        <td>${offset + row.index + 1}</td>
                        <td><c:out value="${user.username}"/></td>
                        <td><c:out value="${user.fullname}"/></td>
                        <td><c:out value="${user.email}"/></td>
                        <td><c:out value="${user.phone}"/></td>
                        <td><c:choose><c:when test="${user.active}">Hoạt động</c:when><c:otherwise>Chưa kích hoạt / khóa</c:otherwise></c:choose></td>
                        <td>
                            <a class="button button-secondary" href="<c:url value='/admin/user/edit?id=${user.userId}'/>">Sửa</a>
                            <c:if test="${sessionScope.userId != user.userId}">
                                <form action="<c:url value='/admin/user/delete'/>" method="post" style="display:inline" onsubmit="return confirm('Xóa người dùng này? Video của họ sẽ được giữ lại.');">
                                    <input type="hidden" name="id" value="${user.userId}">
                                    <button class="button button-danger" type="submit">Xóa</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty users}"><tr><td colspan="7" class="table-empty">Không có người dùng phù hợp.</td></tr></c:if>
                </tbody>
            </table>
        </div></section>
        <c:if test="${totalPages > 1}">
            <div class="toolbar" style="justify-content:center;gap:8px;margin-top:16px;">
                <c:if test="${page > 1}">
                    <c:url value="/admin/user/list" var="previousPage"><c:param name="page" value="${page - 1}"/><c:param name="keyword" value="${keyword}"/></c:url>
                    <a class="button button-secondary" href="${previousPage}">← Trước</a>
                </c:if>
                <span>Trang ${page} / ${totalPages}</span>
                <c:if test="${page < totalPages}">
                    <c:url value="/admin/user/list" var="nextPage"><c:param name="page" value="${page + 1}"/><c:param name="keyword" value="${keyword}"/></c:url>
                    <a class="button button-secondary" href="${nextPage}">Sau →</a>
                </c:if>
            </div>
        </c:if>
        <p><a href="<c:url value='/admin/dashboard'/>">Quay lại trang quản trị</a></p>
    </section>
</main>
</body>
</html>
