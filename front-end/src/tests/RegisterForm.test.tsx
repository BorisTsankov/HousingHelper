import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

vi.mock("../services/authService", () => ({
  registerUser: vi.fn(),
}));

import { registerUser } from "../services/authService";
import RegisterForm from "../components/register/RegisterForm";

describe("RegisterForm", () => {
  const user = userEvent.setup();
  const registerUserMock = registerUser as unknown as vi.Mock;

  beforeEach(() => {
    vi.clearAllMocks();
  });

  const fillForm = async () => {
    render(<RegisterForm />);

    const nameInput = screen.getByLabelText("Name");
    const emailInput = screen.getByLabelText("Email");
    const passwordInput = screen.getByLabelText("Password");

    await user.type(nameInput, "Boris");
    await user.type(emailInput, "test@example.com");
    await user.type(passwordInput, "password123");
  };

  it("renders all fields and submit button", () => {
    render(<RegisterForm />);

    expect(screen.getByLabelText("Name")).toBeInTheDocument();
    expect(screen.getByLabelText("Email")).toBeInTheDocument();
    expect(screen.getByLabelText("Password")).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: "Register" })
    ).toBeInTheDocument();
  });

  it("submits form successfully and shows success message", async () => {
    registerUserMock.mockResolvedValue({
      email: "test@example.com",
      id: "123",
      name: "Boris",
    });

    await fillForm();

    const submitBtn = screen.getByRole("button", { name: "Register" });
    await user.click(submitBtn);

    await waitFor(() => {
      expect(registerUserMock).toHaveBeenCalledWith({
        name: "Boris",
        email: "test@example.com",
        password: "password123",
      });
    });

    await waitFor(() => {
      expect(
        screen.getByText("Account created for", { exact: false })
      ).toBeInTheDocument();
      expect(screen.getByText("test@example.com")).toBeInTheDocument();
    });
  }); // ← this was missing

  it("shows loading state while submitting", async () => {

    registerUserMock.mockImplementation(
      () => new Promise((resolve) => setTimeout(() => resolve({ email: "x" }), 50))
    );

    await fillForm();

    const submitBtn = screen.getByRole("button", { name: "Register" });
    await user.click(submitBtn);


    expect(submitBtn).toBeDisabled();
    expect(submitBtn).toHaveTextContent("Creating account...");

    await waitFor(() => {
      expect(submitBtn).not.toBeDisabled();
    });
  });

  it("shows error message when registration fails", async () => {
    registerUserMock.mockRejectedValue(new Error("Registration failed"));

    await fillForm();

    const submitBtn = screen.getByRole("button", { name: "Register" });
    await user.click(submitBtn);

    await waitFor(() => {
      expect(screen.getByText("Registration failed")).toBeInTheDocument();
    });
  });

  it("falls back to generic error message if error has no message", async () => {
    registerUserMock.mockRejectedValue({});

    await fillForm();

    const submitBtn = screen.getByRole("button", { name: "Register" });
    await user.click(submitBtn);

    await waitFor(() => {
      expect(
        screen.getByText("Something went wrong")
      ).toBeInTheDocument();
    });
  });
it("shows backend string error when response.data is a string", async () => {
    registerUserMock.mockRejectedValue({
      response: { data: "Email already in use" },
    });

    await fillForm();

    const submitBtn = screen.getByRole("button", { name: "Register" });
    await user.click(submitBtn);

    await waitFor(() => {
      expect(screen.getByText("Email already in use")).toBeInTheDocument();
    });
  });

  it("shows backend message when response.data.message is provided", async () => {
    registerUserMock.mockRejectedValue({
      response: { data: { message: "Password too weak" } },
    });

    await fillForm();

    const submitBtn = screen.getByRole("button", { name: "Register" });
    await user.click(submitBtn);

    await waitFor(() => {
      expect(screen.getByText("Password too weak")).toBeInTheDocument();
    });
  });
});
