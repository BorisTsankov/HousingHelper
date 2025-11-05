export type ListingCard = {
  id: string;
  title: string;
  primaryImageUrl: string;
  cityOrCountry: string;
  rent: { amount: number | null; currency: string; period: string };
  bedrooms?: number | null;
  bathrooms?: number | null;
  areaM2?: number | null;
  propertyTypeCode?: string | null;
  propertyTypeLabel?: string | null;
  furnishingCode?: string | null;
  furnishingLabel?: string | null;
  energyLabel?: string | null;
  lastSeenAtIso?: string | null;
};