import React from "react";
import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";

import { Card } from "../components/ui/Card";

describe("Card", () => {
  it("renders children", () => {
    render(
      <Card>
        <div>Inner content</div>
      </Card>
    );

    expect(screen.getByText("Inner content")).toBeInTheDocument();
  });

  it("applies default classes", () => {
    render(<Card>Test</Card>);


    const card = screen.getByText("Test");

    expect(card.className).toContain("rounded-3xl");
    expect(card.className).toContain("border");
    expect(card.className).toContain("border-slate-200");
    expect(card.className).toContain("bg-white");
    expect(card.className).toContain("shadow-sm");
  });

  it("merges custom className", () => {
    render(<Card className="custom-class">Content</Card>);

    const card = screen.getByText("Content");

    expect(card.className).toContain("custom-class");
  });
});