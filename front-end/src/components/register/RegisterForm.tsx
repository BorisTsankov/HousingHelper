import React, { useState } from "react";
import { RegisterRequest, UserResponse } from "../../types/auth";
import { registerUser } from "../../services/authService";

const RegisterForm: React.FC = () => {
  const [form, setForm] = useState<RegisterRequest>({
    email: "",
    name: "",
    password: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successUser, setSuccessUser] = useState<UserResponse | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    setSuccessUser(null);
    setLoading(true);

    try {
      const user = await registerUser(form);
      setSuccessUser(user);
    } catch (err: any) {
      const backendMessage =
        err?.response?.data?.message || err?.response?.data || err.message || "Something went wrong";
      setError(backendMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm mb-1 text-slate-700" htmlFor="name">
          Name
        </label>
        <input
          id="name"
          name="name"
          value={form.name}
          onChange={handleChange}
          type="text"
          className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          required
        />
      </div>

      <div>
        <label className="block text-sm mb-1 text-slate-700" htmlFor="email">
          Email
        </label>
        <input
          id="email"
          name="email"
          value={form.email}
          onChange={handleChange}
          type="email"
          className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          required
        />
      </div>

      <div>
        <label
          className="block text-sm mb-1 text-slate-700"
          htmlFor="password"
        >
          Password
        </label>
        <input
          id="password"
          name="password"
          value={form.password}
          onChange={handleChange}
          type="password"
          className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          required
        />
      </div>

      <button
        type="submit"
        disabled={loading}
        className="w-full rounded-lg bg-blue-600 text-white py-2.5 text-sm font-medium disabled:opacity-50 hover:bg-blue-700 transition"
      >
        {loading ? "Creating account..." : "Register"}
      </button>

      {error && <p className="text-red-500 text-sm mt-2">{error}</p>}

      {successUser && (
        <p className="text-green-600 text-sm mt-2">
          Account created for <strong>{successUser.email}</strong>. <br />
          Please check your email to verify your account before logging in.
        </p>
      )}
    </form>
  );
};

export default RegisterForm;
