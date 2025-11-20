import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import Profile from "../pages/Profile";


let mockUser: any = null;

vi.mock("../context/AuthContext", () => ({
  __esModule: true,
  useAuth: () => ({
    user: mockUser,
  }),
}));


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

function renderProfile(initialUrl: string = "/profile") {
  return render(
    <MemoryRouter initialEntries={[initialUrl]}>
      <Routes>
        <Route path="/profile" element={<Profile />} />
        <Route path="/login" element={<div>Login page</div>} />
        <Route path="/listings" element={<div>Listings page</div>} />
        <Route path="/" element={<div>Home page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

beforeEach(() => {
  mockUser = null;
});

describe("Profile page", () => {
  it("shows login required message when user is not logged in", () => {
    mockUser = null;

    renderProfile();


    expect(screen.getByTestId("page")).toBeInTheDocument();
    expect(screen.getByTestId("navbar")).toBeInTheDocument();
    expect(screen.getByTestId("footer")).toBeInTheDocument();


    expect(
      screen.getByText("You must be logged in to view your profile.")
    ).toBeInTheDocument();

    const loginLink = screen.getByRole("link", { name: /go to login/i });
    expect(loginLink).toHaveAttribute("href", "/login");
  });

  it("renders profile info when user is logged in (with Unknown member date fallback)", () => {
    mockUser = {
      name: "Jane Doe",
      email: "jane@example.com",

      createdAt: undefined,
    };

    renderProfile();


    expect(screen.getByTestId("page")).toBeInTheDocument();
    expect(screen.getByTestId("navbar")).toBeInTheDocument();
    expect(screen.getByTestId("footer")).toBeInTheDocument();


    expect(screen.getByText("Your Profile")).toBeInTheDocument();
    expect(
      screen.getByText(
        "Manage your account details and navigate to your listings."
      )
    ).toBeInTheDocument();


    expect(screen.getByText("Name")).toBeInTheDocument();
    expect(screen.getByText("Email")).toBeInTheDocument();
    expect(screen.getByText("Member since")).toBeInTheDocument();


    expect(screen.getByText("Jane Doe")).toBeInTheDocument();
    expect(screen.getByText("jane@example.com")).toBeInTheDocument();
    expect(screen.getByText("Unknown")).toBeInTheDocument();


    const listingsLink = screen.getByRole("link", { name: /view listings/i });
    expect(listingsLink).toHaveAttribute("href", "/listings");

    const homeLink = screen.getByRole("link", { name: /back to home/i });
    expect(homeLink).toHaveAttribute("href", "/");
  });

  it("formats and displays member since date when createdAt is provided", () => {


    mockUser = {
      name: "John Smith",
      email: "john@example.com",
      createdAt: "2024-01-15T00:00:00.000Z",
    };

    renderProfile();

    expect(screen.getByText("John Smith")).toBeInTheDocument();
    expect(screen.getByText("john@example.com")).toBeInTheDocument();



    const memberSinceLabel = screen.getByText("Member since");
    const parent = memberSinceLabel.parentElement;
    expect(parent).not.toBeNull();

    if (parent) {
      const valueNode = Array.from(parent.querySelectorAll("p")).find((p) =>
        p.className.includes("text-lg")
      );
      expect(valueNode).toBeTruthy();
      expect(valueNode?.textContent).not.toBe("Unknown");

      expect(valueNode?.textContent?.trim()).not.toHaveLength(0);
    }
  });
});
