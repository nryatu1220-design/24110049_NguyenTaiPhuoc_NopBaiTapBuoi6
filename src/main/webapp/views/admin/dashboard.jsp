<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard | Shopping Admin</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/dashboard.css'/>">
    <link rel="stylesheet" href="<c:url value='/assets/css/dashboard-layout.css'/>">
</head>
<body>
<div class="admin-layout">
    <aside class="sidebar" id="sidebar">
        <a class="brand" href="<c:url value='/admin/dashboard'/>">
            <span class="brand-mark">S</span><span>SHOPPING<span class="brand-sub">ADMIN PANEL</span></span>
        </a>
        <div class="sidebar-label">MENU CHÍNH</div>
        <nav class="side-nav">
            <a class="nav-link active" href="<c:url value='/admin/dashboard'/>"><span class="nav-icon">▦</span><span>Dashboard</span></a>
            <a class="nav-link" href="<c:url value='/admin/category/list'/>"><span class="nav-icon">▤</span><span>Quản lý danh mục</span><span class="nav-count">${categoryCount}</span></a>
            <a class="nav-link" href="<c:url value='/video'/>"><span class="nav-icon">▶</span><span>Video của tôi</span></a>
            <a class="nav-link" href="<c:url value='/home'/>"><span class="nav-icon">⌂</span><span>Trang chủ</span></a>
            <div class="nav-link disabled" title="Project hiện chưa có chức năng này"><span class="nav-icon">▣</span><span>Quản lý sản phẩm</span><span class="coming">Sắp có</span></div>
            <div class="nav-link disabled" title="Project hiện chưa có chức năng này"><span class="nav-icon">♙</span><span>Quản lý tài khoản</span><span class="coming">Sắp có</span></div>
        </nav>
        <div class="sidebar-bottom">
            <div class="sidebar-user"><div class="avatar">${sessionScope.fullname.substring(0,1).toUpperCase()}</div><div><strong><c:out value="${sessionScope.fullname}"/></strong><small>@${sessionScope.username}</small></div><span class="user-dot"></span></div>
        </div>
    </aside>

    <main class="main-content">
        <header class="topbar">
            <button class="menu-toggle" id="menuToggle" aria-label="Mở menu">☰</button>
            <div class="breadcrumb">Trang quản trị <span>/</span> <strong>Dashboard</strong></div>
            <div class="top-actions"><span class="today-label" id="todayLabel"></span><a class="logout-link" href="<c:url value='/logout'/>">Đăng xuất</a></div>
        </header>

        <section class="page-content">
            <div class="welcome-row">
                <div><div class="eyebrow"><span class="live-dot"></span> TỔNG QUAN HỆ THỐNG</div><h1>Chào mừng trở lại! <span class="wave">✦</span></h1><p class="page-description">Đây là tình hình tổng quan của cửa hàng hôm nay.</p></div>
                <a class="primary-button" href="<c:url value='/admin/category/add'/>"><span>＋</span> Thêm danh mục</a>
            </div>

            <div class="content-grid">
                <section class="panel recent-panel">
                    <div class="panel-heading"><div><h2>Danh mục gần đây</h2><p>Các danh mục mới nhất theo mã ID</p></div><a class="text-link" href="<c:url value='/admin/category/list'/>">Xem tất cả <span>→</span></a></div>
                    <div class="table-wrap"><table class="recent-table"><thead><tr><th>Danh mục</th><th>STT</th><th>Hình ảnh</th><th>Thao tác</th></tr></thead><tbody>
                    <c:forEach items="${recentCategories}" var="cate" varStatus="status">
                        <tr><td><div class="category-cell"><c:choose><c:when test="${not empty cate.images}"><c:url value="/image?fname=${cate.images}" var="imgUrl"/><img class="category-thumb" src="${imgUrl}" alt="Ảnh danh mục"></c:when><c:otherwise><span class="category-placeholder">▤</span></c:otherwise></c:choose><span><strong><c:out value="${cate.categoryname}"/></strong><small>Danh mục cửa hàng</small></span></div></td><td><span class="id-pill">${status.index + 1}</span></td><td><c:choose><c:when test="${not empty cate.images}"><span class="status status-ready">Đã có ảnh</span></c:when><c:otherwise><span class="status status-pending">Chưa có ảnh</span></c:otherwise></c:choose></td><td><a class="row-action" href="<c:url value='/admin/category/edit?id=${cate.categoryId}'/>">Chỉnh sửa ↗</a></td></tr>
                    </c:forEach>
                    <c:if test="${empty recentCategories}"><tr><td colspan="4" class="empty-state">Chưa có danh mục. Hãy tạo danh mục đầu tiên của bạn.</td></tr></c:if>
                    </tbody></table></div>
                    <div class="panel-footer"><span>Hiển thị tối đa 5 danh mục gần nhất</span><a href="<c:url value='/admin/category/add'/>">＋ Tạo mới</a></div>
                </section>

            </div>
            <footer class="page-footer">© <span id="yearLabel"></span> Shopping Admin <span>•</span> Built for store management</footer>
        </section>
    </main>
</div>
<div class="mobile-overlay" id="mobileOverlay"></div>
<script src="<c:url value='/assets/js/dashboard.js'/>"></script>
</body>
</html>
