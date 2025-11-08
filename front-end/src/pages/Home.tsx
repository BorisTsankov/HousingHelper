import React from "react";
import Page from "../components/layout/Page";
import { Section } from "../components/ui/Section";
import { SearchBar } from "../components/search/SearchBar";
import { SearchFiltersSmall } from "../components/search/SearchFiltersSmall";
import Navbar from "../components/layout/Navbar";
import Footer from "../components/layout/Footer";
import { PropertyGallery } from "../components/gallery/PropertyGallery";

import type { Filters } from "../types/filters";
import type { Property } from "../types/property";
import type { FilterGroup } from "../types/filterOptions";
import homePhoto from "../assets/home_photo.jpeg";

type ListingsResponse = { items: Property[] };

const Home: React.FC = () => {
  const [draftFilters, setDraftFilters] = React.useState<Filters>({});
  const [qDraft, setQDraft] = React.useState<string>("");

  const [properties, setProperties] = React.useState<Property[]>([]);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [error, setError] = React.useState<string | null>(null);

  const [options, setOptions] = React.useState<FilterGroup>({
    types: [], cities: [], priceBuckets: [], bedrooms: [], bathrooms: [], furnished: [], petsAllowed: []
  });

  const PAGE = 0;
  const SIZE = 3;

  // server-driven options (small scope)
  React.useEffect(() => {
    const c = new AbortController();
    (async () => {
      try {
        const res = await fetch("/api/listings/filters?scope=home", { signal: c.signal });
        if (res.ok) setOptions(await res.json());
      } catch { /* noop */ }
    })();
    return () => c.abort();
  }, []);

  // featured listings (no filters)
  React.useEffect(() => {
    const controller = new AbortController();
    (async () => {
      try {
        setLoading(true);
        setError(null);
        const url = `/api/listings?page=${PAGE}&size=${SIZE}`;
        const res = await fetch(url, { signal: controller.signal });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data: ListingsResponse = await res.json();
        setProperties(Array.isArray(data?.items) ? data.items.slice(0, 3) : []);
      } catch (e: any) {
        if (e?.name !== "AbortError") {
          setError(e?.message ?? "Failed to load listings");
          setProperties([]);
        }
      } finally {
        setLoading(false);
      }
    })();
    return () => controller.abort();
  }, []);

  const handleSearch = (text: string) => {
    setQDraft(text);

    const params = new URLSearchParams();
    const qTrimmed = text.trim();
    if (qTrimmed) params.set("q", qTrimmed);

    const { type, city, minPrice, maxPrice } = draftFilters;
    if (type) params.set("type", type);
    if (city) params.set("city", city);
    if (typeof minPrice === "number") params.set("minPrice", String(minPrice));
    if (typeof maxPrice === "number") params.set("maxPrice", String(maxPrice));

    const qs = params.toString();
    window.location.assign(`/listings${qs ? `?${qs}` : ""}`);
  };


  return (
    <Page>
      <Navbar />

<section
  className="relative w-full bg-cover bg-center bg-no-repeat bg-fixed"
  style={{ backgroundImage: `url(${homePhoto})` }}
>
  {/* overlay for contrast */}
  <div className="absolute inset-0 bg-black/45 sm:bg-black/40" />

  {/* content container */}
  <div className="relative mx-auto max-w-5xl px-4 sm:px-6 lg:px-8 py-12 sm:py-16 lg:py-20">
    <h1 className="text-white text-3xl sm:text-4xl font-bold text-center drop-shadow mb-6">
      Find Your Dream Home
    </h1>

    {/* glass card for filters */}
<div className="bg-transparent rounded-2xl shadow-none ring-0 p-5 sm:p-6 lg:p-8">
      <SearchBar onSearch={handleSearch} />

      <div className="mt-4">
        <SearchFiltersSmall
          value={draftFilters}
          onChange={setDraftFilters}
          options={options}
        />

      </div>
    </div>
  </div>

  {/* subtle bottom fade into page background */}
  <div className="pointer-events-none absolute inset-x-0 bottom-0 h-10 bg-gradient-to-b from-transparent to-white/90" />
</section>


      <Section title="Featured listings">
        <div className="mx-auto max-w-5xl px-4">
          {loading && <p>Loading...</p>}
          {!loading && error && <div className="text-red-700">{error}</div>}

          {!loading && !error && properties.length === 0 && (
            <div className="rounded-md border bg-gray-50 p-6 text-center">
              <p className="font-medium">No listings found.</p>
              <p className="text-sm opacity-70">Please try again later.</p>
            </div>
          )}

          {!loading && !error && properties.length > 0 && (
            <PropertyGallery items={properties} />
          )}
        </div>
      </Section>

      <Footer />
    </Page>
  );
};

export default Home;
