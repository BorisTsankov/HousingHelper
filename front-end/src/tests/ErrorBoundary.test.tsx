import React from "react";
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { ErrorBoundary } from "../ErrorBoundary";


function Boom() {
  throw new Error("Test explosion!");
}

describe("ErrorBoundary", () => {
  const originalConsoleError = console.error;

  beforeEach(() => {

    console.error = vi.fn();
  });

  afterEach(() => {
    console.error = originalConsoleError;
  });

  it("renders children when no error occurs", () => {
    render(
      <ErrorBoundary>
        <div data-testid="child">Safe Content</div>
      </ErrorBoundary>
    );

    expect(screen.getByTestId("child")).toBeInTheDocument();
    expect(screen.queryByText("Something broke while rendering.")).toBeNull();
  });

  it("shows fallback UI when a child throws", () => {
    render(
      <ErrorBoundary>
        <Boom />
      </ErrorBoundary>
    );


    expect(
      screen.getByText("Something broke while rendering.")
    ).toBeInTheDocument();


    const pre = screen.getByText(/Test explosion!/);
    expect(pre.tagName.toLowerCase()).toBe("pre");
  });

  it("calls console.error when an error is caught", () => {
    render(
      <ErrorBoundary>
        <Boom />
      </ErrorBoundary>
    );

    expect(console.error).toHaveBeenCalled();
  });
});
