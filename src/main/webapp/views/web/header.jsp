<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<header class="site-header">
    <div class="container nav">
        <a class="brand" href="<c:url value='/home'/>"><span class="brand-mark">S</span>Shopping</a>
        <nav>
            <a class="${param.active == 'home' ? 'active' : ''}" href="<c:url value='/home'/>">Trang chủ</a>
            <c:if test="${not empty sessionScope.username}">
                <a class="${param.active == 'video' ? 'active' : ''}" href="<c:url value='/video'/>">Video của tôi</a>
                <a href="<c:url value='/admin/dashboard'/>">Quản trị</a>
            </c:if>
        </nav>
        <div class="nav-right">
            <c:choose>
                <c:when test="${not empty sessionScope.username}">
                    <span class="hello">Xin chào, <strong><c:out value="${sessionScope.fullname}"/></strong></span>
                    <a class="btn btn-ghost btn-sm" href="<c:url value='/logout'/>">Đăng xuất</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-ghost btn-sm" href="<c:url value='/login'/>">Đăng nhập</a>
                    <a class="btn btn-sm" href="<c:url value='/register'/>">Đăng ký</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>
