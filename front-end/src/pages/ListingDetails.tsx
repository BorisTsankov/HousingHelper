import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import Page from "../components/layout/Page";
import Navbar from "../components/layout/Navbar";
import Footer from "../components/layout/Footer";
import { Section } from "../components/ui/Section";
import { Link } from "react-router-dom";

type Listing = {
  id: string | number;
  title?: string;
  description?: string;
  price?: number;
  address?: string;
  city?: string;
  bedrooms?: number;
  bathrooms?: number;
  sizeSqm?: number;
  type?: string;
  furnished?: boolean;
  petsAllowed?: boolean;
  imageUrl?: string;            // primary image
  images?: string[];            // gallery
  latitude?: number;
  longitude?: number;
};

const ListingDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const nav = useNavigate();

  const [data, setData] = React.useState<Listing | null>(null);
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState<string | null>(null);

  React.useEffect(() => {
    const c = new AbortController();
    (async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await fetch(`/api/listings/${id}`, { signal: c.signal });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const json: Listing = await res.json();
        setData(json);
      } catch (e: any) {
        if (e?.name !== "AbortError") setError(e?.message ?? "Failed to load listing");
      } finally {
        setLoading(false);
      }
    })();
    return () => c.abort();
  }, [id]);

  return (
    <Page>
      <Navbar />

      <Section>
        <div className="mx-auto w-full max-w-5xl px-4">
          <button
            onClick={() => nav(-1)}
            className="mb-4 rounded-md border px-3 py-1.5 text-sm hover:bg-gray-50"
          >
            ← Back
          </button>

          {loading && <div className="rounded-lg border p-6">Loading…</div>}
          {!loading && error && <div className="rounded-lg border border-red-300 bg-red-50 p-6 text-red-800">{error}</div>}
          {!loading && !error && !data && (
            <div className="rounded-lg border bg-gray-50 p-6">Listing not found.</div>
          )}

          {!loading && !error && data && (
            <div className="space-y-8">
              {/* Title + price */}
              <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
                <div>
                  <h1 className="text-2xl sm:text-3xl font-bold">{data.title ?? "Listing"}</h1>
                  <p className="opacity-70">{[data.address, data.city].filter(Boolean).join(", ")}</p>
                </div>
                {typeof data.price === "number" && (
                  <div className="text-2xl font-extrabold">€{data.price.toLocaleString()}</div>
                )}
              </div>

              {/* Gallery */}
              <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                {(data.images?.length ? data.images : [data.imageUrl].filter(Boolean)).map((src, i) => (
                  <div key={i} className="aspect-[16/10] overflow-hidden rounded-xl">
                    <img src={src!} alt={`Photo ${i + 1}`} className="h-full w-full object-cover" />
                  </div>
                ))}
              </div>

              {/* Facts */}
              <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                <Fact label="Type" value={data.type} />
                <Fact label="Bedrooms" value={num(data.bedrooms)} />
                <Fact label="Bathrooms" value={num(data.bathrooms)} />
                <Fact label="Size" value={data.sizeSqm ? `${data.sizeSqm} m²` : undefined} />
                <Fact label="Furnished" value={bool(data.furnished)} />
                <Fact label="Pets Allowed" value={bool(data.petsAllowed)} />
              </div>

              {/* Description */}
              {data.description && (
                <div className="rounded-xl border bg-white p-6">
                  <h2 className="mb-2 text-xl font-semibold">Description</h2>
                  <p className="whitespace-pre-wrap leading-relaxed">{data.description}</p>
                </div>
              )}

              {/* Map (static link placeholder) */}
              {(data.latitude && data.longitude) && (
                <div className="rounded-xl border p-4">
                  <h2 className="mb-3 text-lg font-semibold">Location</h2>
                  <a
                    className="text-blue-600 underline"
                    href={`https://www.google.com/maps?q=${data.latitude},${data.longitude}`}
                    target="_blank" rel="noreferrer"
                  >
                    View on Google Maps
                  </a>
                </div>
              )}

              {/* Contact / Apply */}
              <div className="flex flex-wrap items-center gap-3">
                <button className="rounded-lg bg-blue-600 px-5 py-2.5 font-semibold text-white hover:bg-blue-700">
                  Contact Agent
                </button>
                <button className="rounded-lg border px-5 py-2.5 hover:bg-gray-50">
                  Schedule a Viewing
                </button>
              </div>
            </div>
          )}
        </div>
      </Section>

      <Footer />
    </Page>
  );
};

function Fact({ label, value }: { label: string; value?: string }) {
  if (!value) return null;
  return (
    <div className="rounded-xl border bg-white p-4">
      <div className="text-sm opacity-60">{label}</div>
      <div className="text-lg font-semibold">{value}</div>
    </div>
  );
}

function num(n?: number) {
  return typeof n === "number" ? String(n) : undefined;
}
function bool(b?: boolean) {
  return typeof b === "boolean" ? (b ? "Yes" : "No") : undefined;
}

export default ListingDetails;