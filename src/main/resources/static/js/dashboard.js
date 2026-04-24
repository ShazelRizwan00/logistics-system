const welcome = document.getElementById("welcome");
const logoutBtn = document.getElementById("logout");
const refreshBtn = document.getElementById("refresh");
const ordersOutput = document.getElementById("orders-result");
const shipmentsOutput = document.getElementById("shipments-result");
const deliveriesOutput = document.getElementById("deliveries-result");
const orderForm = document.getElementById("order-form");
const orderStatus = document.getElementById("order-status");

function printResult(element, data) {
  element.textContent = JSON.stringify(data, null, 2);
}

function requireAuth() {
  if (!getToken()) window.location.assign("/login.html");
}

async function loadProfile() {
  try {
    const me = await apiRequest("/api/users/me");
    const role = localStorage.getItem(roleKey) || me.role;
    welcome.textContent = `Signed in as ${me.name} (${role})`;
    document.getElementById("customer-create-order").classList.toggle("hidden", role !== "CUSTOMER");
    document.getElementById("agent-deliveries").classList.toggle("hidden", role !== "DELIVERY_AGENT");
  } catch (error) {
    setStatus(orderStatus, `Auth error: ${error.message}`, false);
    clearAuth();
    window.setTimeout(() => window.location.assign("/login.html"), 700);
  }
}

document.getElementById("load-orders").addEventListener("click", async () => {
  const role = localStorage.getItem(roleKey);
  const endpoint = role === "ADMIN" ? "/api/orders" : "/api/orders/my";
  try {
    printResult(ordersOutput, await apiRequest(endpoint));
  } catch (error) {
    printResult(ordersOutput, { error: error.message });
  }
});

document.getElementById("load-shipments").addEventListener("click", async () => {
  const role = localStorage.getItem(roleKey);
  const endpoint = role === "ADMIN" ? "/api/shipments" : "/api/shipments/my";
  try {
    printResult(shipmentsOutput, await apiRequest(endpoint));
  } catch (error) {
    printResult(shipmentsOutput, { error: error.message });
  }
});

document.getElementById("load-deliveries").addEventListener("click", async () => {
  try {
    printResult(deliveriesOutput, await apiRequest("/api/deliveries/my"));
  } catch (error) {
    printResult(deliveriesOutput, { error: error.message });
  }
});

orderForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const data = new FormData(orderForm);
  const payload = {
    payment: { amount: Number(data.get("amount")), method: data.get("method") },
    shipments: [{
      origin: data.get("origin"),
      destination: data.get("destination"),
      packages: [{
        weight: Number(data.get("weight")),
        dimensions: data.get("dimensions"),
        description: data.get("description")
      }]
    }]
  };

  try {
    const created = await apiRequest("/api/orders", { method: "POST", body: JSON.stringify(payload) });
    setStatus(orderStatus, "Order placed successfully", true);
    printResult(ordersOutput, created);
    orderForm.reset();
  } catch (error) {
    setStatus(orderStatus, error.message, false);
  }
});

logoutBtn.addEventListener("click", () => {
  clearAuth();
  window.location.assign("/login.html");
});
refreshBtn.addEventListener("click", loadProfile);

requireAuth();
loadProfile();
