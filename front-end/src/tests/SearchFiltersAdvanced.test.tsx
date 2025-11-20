import React from "react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";


vi.mock("../styles/filterField", () => ({
  FILTER_FIELD_CLASS: "filter-field",
}));


vi.mock("../components/ui/Select", () => ({
  Select: ({ children, ...props }: any) => (
    <select {...props}>{children}</select>
  ),
}));


vi.mock("../components/search/CityCombo", () => ({
  CityCombo: (props: any) => (
    <div>
      <input
        data-testid="city-combo-input"
        value={props.value ?? ""}
        placeholder={props.placeholder}
        onChange={(e) => props.onChange(e.target.value || undefined)}
      />
    </div>
  ),
}));

import { SearchFiltersAdvanced } from "../components/search/SearchFiltersAdvanced";


type Filters = {
  type?: string;
  minPrice?: number;
  maxPrice?: number;
  city?: string;
  bedroomsMin?: number;
  bathroomsMin?: number;
  furnished?: string;
  petsAllowed?: string;
  areaMin?: number;
  areaMax?: number;
  availableFrom?: string;
};

type FilterOption = { value: string; label: string };
type PriceBucket = { label: string; min?: number; max?: number };

type FilterGroup = {
  types: FilterOption[];
  priceBuckets: PriceBucket[];
  cities: { value: string; label: string }[];
  bedrooms: FilterOption[];
  bathrooms: FilterOption[];
  furnished: FilterOption[];
  petsAllowed: FilterOption[];
};

const baseFilters: Filters = {
  type: "",
  minPrice: undefined,
  maxPrice: undefined,
  city: undefined,
  bedroomsMin: undefined,
  bathroomsMin: undefined,
  furnished: "",
  petsAllowed: "",
  areaMin: undefined,
  areaMax: undefined,
  availableFrom: "",
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
  bedrooms: [
    { value: "1", label: "1+" },
    { value: "2", label: "2+" },
  ],
  bathrooms: [
    { value: "1", label: "1+" },
    { value: "2", label: "2+" },
  ],
  furnished: [
    { value: "yes", label: "Furnished" },
    { value: "no", label: "Unfurnished" },
  ],
  petsAllowed: [
    { value: "yes", label: "Pets allowed" },
    { value: "no", label: "No pets" },
  ],
};

describe("SearchFiltersAdvanced", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const setup = (overrideFilters: Partial<Filters> = {}) => {
    const filters: Filters = { ...baseFilters, ...overrideFilters };
    const onChange = vi.fn();

    const utils = render(
      <SearchFiltersAdvanced
        value={filters}
        onChange={onChange}
        options={options}
      />
    );

    return { ...utils, filters, onChange };
  };

  it("shows the correct price label based on minPrice/maxPrice", () => {
    const { container } = setup({ minPrice: 1000, maxPrice: 2000 });


    const priceOption = screen.getByText("Price Range");
    const priceSelect = priceOption.closest("select") as HTMLSelectElement;


    expect(priceSelect.value).toBe("€1000 - €2000");
  });

  it("updates type when 'Property Type' select changes", () => {
    const { filters, onChange } = setup();

    const typeOption = screen.getByText("Property Type");
    const typeSelect = typeOption.closest("select") as HTMLSelectElement;

    fireEvent.change(typeSelect, { target: { value: "apartment" } });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith({
      ...filters,
      type: "apartment",
    });
  });

  it("updates minPrice/maxPrice when price range changes", () => {
    const { filters, onChange } = setup();

    const priceOption = screen.getByText("Price Range");
    const priceSelect = priceOption.closest("select") as HTMLSelectElement;

    fireEvent.change(priceSelect, { target: { value: "€1000 - €2000" } });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith({
      ...filters,
      minPrice: 1000,
      maxPrice: 2000,
    });
  });

  it("passes city value to CityCombo and updates city via onChange", () => {
    const { filters, onChange } = setup({ city: "ams" });

    const cityInput = screen.getByTestId(
      "city-combo-input"
    ) as HTMLInputElement;


    expect(cityInput.value).toBe("ams");


    fireEvent.change(cityInput, { target: { value: "eind" } });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith({
      ...filters,
      city: "eind",
    });
  });

  it("updates bedroomsMin when 'Bedrooms (min)' changes", () => {
    const { filters, onChange } = setup();

    const bedroomsOption = screen.getByText("Bedrooms (min)");
    const bedroomsSelect = bedroomsOption.closest("select") as HTMLSelectElement;

    fireEvent.change(bedroomsSelect, { target: { value: "2" } });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith({
      ...filters,
      bedroomsMin: 2,
    });
  });

  it("updates bathroomsMin when 'Bathrooms (min)' changes", () => {
    const { filters, onChange } = setup();

    const bathroomsOption = screen.getByText("Bathrooms (min)");
    const bathroomsSelect = bathroomsOption.closest(
      "select"
    ) as HTMLSelectElement;

    fireEvent.change(bathroomsSelect, { target: { value: "1" } });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith({
      ...filters,
      bathroomsMin: 1,
    });
  });

  it("updates furnished and petsAllowed when selects change", () => {
    const { filters, onChange } = setup();

    const furnishingOption = screen.getByText("Furnishing");
    const furnishingSelect = furnishingOption.closest(
      "select"
    ) as HTMLSelectElement;

    const petsOption = screen.getByText("Pets");
    const petsSelect = petsOption.closest("select") as HTMLSelectElement;

    fireEvent.change(furnishingSelect, { target: { value: "yes" } });
    fireEvent.change(petsSelect, { target: { value: "no" } });


    expect(onChange).toHaveBeenCalledTimes(2);

    expect(onChange).toHaveBeenNthCalledWith(1, {
      ...filters,
      furnished: "yes",
    });

    expect(onChange).toHaveBeenNthCalledWith(2, {
      ...filters,
      petsAllowed: "no",
    });
  });

  it("updates areaMin and areaMax when inputs change", () => {
    const { filters, onChange } = setup();

    const areaMinInput = screen.getByPlaceholderText(
      "Area min (m²)"
    ) as HTMLInputElement;
    const areaMaxInput = screen.getByPlaceholderText(
      "Area max (m²)"
    ) as HTMLInputElement;

    fireEvent.change(areaMinInput, { target: { value: "50" } });
    fireEvent.change(areaMaxInput, { target: { value: "80" } });

    expect(onChange).toHaveBeenCalledTimes(2);

    expect(onChange).toHaveBeenNthCalledWith(1, {
      ...filters,
      areaMin: 50,
    });

    expect(onChange).toHaveBeenNthCalledWith(2, {
      ...filters,
      areaMax: 80,
    });
  });

  it("updates availableFrom when date input changes", () => {
    const { filters, onChange } = setup();

    const dateInput = screen.getByPlaceholderText(
      "Available from"
    ) as HTMLInputElement;

    fireEvent.change(dateInput, { target: { value: "2025-12-01" } });

    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith({
      ...filters,
      availableFrom: "2025-12-01",
    });
  });
});
