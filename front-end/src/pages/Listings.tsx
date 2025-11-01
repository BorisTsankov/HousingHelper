import React from "react";
import Page from "../components/layout/Page";
import { Section } from "../components/ui/Section";
import { SearchBar } from "../components/search/SearchBar";
import { SearchFilters } from "../components/search/SearchFilters";
import Navbar from "../components/layout/Navbar";
import Footer from "../components/layout/Footer";
import { PropertyGallery } from "../components/gallery/PropertyGallery";

import type { Filters } from "../types/filters";
import type { Property } from "../types/property";

const Listings: React.FC = () => {
  const [filters, setFilters] = React.useState<Filters>({});
  const [q, setQ] = React.useState<string>("");
  const [properties, setProperties] = React.useState<Property[]>([]);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [error, setError] = React.useState<string | null>(null);

  const [page, setPage] = React.useState<number>(0);
  const [size, setSize] = React.useState<number>(12);
  const [total, setTotal] = React.useState<number>(0);

  const priceToRange = React.useCallback((label?: string) => {
    switch (label) {
      case "$500 - $1,000":   return { minPrice: 500,  maxPrice: 1000 };
      case "$1,000 - $1,500": return { minPrice: 1000, maxPrice: 1500 };
      case "$2,500+":         return { minPrice: 2500, maxPrice: undefined };
      default:                return { minPrice: undefined, maxPrice: undefined };
    }
  }, []);

  const buildQuery = React.useCallback(() => {
    const p = new URLSearchParams();
    if (q.trim()) p.set("q", q.trim());

    const { type, price, location } = filters;
    if (type) p.set("type", type);
    if (location) p.set("city", location);

    const { minPrice, maxPrice } = priceToRange(price);
    if (typeof minPrice === "number") p.set("minPrice", String(minPrice));
    if (typeof maxPrice === "number") p.set("maxPrice", String(maxPrice));

    p.set("page", String(page));
    p.set("size", String(size));

    const qs = p.toString();
    return qs ? `?${qs}` : "";
  }, [q, filters, priceToRange, page, size]);

  const fetchListings = React.useCallback(() => {
    const controller = new AbortController();
    (async () => {
      try {
        setLoading(true);
        setError(null);

        const url = `/api/listings${buildQuery()}`;
        const res = await fetch(url, { signal: controller.signal });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const data: ListingsResponse = await res.json();
        setProperties(Array.isArray(data?.items) ? data.items : []);
        setTotal(typeof data?.total === "number" ? data.total : 0);
      } catch (e: any) {
        if (e?.name !== "AbortError") {
          setError(e?.message ?? "Failed to load listings");
          setProperties([]);
          setTotal(0);
        }
      } finally {
        setLoading(false);
      }
    })();
    return () => controller.abort();
  }, [buildQuery]);

  React.useEffect(() => {
    const cleanup = fetchListings();
    return cleanup;
  }, [fetchListings]);

  React.useEffect(() => { setPage(0); }, [q, filters]);

  const handleSearch = (text: string) => setQ(text);

  const resetAll = () => {
    setFilters({});
    setQ("");
    setPage(0);
  };

  const totalPages = Math.max(1, Math.ceil(total / size));
  const canPrev = page > 0;
  const canNext = page < totalPages - 1;

  return (
    <Page>
      <Navbar />

      <Section title="Find Your Dream Home" center>
        <div className="mx-auto max-w-4xl px-4">
          <SearchBar onSearch={handleSearch} />
          <div className="mt-4">
            <SearchFilters value={filters} onChange={setFilters} />
            <div className="mt-3 flex items-center justify-between">
              <div className="text-sm opacity-80">
                {loading ? "Loading…" : `Found ${total} result${total === 1 ? "" : "s"}`}
              </div>
              <div className="flex items-center gap-3">
                <label className="text-sm">
                  Per page{" "}
                  <select
                    className="border rounded-md px-2 py-1 text-sm"
                    value={size}
                    onChange={(e) => { setPage(0); setSize(Number(e.target.value)); }}
                    disabled={loading}
                  >
                    <option value={12}>12</option>
                    <option value={24}>24</option>
                    <option value={48}>48</option>
                  </select>
                </label>

                <button
                  type="button"
                  onClick={resetAll}
                  className="rounded-md border px-3 py-1.5 text-sm hover:bg-gray-50"
                  aria-label="Reset all filters and search"
                  title="Reset all"
                >
                  Reset filters
                </button>
              </div>
            </div>
          </div>
        </div>
      </Section>

      <Section title="Listings">
        <div className="mx-auto max-w-5xl px-4">
          {loading && <p>Loading...</p>}
          {!loading && error && <div className="text-red-700">{error}</div>}

          {!loading && !error && properties.length === 0 && (
            <div className="rounded-md border bg-gray-50 p-6 text-center">
              <p className="font-medium">No listings found.</p>
              <p className="text-sm opacity-70">Try adjusting filters or search terms.</p>
            </div>
          )}

          {!loading && !error && properties.length > 0 && (
            <>
              <PropertyGallery items={properties} />

              <div className="mt-6 flex items-center justify-center gap-2">
                <button
                  className="px-3 py-1 rounded-md border disabled:opacity-40"
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={!canPrev || loading}
                >
                  Prev
                </button>
                <span className="px-2 text-sm">
                  Page {page + 1} of {totalPages} · {total} result{total === 1 ? "" : "s"}
                </span>
                <button
                  className="px-3 py-1 rounded-md border disabled:opacity-40"
                  onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                  disabled={!canNext || loading}
                >
                  Next
                </button>
              </div>
            </>
          )}
        </div>
      </Section>

      <Footer />
    </Page>
  );
};

export default Listings;