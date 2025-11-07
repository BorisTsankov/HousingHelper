export type Property = {
  // --- legacy/basic (unchanged) ---
  id: string;
  title: string;
  image: string;
  price: string;       // display string
  location: string;

  // --- new richer fields (all optional to avoid breaking old code) ---
  displayPrice?: string;
  displayDeposit?: string;

  rentAmount?: number | null;
  rentPeriod?: string | null;
  deposit?: number | null;

  description?: string | null;
  status?: string | null;
  propertyType?: string | null;
  furnishingType?: string | null;
  energyLabel?: string | null;

  areaM2?: number | null;
  rooms?: number | null;
  bedrooms?: number | null;
  bathrooms?: number | null;

  availableFrom?: string | null;
  availableUntil?: string | null;
  minimumLeaseMonths?: number | null;

  country?: string | null;
  city?: string | null;
  postalCode?: string | null;
  street?: string | null;
  houseNumber?: string | null;
  unit?: string | null;

  lat?: number | null;
  lon?: number | null;

  photosCount?: number | null;
  canonicalUrl?: string | null;

  externalId?: string | null;
  source?: string | null;
};
