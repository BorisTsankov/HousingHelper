import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";

import { Input } from "../components/ui/Input"

describe("Input", () => {
  it("renders with base classes and placeholder", () => {
    render(<Input placeholder="Your name" />);

    const input = screen.getByPlaceholderText("Your name") as HTMLInputElement;

    expect(input).toBeInTheDocument();
    expect(input.className).toContain("w-full");
    expect(input.className).toContain("rounded-xl");
    expect(input.className).toContain("border-slate-300");
    expect(input.className).toContain("focus:border-blue-500");
    expect(input.className).toContain("focus:ring-blue-200");
  });

  it("merges custom className", () => {
    render(<Input className="custom-class" />);

    const input = screen.getByRole("textbox") as HTMLInputElement;

    expect(input.className).toContain("custom-class");
  });

  it("forwards other props (type, onChange, value etc.)", () => {
    const handleChange = vi.fn();

    render(
      <Input
        type="email"
        value="test@example.com"
        onChange={handleChange}
        aria-label="email-input"
      />
    );

    const input = screen.getByLabelText("email-input") as HTMLInputElement;

    expect(input.type).toBe("email");
    expect(input.value).toBe("test@example.com");

    fireEvent.change(input, { target: { value: "new@example.com" } });
    expect(handleChange).toHaveBeenCalled();
  });
});
