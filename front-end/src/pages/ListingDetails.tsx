import React, { useEffect, useMemo, useState } from "react";
import { useParams, Link } from "react-router-dom";
import {
  MapPin,
  Bed,
  Bath,
  Ruler,
  Euro,
  ShieldCheck,
  Home as HomeIcon,
  ExternalLink,
  ImageIcon,
  Calendar,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import Navbar from "../components/layout/Navbar";
import type { Property } from "type";

const API_BASE = "/api/listings";

function formatMoney(n?: number | null) {
  if (n == null) return null;
  try {
    return new Intl.NumberFormat("de-DE", { style: "currency", currency: "EUR" }).format(n);
  } catch {
    return `${n}`;
  }
}

function joinAddress(p: Property) {
  const parts = [p.street, p.houseNumber, p.unit].filter(Boolean).join(" ");
  const cityLine = [p.postalCode, p.city].filter(Boolean).join(" ");
  const country = p.country ?? undefined;
  return [parts || undefined, cityLine || undefined, country].filter(Boolean).join(", ");
}

function numOrUndef(v?: number | null) {
  return v != null ? `${v}` : undefined;
}

function dateOrUndef(v?: string | null) {
  if (!v) return undefined;
  try {
    const d = new Date(v);
    return d.toLocaleDateString();
  } catch {
    return v;
  }
}

export default function ListingDetails() {
  const { id } = useParams<{ id: string }>();

  const [data, setData] = useState<Property | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let aborted = false;
    async function run() {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(`${API_BASE}/${id}`);
        if (!res.ok) {
          const text = await res.text();
          throw new Error(text || `HTTP ${res.status}`);
        }
        const json: Property = await res.json();
        if (!aborted) setData(json);
      } catch (e: any) {
        if (!aborted) setError(e?.message ?? "Failed to load listing");
      } finally {
        if (!aborted) setLoading(false);
      }
    }
    if (id) run();
    return () => {
      aborted = true;
    };
  }, [id]);

  const address = useMemo(() => (data ? joinAddress(data) : ""), [data]);

  // ✅ Always run this hook in the same place
  const photos: string[] = useMemo(() => {
    if (!data) return [];
    if (data.photoUrls && data.photoUrls.length > 0) return data.photoUrls;
    if (data.image) return [data.image];
    return [];
  }, [data]);

  // ✅ Also always in the same place
  const [currentIndex, setCurrentIndex] = useState(0);

  // Optional: reset index when listing changes
  useEffect(() => {
    setCurrentIndex(0);
  }, [data?.id]);

  const price = data?.displayPrice ?? data?.price ?? undefined;
  const deposit = data?.displayDeposit ?? formatMoney(data?.deposit ?? null) ?? undefined;

  if (loading) {
    return (
      <>
        <Navbar />
        <div className="mx-auto max-w-6xl p-4 animate-pulse">
          <div className="h-6 w-40 rounded bg-gray-200/80 mb-3" />
          <div className="h-9 w-80 rounded bg-gray-200/80 mb-4" />
          <div className="aspect-[16/10] w-full rounded-2xl bg-gray-200/80 mb-6" />
          <div className="h-24 rounded-2xl bg-gray-200/80" />
        </div>
      </>
    );
  }

  if (error) {
    return (
      <>
        <Navbar />
        <div className="mx-auto max-w-3xl p-4">
          <div className="rounded-2xl border border-red-200 bg-red-50 p-4">
            <h2 className="text-lg font-semibold text-red-700">Could not load listing</h2>
            <p className="text-red-700/80 mt-1">{error}</p>
            <Link to="/" className="inline-flex items-center gap-1 mt-3 underline">
              <HomeIcon className="h-4 w-4" /> Go back
            </Link>
          </div>
        </div>
      </>
    );
  }

  if (!data) return null;

  const hasPhotos = photos.length > 0;
  const mainPhoto = hasPhotos ? photos[Math.min(currentIndex, photos.length - 1)] : null;
  const photoCount = data.photosCount ?? photos.length;

  const goPrev = () => {
    if (!hasPhotos) return;
    setCurrentIndex((i) => (i - 1 + photos.length) % photos.length);
  };

  const goNext = () => {
    if (!hasPhotos) return;
    setCurrentIndex((i) => (i + 1) % photos.length);
  };

  return (
    <>
      <Navbar />
      <div className="min-h-screen bg-gradient-to-b from-white via-white to-gray-50">
        <div className="mx-auto max-w-6xl p-4 md:p-6">
          <div className="flex flex-col md:flex-row md:items-start gap-6">
            <div className="md:w-2/3 w-full">
              {/* 🖼️ PHOTO GALLERY */}
              <div className="relative overflow-hidden rounded-2xl shadow-sm bg-gray-100">
                {mainPhoto ? (
                  <>
                    <img
                      src={mainPhoto}
                      alt={data.title}
                      className="w-full h-full object-cover aspect-[16/10]"
                    />

                    {photos.length > 1 && (
                      <>
                        <button
                          type="button"
                          onClick={goPrev}
                          className="absolute left-3 top-1/2 -translate-y-1/2 inline-flex h-8 w-8 items-center justify-center rounded-full bg-black/50 text-white shadow-md hover:bg-black/70"
                        >
                          <ChevronLeft className="h-4 w-4" />
                        </button>
                        <button
                          type="button"
                          onClick={goNext}
                          className="absolute right-3 top-1/2 -translate-y-1/2 inline-flex h-8 w-8 items-center justify-center rounded-full bg-black/50 text-white shadow-md hover:bg-black/70"
                        >
                          <ChevronRight className="h-4 w-4" />
                        </button>
                      </>
                    )}

                    {photoCount > 0 && (
                      <div className="absolute bottom-3 right-3 inline-flex items-center gap-1 rounded-full bg-black/60 px-3 py-1 text-xs font-medium text-white backdrop-blur">
                        <ImageIcon className="h-3.5 w-3.5" /> {photoCount}
                      </div>
                    )}
                  </>
                ) : (
                  <div className="aspect-[16/10] flex items-center justify-center text-gray-400">
                    <ImageIcon className="h-8 w-8" />
                  </div>
                )}
              </div>

              {photos.length > 1 && (
                <div className="mt-3 flex gap-2 overflow-x-auto pb-1">
                  {photos.map((url, idx) => (
                    <button
                      key={url + idx}
                      type="button"
                      onClick={() => setCurrentIndex(idx)}
                      className={`relative h-16 w-24 flex-shrink-0 overflow-hidden rounded-xl border ${
                        idx === currentIndex ? "border-blue-500 ring-2 ring-blue-300" : "border-gray-200"
                      }`}
                    >
                      <img src={url} alt="" className="h-full w-full object-cover" />
                    </button>
                  ))}
                </div>
              )}

              <FactsBar
                price={price}
                areaM2={data?.areaM2}
                bedrooms={data?.bedrooms}
                bathrooms={data?.bathrooms}
              />
            </div>

            <aside className="md:w-1/3 w-full md:sticky md:top-6">
              <SectionCard className="p-5">
                <div className="flex items-start justify-between gap-3">
                  <h1 className="text-2xl font-semibold leading-tight tracking-tight">{data.title}</h1>
                  {data.status && (
                    <span className="inline-flex items-center rounded-full border px-2.5 py-1 text-xs font-medium text-gray-700">
                      <ShieldCheck className="mr-1 h-3.5 w-3.5" /> {data.status}
                    </span>
                  )}
                </div>
                <div className="mt-2 flex items-start gap-2 text-gray-600">
                  <MapPin className="mt-0.5 h-4 w-4 shrink-0" />
                  <span className="leading-relaxed">{address || data.location}</span>
                </div>

                <div className="mt-4 grid grid-cols-1 gap-2 text-sm">
                  {price && <Row label="Rent" value={price} />}
                  {data.rentPeriod && <Row label="Period" value={data.rentPeriod} />}
                  {deposit && <Row label="Deposit" value={deposit} />}
                  {data.minimumLeaseMonths && (
                    <Row label="Min. lease" value={`${data.minimumLeaseMonths} months`} />
                  )}
                  {data.availableFrom && (
                    <Row label="Available from" value={dateOrUndef(data.availableFrom)} />
                  )}
                  {data.availableUntil && (
                    <Row label="Available until" value={dateOrUndef(data.availableUntil)} />
                  )}
                </div>

                {data.canonicalUrl && (
                  <a
                    href={data.canonicalUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="mt-4 inline-flex w-full items-center justify-center gap-2 rounded-xl border px-4 py-2 text-sm font-medium hover:bg-gray-50"
                  >
                    <ExternalLink className="h-4 w-4" /> View Source Listing
                  </a>
                )}
              </SectionCard>
            </aside>
          </div>

          <SectionCard className="mt-6">
            <div className="flex items-center gap-2">
              <Calendar className="h-4 w-4 text-gray-500" />
              <h2 className="text-lg font-semibold">Details</h2>
            </div>

            <dl className="mt-4 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-x-6 gap-y-3">
              <DetailItem label="Property type" value={data.propertyType} />
              <DetailItem label="Furnishing" value={data.furnishingType} />
              <DetailItem label="Energy label" value={data.energyLabel} />
              <DetailItem label="Area" value={data.areaM2 ? `${data.areaM2} m²` : undefined} />
              <DetailItem label="Rooms" value={numOrUndef(data.rooms)} />
              <DetailItem label="Bedrooms" value={numOrUndef(data.bedrooms)} />
              <DetailItem label="Bathrooms" value={numOrUndef(data.bathrooms)} />
              <DetailItem label="Available from" value={dateOrUndef(data.availableFrom)} />
              <DetailItem label="Available until" value={dateOrUndef(data.availableUntil)} />
            </dl>
          </SectionCard>

          {data.description && (
            <SectionCard className="mt-6">
              <h2 className="text-lg font-semibold">Description</h2>
              <p className="mt-2 whitespace-pre-line leading-relaxed text-gray-700">
                {data.description}
              </p>
            </SectionCard>
          )}

          {data.lat != null && data.lon != null && (
            <SectionCard className="mt-6">
              <h2 className="text-lg font-semibold mb-3">Location</h2>
              <div className="flex items-start gap-2 text-gray-600 mb-3">
                <MapPin className="mt-0.5 h-4 w-4" />
                <span>{address || data.location}</span>
              </div>

              <iframe
                title="Google Maps"
                width="100%"
                height="350"
                style={{ border: 0, borderRadius: "1rem" }}
                loading="lazy"
                allowFullScreen
                src={`https://www.google.com/maps/embed/v1/place?key=AIzaSyAHr1tg1BF9jfqW-PnkQI0H79M7UPKuHp4&q=${data.lat},${data.lon}`}
              ></iframe>
            </SectionCard>
          )}
        </div>
      </div>
    </>
  );
}

