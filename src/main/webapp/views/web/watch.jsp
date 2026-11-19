<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:choose><c:when test="${not empty video}"><c:out value="${video.title}"/></c:when><c:otherwise>Video</c:otherwise></c:choose> | Shopping</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/site.css'/>">
</head>
<body>
<jsp:include page="/views/web/header.jsp"/>
<main class="container page">
    <div class="watch">
        <c:choose>
            <c:when test="${not empty error}">
                <div class="alert alert-err"><c:out value="${error}"/></div>
                <a class="btn btn-ghost" href="<c:url value='/home'/>">← Về trang chủ</a>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${not empty video.videoFile}">
                        <video class="player" controls autoplay preload="metadata"
                               <c:if test="${not empty video.poster}">poster="<c:url value='/media?fname=${video.poster}'/>"</c:if>
                               src="<c:url value='/media?fname=${video.videoFile}'/>">
                            Trình duyệt của bạn không hỗ trợ thẻ video.
                        </video>
                    </c:when>
                    <c:otherwise><div class="empty">Video này chưa có file để phát.</div></c:otherwise>
                </c:choose>

                <h1><c:out value="${video.title}"/></h1>
                <div class="vmeta">
                    <c:if test="${not empty video.user}">Đăng bởi <strong><c:out value="${video.user.fullname}"/></strong> • </c:if>
                    ${video.views} lượt xem<c:if test="${not empty video.createdDateText}"> • ${video.createdDateText}</c:if>
                    <c:if test="${not video.active}"> • <span class="tag tag-off">Đang ẩn</span></c:if>
                </div>
                <c:if test="${not empty video.description}"><div class="desc"><c:out value="${video.description}"/></div></c:if>

                <p style="margin-top:20px">
                    <a class="btn btn-ghost" href="<c:url value='/home'/>">← Trang chủ</a>
                    <c:if test="${owner}">
                        <a class="btn btn-ghost" href="<c:url value='/video'/>">Video của tôi</a>
                        <a class="btn" href="<c:url value='/video/edit?id=${video.videoId}'/>">Sửa video</a>
                    </c:if>
                </p>
            </c:otherwise>
        </c:choose>
    </div>
</main>
<footer class="site-footer">© Shopping</footer>
</body>
</html>
