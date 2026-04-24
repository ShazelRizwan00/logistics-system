const tokenKey = "logistics_token";
const roleKey = "logistics_role";
const nameKey = "logistics_name";

function getToken() { return localStorage.getItem(tokenKey); }
function saveAuth(authData) {
  localStorage.setItem(tokenKey, authData.token);
  localStorage.setItem(roleKey, authData.role);
  localStorage.setItem(nameKey, authData.name || "User");
}
function clearAuth() {
  localStorage.removeItem(tokenKey);
  localStorage.removeItem(roleKey);
  localStorage.removeItem(nameKey);
}

async function apiRequest(path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(path, { ...options, headers });
  const body = await response.json().catch(() => ({}));

  if (!response.ok || body.success === false) {
    throw new Error(body.message || `Request failed with status ${response.status}`);
  }

  return body.data;
}

function setStatus(element, message, ok = true) {
  element.textContent = message;
  element.classList.remove("error", "success");
  element.classList.add(ok ? "success" : "error");
}
