const session = requireCustomerPage();
if (!session) {
    throw new Error("No session");
}

async function loadOrders() {
    try {
        const orders = await api(`/api/orders/customer/${session.id}`, { method: "GET" });
        const list = el("orderList");
        list.innerHTML = "";

        orders.forEach((order) => {
            const row = document.createElement("div");
            row.className = "item";
            row.innerHTML = `<strong>Order #${order.id}</strong><br>Status: ${order.status}<br>Slot: ${order.pickupSlot || "-"}<br>Time: ${order.orderTime}`;
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
