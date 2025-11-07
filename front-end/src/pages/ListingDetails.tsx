import React, { useEffect, useMemo, useState } from "react";
import { useParams, Link } from "react-router-dom";
import type {Property} from "type";


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
  const price = data?.displayPrice ?? data?.price ?? undefined;
  const deposit = data?.displayDeposit ?? formatMoney(data?.deposit ?? null) ?? undefined;

  if (loading) {
    return (
      <div className="mx-auto max-w-6xl p-4 animate-pulse">
        <div className="h-8 w-64 bg-gray-200 rounded mb-4" />
        <div className="h-64 w-full bg-gray-200 rounded mb-6" />
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="h-24 bg-gray-200 rounded" />
          <div className="h-24 bg-gray-200 rounded" />
          <div className="h-24 bg-gray-200 rounded" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="mx-auto max-w-3xl p-4">
        <div className="rounded-2xl border border-red-200 bg-red-50 p-4">
          <h2 className="text-lg font-semibold text-red-700">Could not load listing</h2>
          <p className="text-red-700/80 mt-1">{error}</p>
          <Link to="/" className="inline-block mt-3 underline">Go back</Link>
        </div>
      </div>
    );
  }

  if (!data) return null;

  return (
    <div className="mx-auto max-w-6xl p-4">
      <div className="mb-3 flex items-center gap-2 text-sm text-gray-500">
        <Link to="/" className="underline">Home</Link>
        <span>/</span>
        <span>Listing #{data.id}</span>
      </div>

      <div className="flex flex-col md:flex-row md:items-start gap-6">
        {/* Image */}
        <div className="md:w-2/3 w-full">
          <img
            src={data.image}
            alt={data.title}
            className="w-full h-auto rounded-2xl shadow-sm object-cover"
          />
          {data.photosCount ? (
            <div className="mt-2 text-xs text-gray-500">Photos: {data.photosCount}</div>
          ) : null}
        </div>

        {/* Summary card */}
        <aside className="md:w-1/3 w-full">
          <div className="rounded-2xl border p-4 shadow-sm">
            <h1 className="text-2xl font-semibold leading-tight">{data.title}</h1>
            <div className="mt-2 text-gray-600">{address || data.location}</div>

            <div className="mt-4 grid grid-cols-1 gap-2 text-sm">
              {price && (
                <div className="flex items-center justify-between">
                  <span className="text-gray-500">Rent</span>
                  <span className="font-medium">{price}</span>
                </div>
              )}
              {data.rentPeriod && (
                <div className="flex items-center justify-between">
                  <span className="text-gray-500">Period</span>
                  <span>{data.rentPeriod}</span>
                </div>
              )}
              {deposit && (
                <div className="flex items-center justify-between">
                  <span className="text-gray-500">Deposit</span>
                  <span>{deposit}</span>
                </div>
              )}
              {data.status && (
                <div className="flex items-center justify-between">
                  <span className="text-gray-500">Status</span>
                  <span>{data.status}</span>
                </div>
              )}
            </div>

            {data.canonicalUrl && (
              <a
                href={data.canonicalUrl}
                target="_blank"
                rel="noreferrer"
                className="mt-4 inline-flex w-full items-center justify-center rounded-xl border px-4 py-2 text-sm font-medium hover:bg-gray-50"
              >
                View Source Listing
              </a>
            )}
          </div>
        </aside>
      </div>

      {/* Details grid */}
      <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-4">
        <DetailCard label="Property type" value={data.propertyType} />
        <DetailCard label="Furnishing" value={data.furnishingType} />
        <DetailCard label="Energy label" value={data.energyLabel} />
        <DetailCard label="Area" value={data.areaM2 ? `${data.areaM2} m²` : undefined} />
        <DetailCard label="Rooms" value={numOrDash(data.rooms)} />
        <DetailCard label="Bedrooms" value={numOrDash(data.bedrooms)} />
        <DetailCard label="Bathrooms" value={numOrDash(data.bathrooms)} />
        <DetailCard label="Available from" value={dateOrDash(data.availableFrom)} />
        <DetailCard label="Available until" value={dateOrDash(data.availableUntil)} />
        <DetailCard label="Min. lease" value={data.minimumLeaseMonths ? `${data.minimumLeaseMonths} months` : undefined} />
      </div>

      {data.description && (
        <div className="mt-6 rounded-2xl border p-4">
          <h2 className="text-lg font-semibold">Description</h2>
          <p className="mt-2 whitespace-pre-line text-gray-700">{data.description}</p>
        </div>
      )}

      {(data.lat != null && data.lon != null) && (
        <div className="mt-6 rounded-2xl border p-4">
          <h2 className="text-lg font-semibold">Location</h2>
          <p className="text-gray-600">{address || data.location}</p>
          <a
            href={`https://www.google.com/maps?q=${data.lat},${data.lon}`}
            target="_blank"
            rel="noreferrer"
            className="mt-3 inline-block underline"
          >
            Open in Google Maps
          </a>
        </div>
      )}
    </div>
  );
}

function DetailCard({ label, value }: { label: string; value?: string | number | null }) {
  if (value == null || value === "") return null;
  return (
    <div className="rounded-2xl border p-4">
      <div className="text-sm text-gray-500">{label}</div>
      <div className="mt-1 text-base font-medium">{value}</div>
    </div>
  );
}

function numOrDash(v?: number | null) {
  return v != null ? `${v}` : undefined;
}

function dateOrDash(v?: string | null) {
  if (!v) return undefined;
  try {
    const d = new Date(v);
    return d.toLocaleDateString();
  } catch {
    return v;
  }
}
