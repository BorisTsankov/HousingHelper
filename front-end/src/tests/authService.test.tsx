// import { describe, it, expect, vi, beforeEach } from "vitest";
// import {
//   registerUser,
//   loginUser,
//   getCurrentUser,
//   logoutUser,
// } from "../services/authService";
//
// const mockFetch = vi.fn();
//
// beforeEach(() => {
//   mockFetch.mockReset();
//
//   global.fetch = mockFetch;
// });
//
// describe("authService", () => {
//
//   it("registerUser sends correct request and returns user on success", async () => {
//     const mockUser = { id: "1", name: "Boris", email: "test@example.com" };
//
//     mockFetch.mockResolvedValue({
//       ok: true,
//       json: async () => mockUser,
//     });
//
//     const result = await registerUser({
//       name: "Boris",
//       email: "test@example.com",
//       password: "password123",
//     } as any);
//
//     expect(mockFetch).toHaveBeenCalledWith("/api/auth/register", {
//       method: "POST",
//       headers: {
//         "Content-Type": "application/json",
//       },
//       body: JSON.stringify({
//         name: "Boris",
//         email: "test@example.com",
//         password: "password123",
//       }),
//       credentials: "include",
//     });
//
//     expect(result).toEqual(mockUser);
//   });
//
//   it("registerUser throws error with API message when registration fails with message", async () => {
//     mockFetch.mockResolvedValue({
//       ok: false,
//       json: async () => ({ message: "Email already in use" }),
//     });
//
//     await expect(
//       registerUser({
//         name: "Boris",
//         email: "test@example.com",
//         password: "password123",
//       } as any)
//     ).rejects.toThrow("Email already in use");
//   });
//
//   it("registerUser throws default message when registration fails without message", async () => {
//     mockFetch.mockResolvedValue({
//       ok: false,
//       json: async () => ({}),
//     });
//
//     await expect(
//       registerUser({
//         name: "Boris",
//         email: "test@example.com",
//         password: "password123",
//       } as any)
//     ).rejects.toThrow("Registration failed");
//   });
//
//   it("registerUser throws default message when error body is not JSON", async () => {
//     mockFetch.mockResolvedValue({
//       ok: false,
//       json: async () => {
//         throw new Error("Invalid JSON");
//       },
//     });
//
//     await expect(
//       registerUser({
//         name: "Boris",
//         email: "test@example.com",
//         password: "password123",
//       } as any)
//     ).rejects.toThrow("Registration failed");
//   });
//
//
//   it("loginUser sends correct request and returns user on success", async () => {
//     const mockUser = { id: "1", name: "Boris", email: "test@example.com" };
//
//     mockFetch.mockResolvedValue({
//       ok: true,
//       json: async () => mockUser,
//     });
//
//     const result = await loginUser({
//       email: "test@example.com",
//       password: "password123",
//     } as any);
//
//     expect(mockFetch).toHaveBeenCalledWith("/api/auth/login", {
//       method: "POST",
//       headers: {
//         "Content-Type": "application/json",
//       },
//       body: JSON.stringify({
//         email: "test@example.com",
//         password: "password123",
//       }),
//       credentials: "include",
//     });
//
//     expect(result).toEqual(mockUser);
//   });
//
//   it("loginUser throws error with API message when login fails with message", async () => {
//     mockFetch.mockResolvedValue({
//       ok: false,
//       json: async () => ({ message: "Invalid credentials" }),
//     });
//
//     await expect(
//       loginUser({
//         email: "test@example.com",
//         password: "wrong",
//       } as any)
//     ).rejects.toThrow("Invalid credentials");
//   });
//
//   it("loginUser throws default message when login fails without message", async () => {
//     mockFetch.mockResolvedValue({
//       ok: false,
//       json: async () => ({}),
//     });
//
//     await expect(
//       loginUser({
//         email: "test@example.com",
//         password: "wrong",
//       } as any)
//     ).rejects.toThrow("Login failed");
//   });
//
//
//   it("getCurrentUser returns user when response is 200 OK", async () => {
//     const mockUser = { id: "1", name: "Boris", email: "test@example.com" };
//
//     mockFetch.mockResolvedValue({
//       ok: true,
//       status: 200,
//       json: async () => mockUser,
//     });
//
//     const result = await getCurrentUser();
//
//     expect(mockFetch).toHaveBeenCalledWith("/api/auth/me", {
//       method: "GET",
//       credentials: "include",
//     });
//
//     expect(result).toEqual(mockUser);
//   });
//
//   it("getCurrentUser returns null when response is 401", async () => {
//     mockFetch.mockResolvedValue({
//       ok: false,
//       status: 401,
//     });
//
//     const result = await getCurrentUser();
//
//     expect(result).toBeNull();
//   });
//
//   it("getCurrentUser throws error when response is not ok and not 401", async () => {
//     mockFetch.mockResolvedValue({
//       ok: false,
//       status: 500,
//     });
//
//     await expect(getCurrentUser()).rejects.toThrow("Failed to fetch current user");
//   });
//
//
//   it("logoutUser sends POST and resolves when response is ok", async () => {
//     mockFetch.mockResolvedValue({
//       ok: true,
//       status: 200,
//     });
//
//     await expect(logoutUser()).resolves.toBeUndefined();
//
//     expect(mockFetch).toHaveBeenCalledWith("/api/auth/logout", {
//       method: "POST",
//       credentials: "include",
//     });
//   });
//
//   it("logoutUser resolves even when response status is 204", async () => {
//     mockFetch.mockResolvedValue({
//       ok: false, // some backends send 204 without ok === true
//       status: 204,
//     });
//
//     await expect(logoutUser()).resolves.toBeUndefined();
//   });
//
//   it("logoutUser throws error when response is not ok and not 204", async () => {
//     mockFetch.mockResolvedValue({
//       ok: false,
//       status: 500,
//     });
//
//     await expect(logoutUser()).rejects.toThrow("Failed to log out");
//   });
// });
