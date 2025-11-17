import React from "react";
import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { FiltersPanel } from '../components/layout/FiltersPanel'

describe("FiltersPanel", () => {
  const renderPanel = (ui?: React.ReactNode, props?: any) =>
    render(
      <FiltersPanel {...props}>
        {ui ?? <p data-testid="child">Child content</p>}
      </FiltersPanel>
    );

  it("renders with default title", () => {
    renderPanel();
    expect(screen.getByText("Search & Filters")).toBeInTheDocument();
  });

  it("renders with a custom title", () => {
    renderPanel(null, { title: "Custom Filters" });
    expect(screen.getByText("Custom Filters")).toBeInTheDocument();
  });

  it("sets the aria-label based on the title", () => {
    renderPanel(null, { title: "My Panel" });
    expect(screen.getByLabelText("My Panel")).toBeInTheDocument();
  });

  it("renders children inside the panel", () => {
    renderPanel(<p data-testid="child">Hello child</p>);
    expect(screen.getByTestId("child")).toBeInTheDocument();
    expect(screen.getByText("Hello child")).toBeInTheDocument();
  });

  it("applies a custom className", () => {
    const { container } = renderPanel(null, { className: "custom-class" });
    const aside = container.querySelector("aside");
    expect(aside?.className).toContain("custom-class");
  });

  it("renders the icon", () => {
    renderPanel();
    // lucide icons render as <svg role="img">
    const icon = screen.getByRole("img");
    expect(icon).toBeInTheDocument();
  });

  it("renders structural elements (header, content, divider)", () => {
    const { container } = renderPanel();

    // Header section
    expect(
      container.querySelector(".bg-gradient-to-b.from-gray-50.to-white")
    ).not.toBeNull();

    // Content wrapper
    expect(container.querySelector(".space-y-4")).not.toBeNull();

    // Divider line at bottom
    expect(
      container.querySelector(".bg-gradient-to-r.from-transparent.via-gray-200.to-transparent")
    ).not.toBeNull();
  });

  it("matches snapshot", () => {
    const { container } = renderPanel();
    expect(container).toMatchSnapshot();
  });
});
