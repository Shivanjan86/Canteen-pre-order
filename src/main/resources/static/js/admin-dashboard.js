const adminSession = requireRolePage("ADMIN");
if (!adminSession) {
    throw new Error("No admin session");
}

el("menuForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target).entries());
    data.price = Number(data.price);

    try {
        await api("/api/menu", {
            method: "POST",
            body: JSON.stringify(data)
        });
        showToast("Menu item added");
        e.target.reset();
    } catch (err) {
        showToast(err.message);
    }
});

el("loadReportsBtn").addEventListener("click", async () => {
    try {
        const report = await api("/api/reports/summary", { method: "GET" });
        el("reportBox").textContent = JSON.stringify(report, null, 2);
    } catch (err) {
        showToast(err.message);
    }
});
