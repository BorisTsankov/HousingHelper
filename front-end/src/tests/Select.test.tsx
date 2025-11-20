import React from "react";
import { describe, it, expect } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";

import { Select } from "../components/ui/Select";

describe("Select", () => {
  it("renders children options", () => {
    render(
      <Select aria-label="my-select">
        <option value="one">One</option>
        <option value="two">Two</option>
      </Select>
    );

    const select = screen.getByLabelText("my-select") as HTMLSelectElement;

    expect(select).toBeInTheDocument();
    expect(screen.getByText("One")).toBeInTheDocument();
    expect(screen.getByText("Two")).toBeInTheDocument();
  });

  it("applies className and forwards props", () => {
    render(
      <Select className="custom-select" defaultValue="two" aria-label="sel">
        <option value="one">One</option>
        <option value="two">Two</option>
      </Select>
    );

    const select = screen.getByLabelText("sel") as HTMLSelectElement;

    expect(select.className).toContain("custom-select");
    expect(select.value).toBe("two");
  });

  it("forwards ref to the underlying select element", () => {
    const ref = React.createRef<HTMLSelectElement>();

    render(
      <Select ref={ref} aria-label="with-ref">
        <option value="1">1</option>
      </Select>
    );

    expect(ref.current).not.toBeNull();
    expect(ref.current?.tagName).toBe("SELECT");
  });

  it("handles change events", () => {
    const handleChange = vi.fn();

    render(
      <Select aria-label="change-select" onChange={handleChange}>
        <option value="one">One</option>
        <option value="two">Two</option>
      </Select>
    );

    const select = screen.getByLabelText("change-select") as HTMLSelectElement;

    fireEvent.change(select, { target: { value: "two" } });
    expect(handleChange).toHaveBeenCalled();
  });
});
