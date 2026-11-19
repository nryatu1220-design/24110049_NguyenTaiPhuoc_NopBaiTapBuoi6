<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quản lý danh mục</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/dashboard.css'/>">
    <link rel="stylesheet" href="<c:url value='/assets/css/dashboard-layout.css'/>">
</head>
<body>
<div class="admin-layout">
    <aside class="sidebar" id="sidebar">
        <a class="brand" href="<c:url value='/admin/dashboard'/>">
            <span class="brand-mark">S</span>
            <span>SHOPPING<span class="brand-sub">ADMIN PANEL</span></span>
        </a>
        <div class="sidebar-label">MENU CHÍNH</div>
        <nav class="side-nav">
            <a class="nav-link" href="<c:url value='/admin/dashboard'/>">
                <span class="nav-icon">▦</span>
                <span>Dashboard</span>
            </a>
            <a class="nav-link active" href="<c:url value='/admin/category/list'/>">
                <span class="nav-icon">▤</span>
                <span>Quản lý danh mục</span>
            </a>
            <a class="nav-link" href="<c:url value='/video'/>">
                <span class="nav-icon">▶</span>
                <span>Video của tôi</span>
            </a>
            <a class="nav-link" href="<c:url value='/home'/>">
                <span class="nav-icon">⌂</span>
                <span>Trang chủ</span>
            </a>
        </nav>
        <div class="sidebar-bottom">
            <div class="sidebar-user">
                <div class="avatar">${sessionScope.fullname.substring(0,1).toUpperCase()}</div>
                <div>
                    <strong><c:out value="${sessionScope.fullname}"/></strong>
                    <small>@${sessionScope.username}</small>
                </div>
                <span class="user-dot"></span>
            </div>
        </div>
    </aside>

    <main class="main-content">
        <header class="topbar">
            <button class="menu-toggle" id="menuToggle" aria-label="Mở menu">☰</button>
            <div class="breadcrumb">Trang quản trị <span>/</span> <strong>Danh mục</strong></div>
            <div class="top-actions">
                <span class="today-label" id="todayLabel"></span>
                <div class="top-avatar">${sessionScope.fullname.substring(0,1).toUpperCase()}</div>
            </div>
        </header>

        <section class="page-content">
            <div class="page-heading">
                <div>
                    <div class="eyebrow"><span class="live-dot"></span> QUẢN LÝ CỬA HÀNG</div>
                    <h1>Danh mục sản phẩm</h1>
                    <p class="page-description">Tạo, tìm kiếm, cập nhật và xóa các danh mục của cửa hàng.</p>
                </div>
            </div>

            <div class="toolbar">
                <a class="button button-primary" href="<c:url value='/admin/category/add'/>">＋ Thêm danh mục</a>
                <c:url value="/admin/category/list" var="listUrl"/>
                <form class="search-form" action="${listUrl}" method="get">
                    <input type="text" name="keyword" class="search-input" placeholder="Tìm theo tên danh mục..." value="<c:out value='${keyword}'/>">
                    <button type="submit" class="button button-secondary">Tìm kiếm</button>
                </form>
            </div>

            <section class="panel">
                <div class="table-wrap">
                    <table class="category-table">
                        <thead>
                        <tr>
                            <th>STT</th>
                            <th>Hình ảnh</th>
                            <th>Tên danh mục</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${cateList}" var="cate" varStatus="STT">
                            <tr>
                                <td class="table-number">${offset + STT.index + 1}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty cate.images}">
                                            <c:url value="/image?fname=${cate.images}" var="imgUrl"/>
                                            <img class="category-image" src="${imgUrl}" alt="Ảnh ${cate.categoryname}">
                                        </c:when>
                                        <c:otherwise>
                                            <span class="image-fallback">▤</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="category-name"><c:out value="${cate.categoryname}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${cate.status == 1}"><span class="status status-ready">Hoạt động</span></c:when>
                                        <c:otherwise><span class="status status-pending">Khóa</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="actions">
                                        <a class="button button-secondary" href="<c:url value='/admin/category/edit?id=${cate.categoryId}'/>">Sửa</a>
                                        <a class="button button-danger" href="<c:url value='/admin/category/delete?id=${cate.categoryId}'/>" onclick="return confirm('Bạn có chắc muốn xóa danh mục này?');">Xóa</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty cateList}">
                            <tr>
                                <td colspan="5" class="table-empty">Không có danh mục nào phù hợp</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </section>

            <c:if test="${totalPages > 1}">
                <div class="toolbar" style="justify-content:center;gap:8px;margin-top:16px;">
                    <c:if test="${page > 1}">
                        <a class="button button-secondary" href="<c:url value='/admin/category/list?page=${page - 1}'/>">← Trước</a>
                    </c:if>
                    <span>Trang ${page} / ${totalPages}</span>
                    <c:if test="${page < totalPages}">
                        <a class="button button-secondary" href="<c:url value='/admin/category/list?page=${page + 1}'/>">Sau →</a>
                    </c:if>
                </div>
            </c:if>

            <footer class="page-footer">© <span id="yearLabel"></span> Shopping Admin <span>•</span> Built for store management</footer>
        </section>
    </main>
</div>

<div class="mobile-overlay" id="mobileOverlay"></div>
<script src="<c:url value='/assets/js/dashboard.js'/>"></script>
</body>
</html>