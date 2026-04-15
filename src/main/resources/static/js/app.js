const CART_KEY = "canteenCart";
const SLOT_KEY = "canteenSlot";
const SESSION_KEY = "canteenSession";

const el = (id) => document.getElementById(id);

function showToast(message) {
    const toast = el("toast");
    if (!toast) return;
    toast.textContent = message;
    toast.classList.add("show");
    setTimeout(() => toast.classList.remove("show"), 1800);
}

function saveSession(session) {
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

function getSession() {
    const raw = localStorage.getItem(SESSION_KEY);
    return raw ? JSON.parse(raw) : null;
}

function clearSession() {
    localStorage.removeItem(SESSION_KEY);
}

function authHeader() {
    const s = getSession();
    if (!s) return null;
    return "Basic " + btoa(`${s.email}:${s.password}`);
}

async function api(path, options = {}) {
    const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
    const auth = authHeader();
    if (auth) headers.Authorization = auth;

    const response = await fetch(path, { ...options, headers });
    const text = await response.text();
    let body = null;

    if (text) {
        try { body = JSON.parse(text); } catch (e) { body = text; }
    }

    if (!response.ok) {
        throw new Error(typeof body === "string" ? body : JSON.stringify(body));
    }

    return body;
}

function requireCustomerPage() {
    return requireRolePage("CUSTOMER");
}

function requireRolePage(role) {
    const session = getSession();
    if (!session) {
        window.location.href = "/index.html";
        return null;
    }

    if (session.role !== role) {
        showToast(`This page is only for ${role}`);
        setTimeout(() => (window.location.href = "/index.html"), 500);
        return null;
    }

    const sessionText = el("sessionText");
    if (sessionText) sessionText.textContent = `${session.name} (${session.email})`;
    const logoutBtn = el("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            clearSession();
            window.location.href = "/index.html";
        });
    }
    return session;
}

function getCart() {
    const raw = localStorage.getItem(CART_KEY);
    return raw ? JSON.parse(raw) : [];
}

function setCart(cart) {
    localStorage.setItem(CART_KEY, JSON.stringify(cart));
}

function getSlot() {
    return localStorage.getItem(SLOT_KEY) || "";
}

function setSlot(slot) {
    localStorage.setItem(SLOT_KEY, slot);
}
