const session = requireCustomerPage();
if (!session) {
    throw new Error("No session");
}

async function loadOrders() {
    try {
        const orders = await api("/api/orders/my", { method: "GET" });
        const list = el("orderList");
        list.innerHTML = "";

        orders.forEach((order) => {
            const row = document.createElement("div");
            row.className = "item";
            const statusNote = order.status === "READY"
                ? "Ready for pickup. You can collect it anytime during your slot."
                : order.status === "PICKED_UP"
                    ? "Collected by customer."
                    : order.status === "EXPIRED"
                        ? "Pickup window missed. Please place a new order."
                    : "";

            row.innerHTML = `<strong>Order #${order.id}</strong><br>Status: ${order.status}<br>${statusNote ? `Note: ${statusNote}<br>` : ""}Slot: ${order.pickupSlot || "-"}<br>Time: ${order.orderTime}`;

            if (order.status === "PLACED") {
                const cancelBtn = document.createElement("button");
                cancelBtn.textContent = "Cancel Order";
                cancelBtn.addEventListener("click", async () => {
                    try {
                        await api(`/api/orders/${order.id}/cancel`, {
                            method: "PUT"
                        });
                        showToast(`Order #${order.id} cancelled`);
                        loadOrders();
                    } catch (err) {
                        showToast(err.message);
                    }
                });
                row.appendChild(document.createElement("br"));
                row.appendChild(cancelBtn);
            }

            if (order.status === "READY") {
                const receivedBtn = document.createElement("button");
                receivedBtn.textContent = "Mark as Received";
                receivedBtn.addEventListener("click", async () => {
                    try {
                        await api(`/api/orders/${order.id}/receive`, {
                            method: "PUT"
                        });
                        showToast(`Order #${order.id} marked as received`);
                        loadOrders();
                    } catch (err) {
                        showToast(err.message);
                    }
                });
                row.appendChild(document.createElement("br"));
                row.appendChild(receivedBtn);
            }

            list.appendChild(row);
        });

        if (!orders.length) {
            list.innerHTML = '<div class="item">No orders yet.</div>';
        }
    } catch (err) {
        showToast(err.message);
    }
}

async function loadNotifications() {
    try {
        const notes = await api(`/api/notifications?userId=${session.id}`, { method: "GET" });
        const list = el("notificationList");
        list.innerHTML = "";

        notes.forEach((n) => {
            const row = document.createElement("div");
            row.className = "item";
            row.innerHTML = `${n.readStatus ? "[READ]" : "[NEW]"} ${n.message}`;

            if (!n.readStatus) {
                const btn = document.createElement("button");
                btn.textContent = "Mark Read";
                btn.addEventListener("click", async () => {
                    try {
                        await api(`/api/notifications/${n.id}/read?userId=${session.id}`, { method: "PUT" });
                        loadNotifications();
                    } catch (err) {
                        showToast(err.message);
                    }
                });
                row.appendChild(document.createElement("br"));
                row.appendChild(btn);
            }

            list.appendChild(row);
        });

        if (!notes.length) {
            list.innerHTML = '<div class="item">No notifications.</div>';
        }
    } catch (err) {
        showToast(err.message);
    }
}

el("loadOrdersBtn").addEventListener("click", loadOrders);
el("loadNotificationsBtn").addEventListener("click", loadNotifications);

loadOrders();
loadNotifications();
