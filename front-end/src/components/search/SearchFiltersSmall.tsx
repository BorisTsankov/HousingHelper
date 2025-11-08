import React from "react";
import { Select } from "../ui/Select";
import type { Filters } from "../../types/filters";
import type { FilterGroup } from "../../types/filterOptions";
import { CityCombo } from "./CityCombo"; // <-- add this import
import type { Filters } from "../../types/filters";
import type { FilterGroup } from "../../types/filterOptions";
import { FILTER_FIELD_CLASS } from "../../styles/filterField";

type Props = { value: Filters; onChange: (f: Filters) => void; options: FilterGroup };

export const SearchFiltersSmall: React.FC<Props> = ({ value, onChange, options }) => {
  const set = (patch: Partial<Filters>) => onChange({ ...value, ...patch });
  const selectedPriceLabel =
    options.priceBuckets.find(b => b.min === value.minPrice && b.max === value.maxPrice)?.label ?? "";

  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
      <Select className={FILTER_FIELD_CLASS} value={value.type ?? ""} onChange={(e) => set({ type: e.target.value || undefined })}>
        <option value="">Property Type</option>
        {options.types.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </Select>

      <Select className={FILTER_FIELD_CLASS}
        value={selectedPriceLabel}
        onChange={(e) => {
          const bucket = options.priceBuckets.find(b => b.label === e.target.value);
          set({ minPrice: bucket?.min ?? undefined, maxPrice: bucket?.max ?? undefined });
        }}
      >
        <option value="">Price Range</option>
        {options.priceBuckets.map(b => <option key={b.label} value={b.label}>{b.label}</option>)}
      </Select>

      <CityCombo
              value={value.city}
              onChange={(v) => set({ city: v })}
              options={options.cities}
              placeholder="City (type to search)"
              limit={5}
            />
    </div>
  );
};