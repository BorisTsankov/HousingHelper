export type FilterOption = { label: string; value: string };
export type PriceBucket = { label: string; min?: number | null; max?: number | null };

export type FilterGroup = {
  types: FilterOption[];
  cities: FilterOption[];
  priceBuckets: PriceBucket[];
  bedrooms: FilterOption[];
  bathrooms: FilterOption[];
  furnished: FilterOption[];
  petsAllowed: FilterOption[];
};