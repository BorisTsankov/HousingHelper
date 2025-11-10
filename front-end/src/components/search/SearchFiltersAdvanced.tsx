import React, { useMemo, useState } from "react";
import { Select } from "../ui/Select";
import { CityCombo } from "./CityCombo";
import type { Filters } from "../../types/filters";
import type { FilterGroup } from "../../types/filterOptions";
import { FILTER_FIELD_CLASS } from "../../styles/filterField";


type Props = { value: Filters; onChange: (f: Filters) => void; options: FilterGroup };

export const SearchFiltersAdvanced: React.FC<Props> = ({ value, onChange, options }) => {
  const set = (patch: Partial<Filters>) => onChange({ ...value, ...patch });
  const priceLabel =
    options.priceBuckets.find(b => b.min === value.minPrice && b.max === value.maxPrice)?.label ?? "";

  return (
    <div className="grid grid-cols-1 gap-3">
      {/* Common */}
      <Select className={FILTER_FIELD_CLASS} value={value.type ?? ""} onChange={(e)=>set({ type: e.target.value || undefined })}>
        <option value="">Property Type</option>
        {options.types.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </Select>

      <Select className={FILTER_FIELD_CLASS} value={priceLabel} onChange={(e)=> {
        const b = options.priceBuckets.find(x=>x.label===e.target.value);
        set({ minPrice: b?.min ?? undefined, maxPrice: b?.max ?? undefined });
      }}>
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

      <Select className={FILTER_FIELD_CLASS} value={String(value.bedroomsMin ?? "")} onChange={(e)=>set({ bedroomsMin: e.target.value ? Number(e.target.value) : undefined })}>
        <option value="">Bedrooms (min)</option>
        {options.bedrooms.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </Select>

      <Select className={FILTER_FIELD_CLASS} value={String(value.bathroomsMin ?? "")} onChange={(e)=>set({ bathroomsMin: e.target.value ? Number(e.target.value) : undefined })}>
        <option value="">Bathrooms (min)</option>
        {options.bathrooms.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </Select>

      <Select className={FILTER_FIELD_CLASS} value={value.furnished ?? ""} onChange={(e)=>set({ furnished: (e.target.value as any) || undefined })}>
        <option value="">Furnishing</option>
        {options.furnished.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </Select>

      <Select className={FILTER_FIELD_CLASS} value={value.petsAllowed ?? ""} onChange={(e)=>set({ petsAllowed: (e.target.value as any) || undefined })}>
        <option value="">Pets</option>
        {options.petsAllowed.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </Select>

      <input
        className={FILTER_FIELD_CLASS}
        type="number" placeholder="Area min (m²)"
        value={value.areaMin ?? ""} onChange={(e)=>set({ areaMin: e.target.value ? Number(e.target.value) : undefined })}
      />
      <input
        className={FILTER_FIELD_CLASS}
        type="number" placeholder="Area max (m²)"
        value={value.areaMax ?? ""} onChange={(e)=>set({ areaMax: e.target.value ? Number(e.target.value) : undefined })}
      />
      <input
        className={FILTER_FIELD_CLASS}
        type="date" placeholder="Available from"
        value={value.availableFrom ?? ""} onChange={(e)=>set({ availableFrom: e.target.value || undefined })}
      />
    </div>
  );
};
