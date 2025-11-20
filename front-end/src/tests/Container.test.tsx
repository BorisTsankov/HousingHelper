import React from "react";
import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";

import { Container } from "../components/ui/Container";

describe("Container", () => {
  it("renders children", () => {
    render(
      <Container>
        <div>Inside container</div>
      </Container>
    );

    expect(screen.getByText("Inside container")).toBeInTheDocument();
  });

  it("applies the layout classes", () => {
    render(
      <Container>
        <div>Content</div>
      </Container>
    );

    const wrapper = screen.getByText("Content").parentElement;

    expect(wrapper?.className).toContain("mx-auto");
    expect(wrapper?.className).toContain("max-w-7xl");
    expect(wrapper?.className).toContain("px-4");
    expect(wrapper?.className).toContain("sm:px-6");
    expect(wrapper?.className).toContain("lg:px-8");
  });
});
