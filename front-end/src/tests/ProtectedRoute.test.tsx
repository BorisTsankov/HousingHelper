import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";

vi.mock("../../context/AuthContext", () => ({
  useAuth: vi.fn(),
}));

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<typeof import("react-router-dom")>(
    "react-router-dom"
  );
  return {
    ...actual,
    // Replace Navigate with a test-friendly component
    Navigate: ({ to }: { to: string }) => (
      <div data-testid="navigate">NAVIGATE:{to}</div>
    ),
  };
});

import { useAuth } from "../components/context/AuthContext";
import ProtectedRoute from "../components/routing/ProtectedRoute";

describe("ProtectedRoute", () => {
  const useAuthMock = useAuth as unknown as vi.Mock;

  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderProtected = () =>
    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div data-testid="protected">Protected content</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

  it("shows loading state when auth is loading", () => {
    useAuthMock.mockReturnValue({ isAuthenticated: false, loading: true });

    renderProtected();

    expect(screen.getByText("Loading...")).toBeInTheDocument();
    expect(screen.queryByTestId("protected")).not.toBeInTheDocument();
  });

  it("navigates to /register when not authenticated", () => {
    useAuthMock.mockReturnValue({ isAuthenticated: false, loading: false });

    renderProtected();

    expect(screen.getByTestId("navigate")).toHaveTextContent("NAVIGATE:/register");
    expect(screen.queryByTestId("protected")).not.toBeInTheDocument();
  });

  it("renders children when authenticated", () => {
    useAuthMock.mockReturnValue({ isAuthenticated: true, loading: false });

    renderProtected();

    expect(screen.getByTestId("protected")).toBeInTheDocument();
    expect(screen.queryByTestId("navigate")).not.toBeInTheDocument();
  });
});
