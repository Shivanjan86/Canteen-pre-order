const CART_KEY = "canteenCart";
const SLOT_KEY = "canteenSlot";
const SESSION_KEY = "canteenSession";
const SESSION_TTL_MS = 1000 * 60 * 60 * 24 * 30;

// Auth session must be per-tab so different roles can stay logged in across tabs.
localStorage.removeItem(SESSION_KEY);

const el = (id) => document.getElementById(id);

function showToast(message) {
    const toast = el("toast");
    if (!toast) return;
    toast.textContent = message;
    toast.classList.add("show");
    setTimeout(() => toast.classList.remove("show"), 1800);
}

function saveSession(session) {
    const now = Date.now();
    sessionStorage.setItem(SESSION_KEY, JSON.stringify({
        ...session,
        _issuedAt: now,
        _expiresAt: now + SESSION_TTL_MS
    }));
}

function isSessionExpired(session) {
    if (!session || !session._expiresAt) return false;
    return Date.now() > Number(session._expiresAt);
}

function refreshSession(session) {
    if (!session) return null;
    saveSession(session);
    return getSession();
}

function getSession() {
    const raw = sessionStorage.getItem(SESSION_KEY);
    if (!raw) return null;

    try {
        const parsed = JSON.parse(raw);
        if (isSessionExpired(parsed)) {
            clearSession();
            return null;
        }

        if (!parsed._expiresAt) {
            return refreshSession(parsed);
        }

        return parsed;
    } catch (e) {
        clearSession();
        return null;
    }
}

function clearSession() {
    sessionStorage.removeItem(SESSION_KEY);
    localStorage.removeItem(SESSION_KEY);
}

function authHeader() {
    const s = getSession();
    if (!s) return null;
    refreshSession(s);
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
        if (response.status === 401) {
            clearSession();
            if (!window.location.pathname.endsWith("/index.html")) {
                window.location.href = "/index.html";
            }
        }
        throw new Error(typeof body === "string" ? body : JSON.stringify(body));
    }

    const currentSession = getSession();
    if (currentSession) {
        refreshSession(currentSession);
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
