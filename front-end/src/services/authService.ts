import { RegisterRequest, LoginRequest, UserResponse } from "../types/auth";

// talk directly to backend
const API_BASE = "http://localhost:8080/api/auth";

export async function registerUser(
  data: RegisterRequest
): Promise<UserResponse> {
  const response = await fetch(`${API_BASE}/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
    credentials: "include",
  });

  if (!response.ok) {
    let message = "Registration failed";

    try {
      const errorBody = await response.json();
      if (errorBody && typeof errorBody.message === "string") {
        message = errorBody.message;
      }
    } catch {}

    throw new Error(message);
  }

  return response.json();
}

export async function loginUser(data: LoginRequest): Promise<UserResponse> {
  const response = await fetch(`${API_BASE}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
    credentials: "include",
  });

  if (!response.ok) {
    let message = "Login failed";
    try {
      const errorBody = await response.json();
      if (errorBody && typeof errorBody.message === "string") {
        message = errorBody.message;
      }
    } catch {}
    throw new Error(message);
  }

  return response.json();
}

export async function getCurrentUser(): Promise<UserResponse | null> {
  const response = await fetch(`${API_BASE}/me`, {
    method: "GET",
    credentials: "include",
  });

  // 🔥 treat both 401 and 403 as "not logged in"
  if (response.status === 401 || response.status === 403) {
    return null;
  }

  if (!response.ok) {
    throw new Error("Failed to fetch current user");
  }

  return response.json();
}

export async function logoutUser(): Promise<void> {
  const response = await fetch(`${API_BASE}/logout`, {
    method: "POST",
    credentials: "include",
  });

  if (!response.ok && response.status !== 204) {
    throw new Error("Failed to log out");
  }
}
