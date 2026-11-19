document.addEventListener("DOMContentLoaded", function () {
    const dateLabel = document.getElementById("todayLabel");
    const yearLabel = document.getElementById("yearLabel");
    const now = new Date();
    if (dateLabel) dateLabel.textContent = now.toLocaleDateString("vi-VN", {
        weekday: "long", day: "2-digit", month: "2-digit", year: "numeric"
    });
    if (yearLabel) yearLabel.textContent = now.getFullYear();

    const sidebar = document.getElementById("sidebar");
    const toggle = document.getElementById("menuToggle");
    const overlay = document.getElementById("mobileOverlay");
    function closeMenu() {
        if (sidebar) sidebar.classList.remove("open");
        if (overlay) overlay.classList.remove("show");
    }
    if (toggle && sidebar && overlay) {
        toggle.addEventListener("click", function () {
            sidebar.classList.toggle("open");
            overlay.classList.toggle("show");
        });
        overlay.addEventListener("click", closeMenu);
    }

    const topActions = document.querySelector(".top-actions");
    if (topActions && !topActions.querySelector(".logout-link")) {
        const adminPathIndex = window.location.pathname.indexOf("/admin/");
        const contextPath = adminPathIndex >= 0
            ? window.location.pathname.substring(0, adminPathIndex) : "";
        const logoutLink = document.createElement("a");
        logoutLink.className = "logout-link";
        logoutLink.href = contextPath + "/logout";
        logoutLink.textContent = "Đăng xuất";
        topActions.appendChild(logoutLink);
    }
});
