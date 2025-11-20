import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import App from "../App";

vi.mock("../pages/Home", () => ({
  __esModule: true,
  default: () => <div>Home Page</div>,
}));

vi.mock("../pages/Register", () => ({
  __esModule: true,
  default: () => <div>Register Page</div>,
}));

vi.mock("../pages/Login", () => ({
  __esModule: true,
  default: () => <div>Login Page</div>,
}));

vi.mock("../pages/Listings", () => ({
  __esModule: true,
  default: () => <div>Listings Page</div>,
}));

vi.mock("../pages/ListingDetails", () => ({
  __esModule: true,
  default: () => <div>Listing Details Page</div>,
}));

vi.mock("../pages/Profile", () => ({
  __esModule: true,
  default: () => <div>Profile Page</div>,
}));


vi.mock("../context/AuthContext", () => {
  const React = require("react");
  return {
    __esModule: true,
    AuthProvider: ({ children }: { children: React.ReactNode }) => (
      <div data-testid="auth-provider">{children}</div>
    ),
  };
});

vi.mock("../components/routing/ProtectedRoute", () => ({
  __esModule: true,
  default: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="protected-route">{children}</div>
  ),
}));

function renderWithRoute(path: string) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <App />
    </MemoryRouter>
  );
}

describe("App routing", () => {
  it("renders Home on /", () => {
    renderWithRoute("/");

    expect(screen.getByTestId("auth-provider")).toBeInTheDocument();
    expect(screen.getByText("Home Page")).toBeInTheDocument();
    expect(screen.queryByTestId("protected-route")).not.toBeInTheDocument();
  });

  it("renders Login on /login", () => {
    renderWithRoute("/login");

    expect(screen.getByText("Login Page")).toBeInTheDocument();
    expect(screen.queryByTestId("protected-route")).not.toBeInTheDocument();
  });

  it("renders Register on /register", () => {
    renderWithRoute("/register");

    expect(screen.getByText("Register Page")).toBeInTheDocument();
    expect(screen.queryByTestId("protected-route")).not.toBeInTheDocument();
  });

  it("wraps /listings in ProtectedRoute and renders Listings", () => {
    renderWithRoute("/listings");

    expect(screen.getByTestId("protected-route")).toBeInTheDocument();
    expect(screen.getByText("Listings Page")).toBeInTheDocument();
  });

  it("wraps /listings/:id in ProtectedRoute and renders ListingDetails", () => {
    renderWithRoute("/listings/123");

    expect(screen.getByTestId("protected-route")).toBeInTheDocument();
    expect(screen.getByText("Listing Details Page")).toBeInTheDocument();
  });

  it("wraps /profile in ProtectedRoute and renders Profile", () => {
    renderWithRoute("/profile");

    expect(screen.getByTestId("protected-route")).toBeInTheDocument();
    expect(screen.getByText("Profile Page")).toBeInTheDocument();
  });
});
