import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor, act, fireEvent } from "@testing-library/react";

import { AuthProvider, useAuth } from "../context/AuthContext";


vi.mock("../services/authService", () => ({
  getCurrentUser: vi.fn(),
  loginUser: vi.fn(),
  logoutUser: vi.fn(),
}));

import {
  getCurrentUser,
  loginUser,
  logoutUser,
} from "../services/authService";


const TestConsumer = () => {
  const { user, isAuthenticated, loading, login, logout } = useAuth();

  return (
    <div>
      <div data-testid="loading">{String(loading)}</div>
      <div data-testid="isAuthenticated">{String(isAuthenticated)}</div>
      <div data-testid="user">{user ? user.email : "null"}</div>

      <button
        data-testid="login-btn"
        onClick={() => login({ email: "test@example.com", password: "1234" })}
      />

      <button data-testid="logout-btn" onClick={() => logout()} />
    </div>
  );
};

describe("AuthProvider + useAuth", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("initially loads current user and sets loading=false afterwards", async () => {
    (getCurrentUser as vi.Mock).mockResolvedValue({
      id: 1,
      email: "existing@example.com",
    });

    render(
      <AuthProvider>
        <TestConsumer />
      </AuthProvider>
    );


    expect(screen.getByTestId("loading").textContent).toBe("true");

    await waitFor(() => {
      expect(screen.getByTestId("loading").textContent).toBe("false");
    });


    expect(screen.getByTestId("user").textContent).toBe("existing@example.com");
    expect(screen.getByTestId("isAuthenticated").textContent).toBe("true");
  });

  it("sets user=null if getCurrentUser throws", async () => {
    (getCurrentUser as vi.Mock).mockRejectedValue(new Error("Not logged in"));

    render(
      <AuthProvider>
        <TestConsumer />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId("loading").textContent).toBe("false");
    });

    expect(screen.getByTestId("user").textContent).toBe("null");
    expect(screen.getByTestId("isAuthenticated").textContent).toBe("false");
  });

  it("login() updates user and isAuthenticated", async () => {
    (getCurrentUser as vi.Mock).mockResolvedValue(null);
    (loginUser as vi.Mock).mockResolvedValue({
      id: 10,
      email: "logged@example.com",
    });

    render(
      <AuthProvider>
        <TestConsumer />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId("loading").textContent).toBe("false");
    });


    fireEvent.click(screen.getByTestId("login-btn"));

    await waitFor(() => {
      expect(screen.getByTestId("user").textContent).toBe("logged@example.com");
      expect(screen.getByTestId("isAuthenticated").textContent).toBe("true");
    });
  });

  it("logout() clears the user", async () => {
    (getCurrentUser as vi.Mock).mockResolvedValue({
      id: 1,
      email: "old@example.com",
    });

    (logoutUser as vi.Mock).mockResolvedValue(undefined);

    render(
      <AuthProvider>
        <TestConsumer />
      </AuthProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId("loading").textContent).toBe("false");
    });

    expect(screen.getByTestId("user").textContent).toBe("old@example.com");

    fireEvent.click(screen.getByTestId("logout-btn"));

    await waitFor(() => {
      expect(screen.getByTestId("user").textContent).toBe("null");
      expect(screen.getByTestId("isAuthenticated").textContent).toBe("false");
    });
  });

  it("throws if useAuth is used outside AuthProvider", () => {

    const renderOutsideProvider = () =>
      render(<TestConsumer />);

    expect(renderOutsideProvider).toThrowError(
      "useAuth must be used within AuthProvider"
    );
  });
});
