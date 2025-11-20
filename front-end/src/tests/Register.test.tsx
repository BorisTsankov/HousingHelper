import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import Register from "../pages/Register";


vi.mock("../components/layout/Page", () => ({
  __esModule: true,
  default: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="page">{children}</div>
  ),
}));

vi.mock("../components/layout/Navbar", () => ({
  __esModule: true,
  default: () => <div data-testid="navbar">Navbar</div>,
}));

vi.mock("../components/layout/Footer", () => ({
  __esModule: true,
  default: () => <div data-testid="footer">Footer</div>,
}));

vi.mock("../components/ui/Section", () => ({
  __esModule: true,
  Section: ({
    children,
    title,
  }: {
    children: React.ReactNode;
    title?: string;
  }) => (
    <section data-testid="section">
      {title && <h2>{title}</h2>}
      {children}
    </section>
  ),
}));

vi.mock("../components/register/RegisterForm", () => ({
  __esModule: true,
  default: () => <div data-testid="register-form">RegisterForm</div>,
}));

function renderRegister(initialUrl: string = "/register") {
  return render(
    <MemoryRouter initialEntries={[initialUrl]}>
      <Routes>
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<div>Login page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe("Register page", () => {
  it("renders layout, section, and registration UI", () => {
    renderRegister();


    expect(screen.getByTestId("page")).toBeInTheDocument();
    expect(screen.getByTestId("navbar")).toBeInTheDocument();
    expect(screen.getByTestId("footer")).toBeInTheDocument();
    expect(screen.getByTestId("section")).toBeInTheDocument();


    expect(
      screen.getByText("Create your account")
    ).toBeInTheDocument();
    expect(
      screen.getByText(
        "Sign up to save listings, manage your profile and more."
      )
    ).toBeInTheDocument();


    expect(screen.getByTestId("register-form")).toBeInTheDocument();


    expect(
      screen.getByText("Already have an account?")
    ).toBeInTheDocument();

    const loginLink = screen.getByRole("link", { name: /go to login/i });
    expect(loginLink).toHaveAttribute("href", "/login");
  });
});
