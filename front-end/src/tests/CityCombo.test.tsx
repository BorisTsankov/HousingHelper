import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";



vi.mock("../../styles/filterField", () => ({
  FILTER_FIELD_CLASS: "filter-field",
}));

import { CityCombo } from "../components/search/CityCombo"; // <-- adjust path if needed

const options = [
  { value: "ams", label: "Amsterdam" },
  { value: "rot", label: "Rotterdam" },
  { value: "eind", label: "Eindhoven" },
  { value: "haar", label: "Haarlem" },
  { value: "utr", label: "Utrecht" },
];

describe("CityCombo", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderCombo = (props: Partial<React.ComponentProps<typeof CityCombo>> = {}) => {
    const onChange = props.onChange ?? vi.fn();
    const result = render(
      <CityCombo
        value={props.value}
        onChange={onChange}
        options={props.options ?? options}
        placeholder={props.placeholder}
        limit={props.limit}
      />
    );
    const input = screen.getByRole("textbox");
    return { ...result, input, onChange };
  };

  it("shows placeholder and syncs input with value prop", () => {
    const { input, rerender } = renderCombo({ value: "ams" });


    expect(input).toHaveAttribute("placeholder", "City (type to search)");


    expect((input as HTMLInputElement).value).toBe("Amsterdam");


    rerender(
      <CityCombo value="rot" onChange={vi.fn()} options={options} />
    );
    expect((screen.getByRole("textbox") as HTMLInputElement).value).toBe(
      "Rotterdam"
    );
  });

  it("filters options based on input and respects limit", () => {
    const { input } = renderCombo({ limit: 2 });

    fireEvent.change(input, { target: { value: "a" } });

    const listbox = screen.getByRole("listbox");
    expect(listbox).toBeInTheDocument();

    const items = screen.getAllByRole("option");
    expect(items.length).toBe(2); // limited to 2


    expect(items[0]).toHaveTextContent("Amsterdam");
  });

  it("shows 'No matches' when there are no matches", () => {
    const { input } = renderCombo();

    fireEvent.change(input, { target: { value: "zzz" } });

    const listbox = screen.getByRole("listbox");
    expect(listbox).toBeInTheDocument();
    expect(screen.getByText("No matches")).toBeInTheDocument();
  });

  it("calls onChange and closes list when an option is clicked", () => {
    const { input, onChange } = renderCombo();

    fireEvent.change(input, { target: { value: "am" } });

    const option = screen.getByText("Amsterdam");
    fireEvent.click(option);

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith("ams");


    expect(screen.queryByRole("listbox")).toBeNull();


    expect((screen.getByRole("textbox") as HTMLInputElement).value).toBe(
      "Amsterdam"
    );
  });

  it("closes dropdown when clicking outside", () => {
    const { input } = renderCombo();

    fireEvent.change(input, { target: { value: "a" } });
    expect(screen.getByRole("listbox")).toBeInTheDocument();

    fireEvent.mouseDown(document.body);

    expect(screen.queryByRole("listbox")).toBeNull();
  });

  it("supports keyboard navigation and Enter to select", () => {
    const { input, onChange } = renderCombo();

    fireEvent.change(input, { target: { value: "a" } });


    fireEvent.keyDown(input, { key: "ArrowDown", code: "ArrowDown" });

    const optionsEls = screen.getAllByRole("option");
    expect(optionsEls[0]).toHaveAttribute("aria-selected", "true");


    fireEvent.keyDown(input, { key: "Enter", code: "Enter" });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith("ams");
  });

  it("closes dropdown and clears active index on Escape", () => {
    const { input } = renderCombo();

    fireEvent.change(input, { target: { value: "a" } });
    expect(screen.getByRole("listbox")).toBeInTheDocument();

    fireEvent.keyDown(input, { key: "Escape", code: "Escape" });

    expect(screen.queryByRole("listbox")).toBeNull();
  });
});
