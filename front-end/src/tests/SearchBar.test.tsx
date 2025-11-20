import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { SearchBar } from "../components/search/SearchBar";

describe("SearchBar", () => {
  const setup = (props = {}) => render(<SearchBar {...props} />);

  it("renders the input field", () => {
    setup();
    expect(
      screen.getByPlaceholderText("Search properties...")
    ).toBeInTheDocument();
  });

  it("updates the input value", () => {
    setup();
    const input = screen.getByPlaceholderText("Search properties...");

    fireEvent.change(input, { target: { value: "Eindhoven" } });

    expect(input).toHaveValue("Eindhoven");
  });

  it("calls onSearch with the current input value", () => {
    const onSearch = vi.fn();
    setup({ onSearch });

    const input = screen.getByPlaceholderText("Search properties...");
    const button = screen.getByRole("button", { name: /start search/i });

    fireEvent.change(input, { target: { value: "Amsterdam" } });
    fireEvent.click(button);

    expect(onSearch).toHaveBeenCalledTimes(1);
    expect(onSearch).toHaveBeenCalledWith("Amsterdam");
  });

  it("does not throw if onSearch is undefined", () => {
    setup(); // no onSearch
    const button = screen.getByRole("button", { name: /start search/i });

    expect(() => fireEvent.click(button)).not.toThrow();
  });

  it("matches snapshot", () => {
    const { container } = setup();
    expect(container).toMatchSnapshot();
  });
});
