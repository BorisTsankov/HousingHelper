import React, { useState } from "react";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/layout/Navbar";
import Footer from "../components/layout/Footer";
import Page from "../components/layout/Page";
import { Link, useNavigate } from "react-router-dom";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      await login({ email, password });
      navigate("/", { replace: true });
    } catch (err: any) {
      setError(err.message || "Login failed");
    }
  };

  return (
    <Page>
      <Navbar />

      <div className="min-h-[60vh] flex items-center justify-center px-4">
        <div className="w-full max-w-md bg-white rounded-2xl shadow-md border border-slate-200 p-6">
          <h2 className="text-2xl font-semibold mb-2 text-slate-900">
            Welcome back
          </h2>
          <p className="text-sm text-slate-500 mb-6">
            Log in to access your saved listings and profile.
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* EMAIL */}
            <div>
              <label
                htmlFor="email"
                className="block text-sm mb-1 text-slate-700"
              >
                Email
              </label>
              <input
                id="email"
                className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                placeholder="you@example.com"
              />
            </div>

            {/* PASSWORD */}
            <div>
              <label
                htmlFor="password"
                className="block text-sm mb-1 text-slate-700"
              >
                Password
              </label>
              <input
                id="password"
                className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                placeholder="••••••••"
              />
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 text-white py-2.5 rounded-lg text-sm font-medium hover:bg-blue-700 transition"
            >
              Login
            </button>

            {error && <p className="text-red-500 text-sm mt-1">{error}</p>}
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-slate-600 mb-2">Don’t have an account?</p>

            <Link
              to="/register"
              className="inline-block w-full rounded-lg bg-slate-100 text-slate-800 text-sm font-medium py-2.5 hover:bg-slate-200 transition"
            >
              Create an account
            </Link>
          </div>
        </div>
      </div>

      <Footer />
    </Page>
  );
}
