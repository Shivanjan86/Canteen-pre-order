const session = requireCustomerPage();
if (!session) {
    throw new Error("No session");
}

const menuList = el("menuList");
const slotSelect = el("slotSelect");

async function loadSlots() {
    try {
        const slots = await api("/api/orders/slots", { method: "GET" });
        slotSelect.innerHTML = "";

        slots.forEach((slot) => {
            const option = document.createElement("option");
            option.value = slot;
            option.textContent = slot;
            slotSelect.appendChild(option);
        });

        const savedSlot = getSlot();
        if (savedSlot && slots.includes(savedSlot)) {
            slotSelect.value = savedSlot;
        }

        setSlot(slotSelect.value);
    } catch (err) {
        showToast(err.message);
    }
}

slotSelect.addEventListener("change", () => setSlot(slotSelect.value));

async function loadMenu() {
    try {
        const items = await api("/api/menu", { method: "GET" });
        menuList.innerHTML = "";

        items.forEach((item) => {
            const row = document.createElement("div");
            row.className = "item";
            row.innerHTML = `<strong>#${item.id} ${item.name}</strong><br>Rs ${item.price} - ${item.description}`;

            const addBtn = document.createElement("button");
            addBtn.textContent = "Add to Cart";
            addBtn.addEventListener("click", () => {
                const cart = getCart();
                cart.push(item);
                setCart(cart);
                showToast(`${item.name} added to cart`);
            });

            row.appendChild(document.createElement("br"));
            row.appendChild(addBtn);
            menuList.appendChild(row);
        });

        if (!items.length) {
            menuList.innerHTML = '<div class="item">No menu items available.</div>';
        }
    } catch (err) {
        showToast(err.message);
    }
}

el("refreshMenu").addEventListener("click", loadMenu);

loadSlots();
loadMenu();