function Row({ label, value }: { label: string; value?: string | number | null }) {
  if (value == null || value === "") return null;
  return (
    <div className="flex items-center justify-between gap-4">
      <span className="text-gray-500">{label}</span>
      <span className="font-medium">{value}</span>
    </div>
  );
}

function SectionCard({
  children,
  className = "",
}: React.PropsWithChildren<{ className?: string }>) {
  return <section className={`rounded-2xl border bg-white p-5 shadow-sm ${className}`}>{children}</section>;
}

function FactsBar({
  price,
  areaM2,
  bedrooms,
  bathrooms,
}: {
  price?: string | number | null;
  areaM2?: number | null;
  bedrooms?: number | null;
  bathrooms?: number | null;
}) {
  const items: Array<{ key: string; icon: React.ReactNode; label: string } | null> = [
    price != null && price !== ""
      ? { key: String(price), icon: <Euro className="h-4 w-4" />, label: `${price}` }
      : null,
    areaM2 ? { key: `area-${areaM2}`, icon: <Ruler className="h-4 w-4" />, label: `${areaM2} m²` } : null,
    bedrooms != null
      ? {
          key: `bed-${bedrooms}`,
          icon: <Bed className="h-4 w-4" />,
          label: `${bedrooms} bed${bedrooms === 1 ? "" : "s"}`,
        }
      : null,
    bathrooms != null
      ? {
          key: `bath-${bathrooms}`,
          icon: <Bath className="h-4 w-4" />,
          label: `${bathrooms} bath${bathrooms === 1 ? "" : "s"}`,
        }
      : null,
  ];

  const visible = items.filter(Boolean) as Array<{ key: string; icon: React.ReactNode; label: string }>;
  if (visible.length === 0) return null;

  return (
    <div className="mt-3 rounded-2xl border bg-white/80 backdrop-blur p-3">
      <ul className="flex flex-wrap items-center gap-2">
        {visible.map((it) => (
          <li
            key={it.key}
            className="inline-flex items-center gap-2 rounded-full border px-3 py-1 text-sm font-medium text-gray-700 hover:bg-gray-50"
          >
            {it.icon}
            <span>{it.label}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}

function DetailItem({ label, value }: { label: string; value?: string | number | null }) {
  if (value == null || value === "") return null;
  return (
    <div className="flex items-baseline gap-2">
      <dt className="text-sm text-gray-500">{label}</dt>
      <dd className="text-base font-medium">{value}</dd>
    </div>
  );
}
