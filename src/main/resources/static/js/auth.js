const loginForm = el("loginForm");
const registerForm = el("registerForm");

const authTabs = document.querySelectorAll(".auth-tab");
const authPanels = document.querySelectorAll(".auth-panel");
const authLinks = document.querySelectorAll("[data-switch-target]");

function showAuthPanel(targetId) {
    authTabs.forEach((tab) => {
        const isActive = tab.dataset.target === targetId;
        tab.classList.toggle("active", isActive);
        tab.setAttribute("aria-selected", String(isActive));
    });

    authPanels.forEach((panel) => {
        panel.classList.toggle("active", panel.id === targetId);
    });
}

authTabs.forEach((tab) => {
    tab.addEventListener("click", () => showAuthPanel(tab.dataset.target));
});

authLinks.forEach((link) => {
    link.addEventListener("click", (event) => {
        event.preventDefault();
        showAuthPanel(link.dataset.switchTarget);
    });
});

showAuthPanel("login-panel");

loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(loginForm).entries());

    try {
        const user = await api(`/api/auth/login?email=${encodeURIComponent(data.email)}&password=${encodeURIComponent(data.password)}`, {
            method: "POST"
        });

        saveSession({ ...user, password: data.password });

        if (user.role === "CUSTOMER") {
            window.location.href = "/customer/menu.html";
            return;
        }

        if (user.role === "STAFF") {
            window.location.href = "/staff/dashboard.html";
            return;
        }

        if (user.role === "ADMIN") {
            window.location.href = "/admin/dashboard.html";
            return;
        }

        showToast(`Unknown role: ${user.role}`);
    } catch (err) {
        showToast(err.message);
    }
});

registerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(registerForm).entries());

    try {
        await api("/api/auth/register", {
            method: "POST",
            body: JSON.stringify(data)
        });
        showToast("Registration successful");
        registerForm.reset();
    } catch (err) {
        showToast(err.message);
    }
});
