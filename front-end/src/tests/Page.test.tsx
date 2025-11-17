import React from "react";
import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import Page from '../components/layout/Page'

describe("Page wrapper", () => {
  it("renders children", () => {
    render(
      <Page>
        <p data-testid="child">Hello</p>
      </Page>
    );

    expect(screen.getByTestId("child")).toBeInTheDocument();
  });

  it("applies custom className", () => {
    const { container } = render(
      <Page className="custom">
        <div />
      </Page>
    );

    expect(container.firstChild?.className).toContain("custom");
  });

  it("has correct base layout classes", () => {
    const { container } = render(<Page />);

    const root = container.firstChild as HTMLElement;
    expect(root.className).toContain("min-h-screen");
    expect(root.className).toContain("flex");
    expect(root.className).toContain("flex-col");
  });

  it("matches snapshot", () => {
    const { container } = render(<Page />);
    expect(container).toMatchSnapshot();
  });
});
