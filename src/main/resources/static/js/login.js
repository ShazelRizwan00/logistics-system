const form = document.getElementById("login-form");
const statusEl = document.getElementById("status");

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const formData = new FormData(form);
  const payload = {
    contactInfo: formData.get("contactInfo"),
    password: formData.get("password")
  };

  try {
    const auth = await apiRequest("/api/auth/login", { method: "POST", body: JSON.stringify(payload) });
    saveAuth(auth);
    setStatus(statusEl, "Login successful. Redirecting...", true);
    window.setTimeout(() => window.location.assign("/dashboard.html"), 600);
  } catch (error) {
    setStatus(statusEl, error.message, false);
  }
});
