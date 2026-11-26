<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Video của tôi | Shopping</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/site.css'/>">
</head>
<body>
<jsp:include page="/views/web/header.jsp"><jsp:param name="active" value="video"/></jsp:include>
<main class="container page">
    <div class="page-head">
        <div>
            <h1>Video của tôi</h1>
            <p>${total} video • Trang ${page}/${totalPages}</p>
        </div>
        <a class="btn" href="<c:url value='/video/add'/>">＋ Thêm video</a>
    </div>

    <c:if test="${not empty success}"><div class="alert alert-ok"><c:out value="${success}"/></div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-err"><c:out value="${error}"/></div></c:if>

    <c:choose>
        <c:when test="${empty videos}">
            <div class="empty">Bạn chưa có video nào. <a href="<c:url value='/video/add'/>">Thêm video đầu tiên</a>.</div>
        </c:when>
        <c:otherwise>
            <div class="grid grid-3">
                <c:forEach items="${videos}" var="v">
                    <article class="vcard">
                        <a class="thumb" href="<c:url value='/watch?id=${v.videoId}'/>">
                            <c:if test="${not empty v.poster}"><img src="<c:url value='/media?fname=${v.poster}'/>" alt="" loading="lazy"></c:if>
                            <span class="play">▶</span>
                        </a>
                        <div class="vbody">
                            <a class="vtitle" href="<c:url value='/watch?id=${v.videoId}'/>"><c:out value="${v.title}"/></a>
                            <div class="vmeta">
                                <c:choose>
                                    <c:when test="${v.active}"><span class="tag tag-on">Hiển thị</span></c:when>
                                    <c:otherwise><span class="tag tag-off">Đang ẩn</span></c:otherwise>
                                </c:choose>
                                ${v.views} lượt xem<c:if test="${not empty v.createdDateText}"> • ${v.createdDateText}</c:if>
                            </div>
                            <div class="vactions">
                                <a class="btn btn-ghost btn-sm" href="<c:url value='/video/edit?id=${v.videoId}'/>">Sửa</a>
                                <form action="<c:url value='/video/delete'/>" method="post" onsubmit="return confirm('Xoá video này?');">
                                    <input type="hidden" name="id" value="${v.videoId}">
                                    <button class="btn btn-danger btn-sm" type="submit">Xoá</button>
                                </form>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </div>

            <c:if test="${totalPages > 1}">
                <nav class="pager" aria-label="Phân trang">
                    <c:choose>
                        <c:when test="${page > 1}"><a href="<c:url value='/video?page=${page - 1}'/>">«</a></c:when>
                        <c:otherwise><span class="dis">«</span></c:otherwise>
                    </c:choose>
                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:choose>
                            <c:when test="${p == page}"><span class="cur">${p}</span></c:when>
                            <c:otherwise><a href="<c:url value='/video?page=${p}'/>">${p}</a></c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:choose>
                        <c:when test="${page < totalPages}"><a href="<c:url value='/video?page=${page + 1}'/>">»</a></c:when>
                        <c:otherwise><span class="dis">»</span></c:otherwise>
                    </c:choose>
                </nav>
            </c:if>
        </c:otherwise>
    </c:choose>
</main>
<footer class="site-footer">© Shopping</footer>
</body>
</html>
