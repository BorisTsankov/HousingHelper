import React from "react";
import { describe, it, vi, expect, beforeEach } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import Navbar from '../components/layout/Navbar'
import { MemoryRouter } from "react-router-dom";

vi.mock("../../context/AuthContext", () => ({
  useAuth: vi.fn(),
}));

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");
  return {
    ...actual,
    useNavigate: () => vi.fn(),
  };
});

import { useAuth } from "../../context/AuthContext";

describe("Navbar", () => {
  const mockNavigate = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();

    // override useNavigate mock for each test
    vi.mock("react-router-dom", async () => {
      const actual = await vi.importActual("react-router-dom");
      return {
        ...actual,
        useNavigate: () => mockNavigate,
      };
    });
  });

  const renderNav = () =>
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );

  it("shows login icon when user is NOT authenticated", () => {
    (useAuth as any).mockReturnValue({
      isAuthenticated: false,
      user: null,
      logout: vi.fn(),
    });

    renderNav();

    expect(screen.getByRole("button")).toBeInTheDocument();
    expect(screen.queryByText("Logout")).not.toBeInTheDocument();
  });

  it("shows user info and logout when authenticated", () => {
    (useAuth as any).mockReturnValue({
      isAuthenticated: true,
      user: { name: "Boris", email: "test@test.com" },
      logout: vi.fn(),
    });

    renderNav();

    expect(screen.getByText("Boris")).toBeInTheDocument();
    expect(screen.getByText("Logout")).toBeInTheDocument();
  });

  it("navigates to /login when unauthenticated user icon is clicked", () => {
    (useAuth as any).mockReturnValue({
      isAuthenticated: false,
      user: null,
      logout: vi.fn(),
    });

    renderNav();

    const userBtn = screen.getByRole("button");
    fireEvent.click(userBtn);

    expect(mockNavigate).toHaveBeenCalledWith("/login");
  });

  it("navigates to /profile when authenticated user is clicked", () => {
    (useAuth as any).mockReturnValue({
      isAuthenticated: true,
      user: { name: "Boris" },
      logout: vi.fn(),
    });

    renderNav();

    const profileBtn = screen.getByText("Boris").closest("button")!;
    fireEvent.click(profileBtn);

    expect(mockNavigate).toHaveBeenCalledWith("/profile");
  });

  it("calls logout and navigates to /register", async () => {
    const logoutMock = vi.fn();

    (useAuth as any).mockReturnValue({
      isAuthenticated: true,
      user: { name: "Boris" },
      logout: logoutMock,
    });

    renderNav();

    fireEvent.click(screen.getByText("Logout"));

    expect(logoutMock).toHaveBeenCalled();
    expect(mockNavigate).toHaveBeenCalledWith("/register");
  });

  it("matches snapshot", () => {
    (useAuth as any).mockReturnValue({
      isAuthenticated: false,
      user: null,
      logout: vi.fn(),
    });

    const { container } = renderNav();
    expect(container).toMatchSnapshot();
  });
});
