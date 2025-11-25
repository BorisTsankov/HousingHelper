// import React from "react";
// import { describe, it, expect, vi, beforeEach } from "vitest";
// import { render, screen, waitFor } from "@testing-library/react";
// import { MemoryRouter, Routes, Route } from "react-router-dom";
// import userEvent from "@testing-library/user-event";
// import Login from "../pages/Login";
//
//
// const mockLogin = vi.fn();
// const mockNavigate = vi.fn();
//
//
// vi.mock("../context/AuthContext", () => ({
//   __esModule: true,
//   useAuth: () => ({
//     login: mockLogin,
//   }),
// }));
//
//
// vi.mock("../components/layout/Page", () => ({
//   __esModule: true,
//   default: ({ children }: { children: React.ReactNode }) => (
//     <div data-testid="page">{children}</div>
//   ),
// }));
//
// vi.mock("../components/layout/Navbar", () => ({
//   __esModule: true,
//   default: () => <div data-testid="navbar">Navbar</div>,
// }));
//
// vi.mock("../components/layout/Footer", () => ({
//   __esModule: true,
//   default: () => <div data-testid="footer">Footer</div>,
// }));
//
//
// vi.mock("react-router-dom", async () => {
//   const actual: any = await vi.importActual("react-router-dom");
//   return {
//     __esModule: true,
//     ...actual,
//     useNavigate: () => mockNavigate,
//   };
// });
//
// beforeEach(() => {
//   mockLogin.mockReset();
//   mockNavigate.mockReset();
// });
//
// function renderLogin(initialUrl: string = "/login") {
//   return render(
//     <MemoryRouter initialEntries={[initialUrl]}>
//       <Routes>
//         <Route path="/login" element={<Login />} />
//         {}
//         <Route path="/register" element={<div>Register page</div>} />
//         <Route path="/" element={<div>Home page</div>} />
//       </Routes>
//     </MemoryRouter>
//   );
// }
//
// describe("Login page", () => {
//   it("renders layout with navbar, footer, and form fields", () => {
//     renderLogin();
//
//     expect(screen.getByTestId("page")).toBeInTheDocument();
//     expect(screen.getByTestId("navbar")).toBeInTheDocument();
//     expect(screen.getByTestId("footer")).toBeInTheDocument();
//
//     expect(screen.getByText("Welcome back")).toBeInTheDocument();
//     expect(
//       screen.getByText("Log in to access your saved listings and profile.")
//     ).toBeInTheDocument();
//
//     expect(screen.getByLabelText("Email")).toBeInTheDocument();
//     expect(screen.getByLabelText("Password")).toBeInTheDocument();
//
//     expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument();
//
//     const registerLink = screen.getByRole("link", {
//       name: /create an account/i,
//     });
//     expect(registerLink).toHaveAttribute("href", "/register");
//   });
//
//   it("calls login with email and password and redirects on success", async () => {
//     mockLogin.mockResolvedValueOnce(undefined);
//
//     const user = userEvent.setup();
//     renderLogin();
//
//     const emailInput = screen.getByLabelText("Email") as HTMLInputElement;
//     const passwordInput = screen.getByLabelText(
//       "Password"
//     ) as HTMLInputElement;
//     const submitBtn = screen.getByRole("button", { name: /login/i });
//
//     await user.type(emailInput, "test@example.com");
//     await user.type(passwordInput, "supersecret");
//     await user.click(submitBtn);
//
//     await waitFor(() => {
//       expect(mockLogin).toHaveBeenCalledTimes(1);
//     });
//
//     expect(mockLogin).toHaveBeenCalledWith({
//       email: "test@example.com",
//       password: "supersecret",
//     });
//
//
//     expect(mockNavigate).toHaveBeenCalledWith("/", { replace: true });
//   });
//
//   it("shows error when login throws with a specific message", async () => {
//     mockLogin.mockRejectedValueOnce(new Error("Invalid credentials"));
//
//     const user = userEvent.setup();
//     renderLogin();
//
//     const emailInput = screen.getByLabelText("Email") as HTMLInputElement;
//     const passwordInput = screen.getByLabelText(
//       "Password"
//     ) as HTMLInputElement;
//     const submitBtn = screen.getByRole("button", { name: /login/i });
//
//     await user.type(emailInput, "wrong@example.com");
//     await user.type(passwordInput, "wrongpassword");
//     await user.click(submitBtn);
//
//     await waitFor(() => {
//       expect(
//         screen.getByText("Invalid credentials")
//       ).toBeInTheDocument();
//     });
//
//     expect(mockNavigate).not.toHaveBeenCalled();
//   });
//
//   it("shows generic error when login throws without a message", async () => {
//
//     mockLogin.mockRejectedValueOnce({});
//
//     const user = userEvent.setup();
//     renderLogin();
//
//     const emailInput = screen.getByLabelText("Email") as HTMLInputElement;
//     const passwordInput = screen.getByLabelText(
//       "Password"
//     ) as HTMLInputElement;
//     const submitBtn = screen.getByRole("button", { name: /login/i });
//
//     await user.type(emailInput, "user@example.com");
//     await user.type(passwordInput, "password123");
//     await user.click(submitBtn);
//
//     await waitFor(() => {
//       expect(screen.getByText("Login failed")).toBeInTheDocument();
//     });
//
//     expect(mockNavigate).not.toHaveBeenCalled();
//   });
//
//   it("clears previous error when resubmitting", async () => {
//     const user = userEvent.setup();
//
//
//     mockLogin.mockRejectedValueOnce(new Error("Invalid credentials"));
//
//     mockLogin.mockResolvedValueOnce(undefined);
//
//     renderLogin();
//
//     const emailInput = screen.getByLabelText("Email") as HTMLInputElement;
//     const passwordInput = screen.getByLabelText(
//       "Password"
//     ) as HTMLInputElement;
//     const submitBtn = screen.getByRole("button", { name: /login/i });
//
//
//     await user.type(emailInput, "wrong@example.com");
//     await user.type(passwordInput, "wrongpassword");
//     await user.click(submitBtn);
//
//     await screen.findByText("Invalid credentials");
//
//
//     await user.clear(emailInput);
//     await user.clear(passwordInput);
//     await user.type(emailInput, "ok@example.com");
//     await user.type(passwordInput, "correctpassword");
//     await user.click(submitBtn);
//
//     await waitFor(() => {
//       expect(screen.queryByText("Invalid credentials")).not.toBeInTheDocument();
//     });
//
//     expect(mockNavigate).toHaveBeenCalledWith("/", { replace: true });
//   });
// });
