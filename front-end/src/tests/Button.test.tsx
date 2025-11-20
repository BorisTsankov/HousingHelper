
import React from "react";
import { describe, it, expect } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";

import { Button } from "../components/ui/Button";

describe("Button", () => {
  it("renders a button with default props", () => {
    render(<Button>Click me</Button>);

    const btn = screen.getByRole("button");

    expect(btn).toBeInTheDocument();
    expect(btn).toHaveTextContent("Click me");


    expect(btn.className).toContain("bg-blue-600");
    expect(btn.className).toContain("rounded-full");
  });

  it("applies ghost variant classes", () => {
    render(<Button variant="ghost">Ghost</Button>);

    const btn = screen.getByRole("button");

    expect(btn.className).toContain("bg-transparent");
    expect(btn.className).toContain("hover:bg-slate-100");
  });

  it("applies rounded-md radius", () => {
    render(<Button rounded="md">Radius</Button>);

    const btn = screen.getByRole("button");

    expect(btn.className).toContain("rounded-md");
  });

  it("merges custom className", () => {
    render(<Button className="custom-class">Custom</Button>);

    const btn = screen.getByRole("button");

    expect(btn.className).toContain("custom-class");
  });

  it("passes through additional props", () => {
    const handleClick = vi.fn();

    render(<Button onClick={handleClick}>Click</Button>);

    const btn = screen.getByRole("button");

    fireEvent.click(btn);

    expect(handleClick).toHaveBeenCalledTimes(1);
  });
});
