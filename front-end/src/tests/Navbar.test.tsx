import React from "react";
import { describe, it, vi, expect, beforeEach } from "vitest";
import {
  render,
  screen,
  fireEvent,
  waitFor,
} from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";


const mockNavigate = vi.fn();


vi.mock("../context/AuthContext", () => ({
  useAuth: vi.fn(),
}));


vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<typeof import("react-router-dom")>(
    "react-router-dom"
  );
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

import Navbar from "../components/layout/Navbar";
import { useAuth } from "../context/AuthContext";

describe("Navbar", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockNavigate.mockClear();
  });

  const renderNav = () =>
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );

  it("shows login icon when user is NOT authenticated", () => {
    (useAuth as unknown as vi.Mock).mockReturnValue({
      isAuthenticated: false,
      user: null,
      logout: vi.fn(),
    });

    renderNav();


    const loginButton = screen.getByRole("button", { name: /login/i });
    expect(loginButton).toBeInTheDocument();

    expect(screen.queryByText("Logout")).not.toBeInTheDocument();
  });

  it("shows user info and logout when authenticated", () => {
    (useAuth as unknown as vi.Mock).mockReturnValue({
      isAuthenticated: true,
      user: { name: "Boris", email: "test@test.com" },
      logout: vi.fn(),
    });

    renderNav();

    expect(screen.getByText("Boris")).toBeInTheDocument();
    expect(screen.getByText("Logout")).toBeInTheDocument();
  });

  it("navigates to /login when unauthenticated user icon is clicked", () => {
    (useAuth as unknown as vi.Mock).mockReturnValue({
      isAuthenticated: false,
      user: null,
      logout: vi.fn(),
    });

    renderNav();

    const loginButton = screen.getByRole("button", { name: /login/i });
    fireEvent.click(loginButton);

    expect(mockNavigate).toHaveBeenCalledWith("/login");
  });

  it("navigates to /profile when authenticated user is clicked", () => {
    (useAuth as unknown as vi.Mock).mockReturnValue({
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
    const logoutMock = vi.fn().mockResolvedValue(undefined);

    (useAuth as unknown as vi.Mock).mockReturnValue({
      isAuthenticated: true,
      user: { name: "Boris" },
      logout: logoutMock,
    });

    renderNav();

    fireEvent.click(screen.getByText("Logout"));

    await waitFor(() => {
      expect(logoutMock).toHaveBeenCalled();
      expect(mockNavigate).toHaveBeenCalledWith("/register");
    });
  });

  it("matches snapshot", () => {
    (useAuth as unknown as vi.Mock).mockReturnValue({
      isAuthenticated: false,
      user: null,
      logout: vi.fn(),
    });

    const { container } = renderNav();
    expect(container).toMatchSnapshot();
  });
});
