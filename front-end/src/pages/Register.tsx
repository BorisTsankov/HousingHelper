import { useState } from "react";

export default function Register() {
    const [form, setForm] = useState({ email: "", name: "", password: "" });
    const [message, setMessage] = useState("");

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) =>
        setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const res = await fetch("/api/users/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(form),
            });
            if (res.ok) setMessage("Registration successful");
            else setMessage("Error: " + (await res.text()));
        } catch (err) {
            setMessage("Network error: " + err);
        }
    };

    return (
        <div style={{ maxWidth: 400, margin: "2rem auto" }}>
            <h2>Register</h2>
            <form onSubmit={handleSubmit}>
                <input name="email" placeholder="Email" value={form.email} onChange={handleChange} required />
                <br />
                <input name="name" placeholder="Name" value={form.name} onChange={handleChange} required />
                <br />
                <input name="password" type="password" placeholder="Password" value={form.password} onChange={handleChange} required />
                <br />
                <button type="submit">Register</button>
            </form>
            <p>{message}</p>
        </div>
    );
}
