import React from "react";
import Page from "../components/layout/Page";
import { Section } from "../components/ui/Section";
import { SearchBar } from "../components/search/SearchBar";
import Navbar from "../components/layout/Navbar";
import Footer from "../components/layout/Footer";
import { PropertyGallery } from "../components/gallery/PropertyGallery";
import FiltersPanel from "../components/layout/FiltersPanel";
import { SearchFiltersAdvanced } from "../components/search/SearchFiltersAdvanced";
import { useNavigate, useSearchParams } from "react-router-dom";
import MapWithListings from "../components/listings/MapWithListings";

import type { Filters } from "../types/filters";
import type { Property } from "../types/property";
import type { FilterGroup } from "../types/filterOptions";

type ListingsResponse = {
  items: Property[];
  total?: number;
  page?: number;
  pageSize?: number;
  totalPages?: number;
};

const Listings: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();

  const [filters, setFilters] = React.useState<Filters>({});
  const [options, setOptions] = React.useState<FilterGroup>({
    types: [],
    cities: [],
    priceBuckets: [],
    bedrooms: [],
    bathrooms: [],
    furnished: [],
    petsAllowed: [],
  });

  const [q, setQ] = React.useState<string>("");

  const [bbox, setBbox] = React.useState<{
    north: number;
    south: number;
    east: number;
    west: number;
  } | null>(null);

  const [properties, setProperties] = React.useState<Property[]>([]);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [error, setError] = React.useState<string | null>(null);

  const [page, setPage] = React.useState<number>(() => {
    const p = Number(searchParams.get("page"));
    return Number.isFinite(p) && p >= 0 ? p : 0;
  });
  const [size, setSize] = React.useState<number>(() => {
    const s = Number(searchParams.get("size"));
    return Number.isFinite(s) && s > 0 ? s : 12;
  });
  const [total, setTotal] = React.useState<number>(0);

  const [drawerOpen, setDrawerOpen] = React.useState(false);
  const [showMap, setShowMap] = React.useState<boolean>(
    () => searchParams.get("view") === "map"
  );

  const [previewId, setPreviewId] = React.useState<string | number | null>(null);
  const previewItem = React.useMemo(
    () => properties.find((p) => String(p.id) === String(previewId)) || null,
    [properties, previewId]
  );

  const onBoundsChange = React.useCallback(
    (bbox: { north: number; south: number; east: number; west: number }) => {
      // you can react to map bounds here if you want
    },
    []
  );

  // Load filter options
  React.useEffect(() => {
    const c = new AbortController();
    (async () => {
      try {
        const res = await fetch("/api/listings/filters?scope=listings", {
          signal: c.signal,
        });
        if (res.ok) setOptions(await res.json());
      } catch {
        /* noop */
      }
    })();
    return () => c.abort();
  }, []);

  // Build query string for API
  const buildApiQuery = React.useCallback(() => {
    const p = new URLSearchParams();
    if (q.trim()) p.set("q", q.trim());

    const {
      type,
      city,
      minPrice,
      maxPrice,
      bedroomsMin,
      bathroomsMin,
      furnished,
      petsAllowed,
      areaMin,
      areaMax,
      availableFrom,
    } = filters;

    if (type) p.set("type", type);
    if (city) p.set("city", city);
    if (Number.isFinite(minPrice as number))
      p.set("minPrice", String(minPrice));
    if (Number.isFinite(maxPrice as number))
      p.set("maxPrice", String(maxPrice));
    if (Number.isFinite(bedroomsMin as number))
      p.set("bedroomsMin", String(bedroomsMin));
    if (Number.isFinite(bathroomsMin as number))
      p.set("bathroomsMin", String(bathroomsMin));
    if (furnished) p.set("furnished", furnished);
    if (petsAllowed) p.set("petsAllowed", petsAllowed);
    if (Number.isFinite(areaMin as number)) p.set("areaMin", String(areaMin));
    if (Number.isFinite(areaMax as number)) p.set("areaMax", String(areaMax));
    if (availableFrom) p.set("availableFrom", availableFrom);

    if (showMap && bbox) {
      p.set("north", String(bbox.north));
      p.set("south", String(bbox.south));
      p.set("east", String(bbox.east));
      p.set("west", String(bbox.west));
    }

    p.set("page", String(page));
    p.set("size", String(size));
    const qs = p.toString();
    return qs ? `?${qs}` : "";
  }, [q, filters, page, size, showMap, bbox]);

  // Sync URL search params
  const updateSearchParams = React.useCallback(
    (next: Partial<{ q: string; filters: Filters; page: number; size: number }>) => {
      const p = new URLSearchParams(searchParams.toString());

      if (next.q !== undefined) {
        const val = next.q.trim();
        if (val) p.set("q", val);
        else p.delete("q");
      }

      const f = next.filters ?? filters;
      const pairs: Array<[string, unknown]> = [
        ["type", f.type],
        ["city", f.city],
        ["minPrice", f.minPrice],
        ["maxPrice", f.maxPrice],
        ["bedroomsMin", f.bedroomsMin],
        ["bathroomsMin", f.bathroomsMin],
        ["furnished", f.furnished],
        ["petsAllowed", f.petsAllowed],
        ["areaMin", f.areaMin],
        ["areaMax", f.areaMax],
        ["availableFrom", f.availableFrom],
      ];
      for (const [k, v] of pairs) {
        if (v === undefined || v === "" || v === null) p.delete(k);
        else p.set(k, String(v));
      }

      const np = next.page ?? page;
      const ns = next.size ?? size;
      p.set("page", String(np));
      p.set("size", String(ns));

      setSearchParams(p, { replace: false });
    },
    [filters, page, size, searchParams, setSearchParams]
  );

  // Hydrate state from URL
  React.useEffect(() => {
    const qParam = searchParams.get("q") || "";
    setQ(qParam);

    const num = (k: string) => {
      const v = searchParams.get(k);
      return v ? Number(v) : undefined;
    };

    setFilters({
      type: searchParams.get("type") || undefined,
      city: searchParams.get("city") || undefined,
      minPrice: num("minPrice"),
      maxPrice: num("maxPrice"),
      bedroomsMin: num("bedroomsMin"),
      bathroomsMin: num("bathroomsMin"),
      furnished: (searchParams.get("furnished") as any) || undefined,
      petsAllowed: (searchParams.get("petsAllowed") as any) || undefined,
      areaMin: num("areaMin"),
      areaMax: num("areaMax"),
      availableFrom: searchParams.get("availableFrom") || undefined,
    });

    const p = Number(searchParams.get("page"));
    const s = Number(searchParams.get("size"));
    if (Number.isFinite(p) && p >= 0) setPage(p);
    if (Number.isFinite(s) && s > 0) setSize(s);
  }, [searchParams]);

  // Fetch listings
  const fetchListings = React.useCallback(() => {
    const controller = new AbortController();
    (async () => {
      try {
        setLoading(true);
        setError(null);
        const url = `/api/listings${buildApiQuery()}`;
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
  }, [buildApiQuery]);

  React.useEffect(() => {
    const cleanup = fetchListings();
    return cleanup;
  }, [fetchListings]);

  // ✅ No more "reset page on q/filters" effect here – we do it explicitly in handlers

  const handleSearch = (text: string) => {
    setQ(text);
    setPage(0);
    updateSearchParams({ q: text, page: 0 });
  };

  const resetAll = () => {
    setFilters({});
    setQ("");
    setPage(0);
    updateSearchParams({ q: "", filters: {}, page: 0 });
  };

  const totalPages = Math.max(1, Math.ceil(total / size));

  const GallerySkeleton = () => (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 animate-pulse">
      {Array.from({ length: size }).map((_, i) => (
        <div key={i} className="h-64 rounded-xl bg-gray-100" />
      ))}
    </div>
  );

  const buildDetailsHref = (id: string | number) => `/listings/${id}`;

  return (
    <Page>
      <Navbar />

      <Section title="">
        <div className="relative">
          {/* Fixed left rail */}
          <div
            className="hidden md:block fixed left-0 bottom-0 w-[320px] pl-4 pr-2"
            style={{ top: "var(--nav-offset, 80px)" }}
            aria-hidden={false}
          >
            <FiltersPanel className="h-full overflow-y-auto">
              <div className="mb-2">
                <SearchBar
                  onSearch={(t) => {
                    setQ(t);
                    setPage(0);
                    updateSearchParams({ q: t, page: 0 });
                  }}
                  defaultValue={q}
                  className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <SearchFiltersAdvanced
                value={filters}
                onChange={(f) => {
                  setFilters(f);
                  setPage(0);
                  updateSearchParams({ filters: f, page: 0 });
                }}
                options={options}
              />

              <div className="mt-4 flex items-center justify-between border-t pt-4">
                <button
                  type="button"
                  onClick={resetAll}
                  className="rounded-md border px-3 py-1.5 text-sm hover:bg-gray-50 active:scale-[0.99] transition"
                >
                  Reset
                </button>
                <div className="text-xs md:text-sm opacity-80" aria-live="polite">
                  {loading
                    ? "Loading…"
                    : `Found ${total} result${total === 1 ? "" : "s"}`}
                </div>
              </div>
            </FiltersPanel>
          </div>

          <div className="mx-auto max-w-6xl px-4 md:ml-[320px]">
            <div className="mb-3 flex items-center justify-between">
              {/* Mobile Filters button (drawer) */}
              <button
                className="md:hidden rounded-md border px-3 py-2 text-sm"
                onClick={() => setDrawerOpen(true)}
              >
                Filters
              </button>

              <div className="ml-auto flex gap-2">
                <button
                  className={`rounded-md border px-3 py-2 text-sm ${
                    !showMap ? "bg-black text-white" : ""
                  }`}
                  onClick={() => {
                    setShowMap(false);
                    const sp = new URLSearchParams(searchParams);
                    sp.delete("view");
                    setSearchParams(sp);
                  }}
                  aria-pressed={!showMap}
                >
                  List
                </button>
                <button
                  className={`rounded-md border px-3 py-2 text-sm ${
                    showMap ? "bg-black text-white" : ""
                  }`}
                  onClick={() => {
                    setShowMap(true);
                    const sp = new URLSearchParams(searchParams);
                    sp.set("view", "map");
                    setSearchParams(sp);
                  }}
                  aria-pressed={showMap}
                >
                  Map
                </button>
              </div>
            </div>

            {drawerOpen && (
              <div className="fixed inset-0 z-50 md:hidden">
                <div
                  className="absolute inset-0 bg-black/30"
                  onClick={() => setDrawerOpen(false)}
                />
                <div className="absolute inset-y-0 left-0 w-80 max-w-[85vw] bg-white shadow-2xl p-4 overflow-y-auto rounded-r-2xl">
                  <div className="mb-3 flex items-center justify-between">
                    <h2 className="text-base font-semibold">Search &amp; Filters</h2>
                    <button
                      className="rounded-md border px-2 py-1 text-sm hover:bg-gray-50"
                      onClick={() => setDrawerOpen(false)}
                    >
                      Close
                    </button>
                  </div>

                  <div className="mb-3">
                    <SearchBar
                      onSearch={(t) => {
                        handleSearch(t);
                        setDrawerOpen(false);
                      }}
                      defaultValue={q}
                      className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500"
                    />
                  </div>

                  <SearchFiltersAdvanced
                    value={filters}
                    onChange={(f) => {
                      setFilters(f);
                      setPage(0);
                      updateSearchParams({ filters: f, page: 0 });
                    }}
                    options={options}
                  />

                  <div className="mt-3 flex items-center justify-between">
                    <button
                      type="button"
                      onClick={resetAll}
                      className="rounded-md border px-3 py-1.5 text-sm hover:bg-gray-50"
                    >
                      Reset
                    </button>
                    <button
                      type="button"
                      onClick={() => setDrawerOpen(false)}
                      className="rounded-md bg-black text-white px-3 py-1.5 text-sm hover:opacity-90"
                    >
                      Apply
                    </button>
                  </div>
                </div>
              </div>
            )}

            {!showMap ? (
              <div>
                {loading ? (
                  <GallerySkeleton />
                ) : error ? (
                  <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center text-red-700">
                    <p className="font-medium">Something went wrong</p>
                    <p className="text-sm opacity-80 mt-1">{error}</p>
                  </div>
                ) : properties.length === 0 ? (
                  <div className="rounded-2xl border bg-gradient-to-b from-white to-gray-50 p-8 text-center">
                    <p className="font-semibold text-gray-900">No listings found</p>
                    <p className="text-sm text-gray-600 mt-1">
                      Try adjusting search or filters. You can also hit{" "}
                      <span className="font-medium">Reset</span>.
                    </p>
                  </div>
                ) : (
                  <>
                    <PropertyGallery items={properties} />
                    <div className="mt-6 flex items-center justify-center gap-2">
                      <button
                        className="px-3 py-1.5 rounded-full border hover:bg-gray-50 disabled:opacity-40"
                        onClick={() => {
                          const nextPage = Math.max(0, page - 1);
                          setPage(nextPage);
                          updateSearchParams({ page: nextPage });
                        }}
                        disabled={page <= 0 || loading}
                      >
                        Prev
                      </button>
                      <span className="px-3 py-1.5 rounded-full text-sm bg-gray-100">
                        Page {page + 1} of {totalPages} · {total} result
                        {total === 1 ? "" : "s"}
                      </span>
                      <button
                        className="px-3 py-1.5 rounded-full border hover:bg-gray-50 disabled:opacity-40"
                        onClick={() => {
                          const nextPage = Math.min(totalPages - 1, page + 1);
                          setPage(nextPage);
                          updateSearchParams({ page: nextPage });
                        }}
                        disabled={page >= totalPages - 1 || loading}
                      >
                        Next
                      </button>
                    </div>
                  </>
                )}
              </div>
            ) : (
              <div className="relative">
                <MapWithListings
                  items={properties
                    .filter(
                      (p) =>
                        typeof p.lat === "number" && typeof p.lon === "number"
                    )
                    .map((p) => ({
                      id: p.id,
                      title: p.title,
                      lat: p.lat!,
                      lon: p.lon!,
                      price: (p as any).displayPrice ?? p.price ?? null,
                      image: p.image ?? null,
                      city: p.city ?? null,
                    }))}
                  onBoundsChange={(b) => setBbox(b)}
                  onPreview={(id) => setPreviewId(id)}
                  getDetailsHref={(pp) => buildDetailsHref(pp.id)}
                  className="w-full h-[70vh] md:h-[calc(100vh-var(--nav-offset,80px)-64px)] rounded-xl overflow-hidden"
                />

                {previewItem && (
                  <div className="absolute right-4 top-4 z-[500] w-80 max-w-[90vw] rounded-2xl bg-white shadow-xl border overflow-hidden">
                    {previewItem.image && (
                      <img
                        src={previewItem.image}
                        alt={previewItem.title || "Listing"}
                        className="w-full h-40 object-cover"
                      />
                    )}
                    <div className="p-3">
                      <div className="text-sm font-semibold line-clamp-2">
                        {previewItem.title || "Untitled"}
                      </div>
                      {previewItem.city && (
                        <div className="text-xs text-gray-600 mt-0.5">
                          {previewItem.city}
                        </div>
                      )}
                      {previewItem.price != null && (
                        <div className="text-sm font-medium mt-1">
                          {String(
                            (previewItem as any).displayPrice ??
                              previewItem.price
                          )}
                        </div>
                      )}
                      <div className="mt-3 flex gap-2">
                        <button
                          className="rounded-md border px-3 py-1.5 text-sm hover:bg-gray-50"
                          onClick={() => setPreviewId(null)}
                        >
                          Close
                        </button>
                        <button
                          className="ml-auto rounded-md bg-black text-white px-3 py-1.5 text-sm hover:opacity-90"
                          onClick={() =>
                            navigate(buildDetailsHref(previewItem.id))
                          }
                        >
                          Open details
                        </button>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </Section>

      <Footer />
    </Page>
  );
};

export default Listings;
