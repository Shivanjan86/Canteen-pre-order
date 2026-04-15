const staffSession = requireRolePage("STAFF");
if (!staffSession) {
    throw new Error("No staff session");
}

el("statusForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target).entries());

    try {
        await api(`/api/orders/${Number(data.orderId)}/status?status=${encodeURIComponent(data.status)}`, {
            method: "PUT"
        });
        showToast("Order status updated");
        e.target.reset();
    } catch (err) {
        showToast(err.message);
    }
});

el("fetchOrdersForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target).entries());

    try {
        const orders = await api(`/api/orders/customer/${Number(data.customerId)}`, { method: "GET" });
        const list = el("orderList");
        list.innerHTML = "";

        orders.forEach((order) => {
            const row = document.createElement("div");
            row.className = "item";
            row.innerHTML = `<strong>Order #${order.id}</strong><br>Status: ${order.status}<br>Slot: ${order.pickupSlot || "-"}<br>Time: ${order.orderTime}`;
            list.appendChild(row);
        });

        if (!orders.length) {
            list.innerHTML = '<div class="item">No orders found for this customer.</div>';
        }
    } catch (err) {
        showToast(err.message);
    }
});
