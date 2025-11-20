import React from "react";
import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";

import { Section } from "../components/ui/Section";

describe("Section", () => {
  it("renders title and children, with spacing when title is present", () => {
    render(
      <Section title="My Section">
        <div>Section content</div>
      </Section>
    );

    const heading = screen.getByText("My Section");
    expect(heading.tagName).toBe("H2");
    expect(heading.className).toContain("text-2xl");
    expect(heading.className).toContain("font-bold");
    expect(heading.className).toContain("text-slate-900");

    expect(heading.className).not.toContain("text-center");

    const content = screen.getByText("Section content");
    const contentWrapper = content.parentElement;
    expect(contentWrapper?.className).toContain("mt-8");
  });

  it("centers the title when center prop is true", () => {
    render(
      <Section title="Centered" center>
        <div>Center content</div>
      </Section>
    );

    const heading = screen.getByText("Centered");
    expect(heading.className).toContain("text-center");
  });

  it("renders without title and without extra margin when title is missing", () => {
    render(
      <Section>
        <div>No title content</div>
      </Section>
    );

    expect(screen.queryByRole("heading")).toBeNull();

    const content = screen.getByText("No title content");
    const contentWrapper = content.parentElement;


    expect(contentWrapper?.className).toBe("");
  });
});
