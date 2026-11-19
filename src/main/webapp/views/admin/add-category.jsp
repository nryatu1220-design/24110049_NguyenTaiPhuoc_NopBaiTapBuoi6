<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Thêm danh mục</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/dashboard.css'/>">
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
            <div class="breadcrumb">Danh mục <span>/</span> <strong>Thêm mới</strong></div>
            <div class="top-actions">
                <span class="today-label" id="todayLabel"></span>
                <div class="top-avatar">${sessionScope.fullname.substring(0,1).toUpperCase()}</div>
            </div>
        </header>

        <section class="page-content form-shell">
            <div class="page-heading">
                <div>
                    <div class="eyebrow"><span class="live-dot"></span> DANH MỤC MỚI</div>
                    <h1>Thêm danh mục</h1>
                    <p class="page-description">Điền thông tin bên dưới để tạo danh mục mới cho cửa hàng.</p>
                </div>
            </div>

            <section class="panel form-card">
                <h2>Thông tin danh mục</h2>
                <p>Các trường có dấu * cần được hoàn thành.</p>

                <c:if test="${not empty error}">
                    <div class="alert-error"><c:out value="${error}"/></div>
                </c:if>

                <c:url value="/admin/category/add" var="addUrl"/>
                <form action="${addUrl}" method="post" enctype="multipart/form-data">
                    <div class="form-group">
                        <label class="form-label" for="catename">Tên danh mục *</label>
                        <input id="catename" class="field-input" placeholder="Ví dụ: Điện thoại, Thời trang..." name="catename" required maxlength="255" value="<c:out value='${category.categoryname}'/>">
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="icon">Ảnh đại diện</label>
                        <input id="icon" class="file-input" type="file" name="icon" accept=".jpg,.jpeg,.png,.webp">
                        <span class="file-hint">Không bắt buộc. Chọn ảnh định dạng JPG, PNG hoặc WEBP.</span>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Trạng thái</label>
                        <label><input type="radio" name="status" value="1" ${empty category or category.status != 0 ? 'checked' : ''}> Hoạt động</label>
                        &nbsp;&nbsp;
                        <label><input type="radio" name="status" value="0" ${not empty category and category.status == 0 ? 'checked' : ''}> Khóa</label>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="button button-primary">Lưu danh mục</button>
                        <button type="reset" class="button button-secondary">Nhập lại</button>
                        <a href="<c:url value='/admin/category/list'/>" class="back-link">← Quay lại danh sách</a>
                    </div>
                </form>
            </section>
        </section>
    </main>
</div>

<div class="mobile-overlay" id="mobileOverlay"></div>
<script src="<c:url value='/assets/js/dashboard.js'/>"></script>
</body>
</html>