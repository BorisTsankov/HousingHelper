export type ListingDetails = {
  id: string;
  title: string;
  description?: string;
  canonicalUrl?: string;

  statusCode?: string;
  statusLabel?: string;

  address?: string;
  city?: string;
  postalCode?: string;
  country?: string;
  street?: string;
  houseNumber?: string;
  unit?: string;

  bedrooms?: number | null;
  bathrooms?: number | null;
  rooms?: number | null;
  areaM2?: number | null;
  energyLabel?: string | null;

  rent: { amount: number | null; currency: string; period: string } | null;
  deposit?: number | null;
  rentPeriodCode?: string | null;
  rentPeriodLabel?: string | null;

  propertyTypeCode?: string | null;
  propertyTypeLabel?: string | null;
  furnishingCode?: string | null;
  furnishingLabel?: string | null;

  availableFromIso?: string | null;
  availableUntilIso?: string | null;
  minimumLeaseMonths?: number | null;

  lat?: number | null;
  lon?: number | null;

  primaryPhotoUrl?: string | null;
  photosCount?: number | null;

  agencyId?: number | null;
  agencyName?: string | null;
  agencyWebsiteUrl?: string | null;
  agencyLogoUrl?: string | null;

  priceHistory?: { observedAtIso: string; amount: number | null }[];
};