const session = requireCustomerPage();
if (!session) {
    throw new Error("No session");
}

const cartList = el("cartList");
const slotText = el("slotText");
const totalText = el("totalText");

function renderCart() {
    const cart = getCart();
    cartList.innerHTML = "";

    let total = 0;
    cart.forEach((item, idx) => {
        total += Number(item.price);
        const row = document.createElement("div");
        row.className = "item";
        row.innerHTML = `<strong>${item.name}</strong> - Rs ${item.price}`;

        const removeBtn = document.createElement("button");
        removeBtn.className = "ghost";
        removeBtn.textContent = "Remove";
        removeBtn.addEventListener("click", () => {
            const next = getCart();
            next.splice(idx, 1);
            setCart(next);
            renderCart();
        });
        row.appendChild(document.createElement("br"));
        row.appendChild(removeBtn);
        cartList.appendChild(row);
    });

    slotText.textContent = getSlot() || "Not selected";
    totalText.textContent = total.toFixed(2);

    if (!cart.length) {
        cartList.innerHTML = '<div class="item">Your cart is empty.</div>';
    }
}

el("clearCartBtn").addEventListener("click", () => {
    setCart([]);
    renderCart();
    showToast("Cart cleared");
});

el("placeOrderBtn").addEventListener("click", async () => {
    const cart = getCart();
    const slot = getSlot();

    if (!cart.length) {
        showToast("Cart is empty");
        return;
    }

    if (!slot) {
        showToast("Select a slot from Menu page first");
        return;
    }

    const payload = {
        customerId: session.id,
        menuItemIds: cart.map((c) => c.id),
        pickupSlot: slot
    };

    try {
        const order = await api("/api/orders", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        setCart([]);
        showToast(`Order #${order.id} placed`);
        renderCart();
    } catch (err) {
        showToast(err.message);
    }
});

renderCart();
