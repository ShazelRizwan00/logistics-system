const registerForm = document.getElementById("register-form");
const registerStatus = document.getElementById("status");
const roleSelect = document.getElementById("role");
const addressField = document.getElementById("address-field");
const vehicleField = document.getElementById("vehicle-field");

function syncRoleFields() {
  const role = roleSelect.value;
  addressField.classList.toggle("hidden", role !== "CUSTOMER");
  vehicleField.classList.toggle("hidden", role !== "DELIVERY_AGENT");
}

roleSelect.addEventListener("change", syncRoleFields);
syncRoleFields();

registerForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const formData = new FormData(registerForm);
  const payload = {
    name: formData.get("name"),
    contactInfo: formData.get("contactInfo"),
    password: formData.get("password"),
    role: formData.get("role"),
    address: formData.get("address") || null,
    vehicleInfo: formData.get("vehicleInfo") || null
  };

  try {
    const auth = await apiRequest("/api/auth/register", { method: "POST", body: JSON.stringify(payload) });
    saveAuth(auth);
    setStatus(registerStatus, "Registration successful. Redirecting...", true);
    window.setTimeout(() => window.location.assign("/dashboard.html"), 600);
  } catch (error) {
    setStatus(registerStatus, error.message, false);
  }
});
