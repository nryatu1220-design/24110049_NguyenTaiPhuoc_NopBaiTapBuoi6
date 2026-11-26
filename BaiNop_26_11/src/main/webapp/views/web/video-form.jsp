<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${editMode ? 'Sửa video' : 'Thêm video'} | Shopping</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/site.css'/>">
</head>
<body>
<jsp:include page="/views/web/header.jsp"><jsp:param name="active" value="video"/></jsp:include>
<main class="container page">
    <div class="page-head">
        <div><h1>${editMode ? 'Sửa video' : 'Thêm video'}</h1></div>
    </div>

    <c:if test="${not empty error}"><div class="alert alert-err"><c:out value="${error}"/></div></c:if>

    <%-- enctype multipart/form-data bắt buộc để upload file --%>
    <form class="panel" method="post" enctype="multipart/form-data"
          action="<c:url value='${editMode ? "/video/edit" : "/video/add"}'/>">
        <c:if test="${editMode}"><input type="hidden" name="id" value="<c:out value='${video.videoId}'/>"></c:if>

        <label for="title">Tiêu đề *</label>
        <input id="title" type="text" name="title" maxlength="255" required autofocus value="<c:out value='${video.title}'/>">

        <label for="description">Mô tả</label>
        <textarea id="description" name="description"><c:out value="${video.description}"/></textarea>

        <label for="poster">Ảnh poster</label>
        <c:if test="${not empty video.poster}">
            <div class="current"><img src="<c:url value='/media?fname=${video.poster}'/>" alt="Poster hiện tại"><span class="hint">Poster hiện tại. Chọn file mới để thay thế.</span></div>
        </c:if>
        <input id="poster" type="file" name="poster" accept="image/*">
        <div class="hint">jpg, jpeg, png, gif, webp</div>

        <label for="videoFile">File video <c:if test="${not editMode}">*</c:if></label>
        <c:if test="${not empty video.videoFile}">
            <div class="hint" style="margin-bottom:6px">Đã có video: <c:out value="${video.videoFile}"/>. Chọn file mới để thay thế.</div>
        </c:if>
        <input id="videoFile" type="file" name="videoFile" accept="video/*" <c:if test="${not editMode}">required</c:if>>
        <div class="hint">mp4, webm, ogg, mov • tối đa 200MB</div>

        <div class="check">
            <input id="active" type="checkbox" name="active" <c:if test="${empty video or video.active}">checked</c:if>>
            <label for="active">Hiển thị công khai trên trang chủ</label>
        </div>

        <div class="form-actions">
            <button class="btn" type="submit">${editMode ? 'Lưu thay đổi' : 'Thêm video'}</button>
            <a class="btn btn-ghost" href="<c:url value='/video'/>">Huỷ</a>
        </div>
    </form>
</main>
<footer class="site-footer">© Shopping</footer>
</body>
</html>
