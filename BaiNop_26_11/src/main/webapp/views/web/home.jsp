<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Trang chủ | Shopping</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/site.css'/>">
</head>
<body>
<jsp:include page="/views/web/header.jsp"><jsp:param name="active" value="home"/></jsp:include>
<main class="container page">
    <div class="page-head">
        <div>
            <h1>Video mới nhất</h1>
            <p>10 video mới được đăng gần đây nhất.</p>
        </div>
        <c:if test="${not empty sessionScope.username}">
            <a class="btn" href="<c:url value='/video/add'/>">＋ Đăng video</a>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${empty videos}">
            <div class="empty">Chưa có video nào.</div>
        </c:when>
        <c:otherwise>
            <div class="grid">
                <c:forEach items="${videos}" var="v">
                    <article class="vcard">
                        <a class="thumb" href="<c:url value='/watch?id=${v.videoId}'/>">
                            <c:if test="${not empty v.poster}"><img src="<c:url value='/media?fname=${v.poster}'/>" alt="" loading="lazy"></c:if>
                            <span class="play">▶</span>
                        </a>
                        <div class="vbody">
                            <a class="vtitle" href="<c:url value='/watch?id=${v.videoId}'/>"><c:out value="${v.title}"/></a>
                            <div class="vmeta">
                                <c:if test="${not empty v.user}"><c:out value="${v.user.fullname}"/> • </c:if>
                                ${v.views} lượt xem<c:if test="${not empty v.createdDateText}"> • ${v.createdDateText}</c:if>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>
<footer class="site-footer">© Shopping</footer>
</body>
</html>
