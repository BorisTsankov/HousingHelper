export type Filters = {
  // shared (home + listings)
  type?: string;
  city?: string;
  minPrice?: number;
  maxPrice?: number;

  // advanced (listings only)
  bedroomsMin?: number;
  bathroomsMin?: number;
  furnished?: "furnished" | "semi-furnished" | "unfurnished" | "";
  petsAllowed?: "yes" | "no" | "";
  areaMin?: number;
  areaMax?: number;
  availableFrom?: string; // ISO date YYYY-MM-DD
};