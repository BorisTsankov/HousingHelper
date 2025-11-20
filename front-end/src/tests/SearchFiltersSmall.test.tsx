import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";



vi.mock("../styles/filterField", () => ({
  FILTER_FIELD_CLASS: "filter-field",
}));


vi.mock("../components/ui/Select", () => ({
  Select: ({ children, ...props }: any) => <select {...props}>{children}</select>,
}));


vi.mock("../components/search/CityCombo", () => ({
  CityCombo: (props: any) => (
    <input
      data-testid="city-combo-input"
      value={props.value ?? ""}
      placeholder={props.placeholder}
      onChange={(e) => props.onChange(e.target.value || undefined)}
    />
  ),
}));

import { SearchFiltersSmall } from "../components/search/SearchFiltersSmall";


type Filters = {
  type?: string;
  minPrice?: number;
  maxPrice?: number;
  city?: string;
};

type FilterOption = { value: string; label: string };
type PriceBucket = { label: string; min?: number; max?: number };

type FilterGroup = {
  types: FilterOption[];
  priceBuckets: PriceBucket[];
  cities: { value: string; label: string }[];
};

const baseFilters: Filters = {
  type: "",
  minPrice: undefined,
  maxPrice: undefined,
  city: undefined,
};

const options: FilterGroup = {
  types: [
    { value: "apartment", label: "Apartment" },
    { value: "studio", label: "Studio" },
  ],
  priceBuckets: [
    { label: "€0 - €1000", min: 0, max: 1000 },
    { label: "€1000 - €2000", min: 1000, max: 2000 },
  ],
  cities: [
    { value: "ams", label: "Amsterdam" },
    { value: "eind", label: "Eindhoven" },
  ],
};

describe("SearchFiltersSmall", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const setup = (overrideFilters: Partial<Filters> = {}) => {
    const filters: Filters = { ...baseFilters, ...overrideFilters };
    const onChange = vi.fn();

    const utils = render(
      <SearchFiltersSmall value={filters} onChange={onChange} options={options} />
    );

    return { ...utils, filters, onChange };
  };

  it("sets price select value based on minPrice/maxPrice", () => {
    setup({ minPrice: 1000, maxPrice: 2000 });

    const pricePlaceholder = screen.getByText("Price Range");
    const priceSelect = pricePlaceholder.closest("select") as HTMLSelectElement;

    expect(priceSelect.value).toBe("€1000 - €2000");
  });

  it("updates type when 'Property Type' select changes", () => {
    const { filters, onChange } = setup();

    const typePlaceholder = screen.getByText("Property Type");
    const typeSelect = typePlaceholder.closest("select") as HTMLSelectElement;

    fireEvent.change(typeSelect, { target: { value: "apartment" } });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith({
      ...filters,
      type: "apartment",
    });
  });

  it("updates minPrice/maxPrice when price bucket changes", () => {
    const { filters, onChange } = setup();

    const pricePlaceholder = screen.getByText("Price Range");
    const priceSelect = pricePlaceholder.closest("select") as HTMLSelectElement;

    fireEvent.change(priceSelect, { target: { value: "€0 - €1000" } });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith({
      ...filters,
      minPrice: 0,
      maxPrice: 1000,
    });
  });

  it("passes city value to CityCombo and merges city on change", () => {
    const { filters, onChange } = setup({ city: "ams" });

    const cityInput = screen.getByTestId("city-combo-input") as HTMLInputElement;


    expect(cityInput.value).toBe("ams");


    fireEvent.change(cityInput, { target: { value: "eind" } });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith({
      ...filters,
      city: "eind",
    });
  });
});
