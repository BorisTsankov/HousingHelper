import React, { useMemo } from "react";
import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  useMap,
  useMapEvents,
} from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L, { LatLngBoundsExpression } from "leaflet";

import MarkerClusterGroup from "react-leaflet-markercluster";
import "leaflet.markercluster/dist/MarkerCluster.css";
import "leaflet.markercluster/dist/MarkerCluster.Default.css";

import iconUrl from "leaflet/dist/images/marker-icon.png";
import icon2xUrl from "leaflet/dist/images/marker-icon-2x.png";
import shadowUrl from "leaflet/dist/images/marker-shadow.png";

L.Icon.Default.mergeOptions({
  iconUrl,
  iconRetinaUrl: icon2xUrl,
  shadowUrl,
});


export type PropertyLite = {
  id: string | number;
  title?: string | null;
  lat?: number | null;
  lon?: number | null;
  price?: string | number | null;
  image?: string | null;
  city?: string | null;
};

export type MapWithListingsProps = {
  items: PropertyLite[];
  initialCenter?: [number, number];
  initialZoom?: number;
  activeId?: string | number | null;
  onBoundsChange?: (bbox: { north: number; south: number; east: number; west: number }) => void;
  onPreview?: (id: string | number) => void;
  getDetailsHref?: (p: PropertyLite) => string;
  className?: string;
  fitBounds?: LatLngBoundsExpression;
};

const DEFAULT_CENTER: [number, number] = [52.370216, 4.895168]; // Amsterdam
const DEFAULT_ZOOM = 11;

export default function MapWithListings({
  items,
  initialCenter = DEFAULT_CENTER,
  initialZoom = DEFAULT_ZOOM,
  activeId,
  onBoundsChange,
  onPreview,
  getDetailsHref,
  className,
  fitBounds,
}: MapWithListingsProps) {
  const valid = useMemo(
    () =>
      items.filter((p) => typeof p.lat === "number" && typeof p.lon === "number") as Array<
        Required<Pick<PropertyLite, "id" | "lat" | "lon">> & PropertyLite
      >,
    [items]
  );

  const bounds = useMemo(() => {
    if (fitBounds) return fitBounds;
    if (valid.length === 0) return undefined;
    const b = L.latLngBounds(valid.map((p) => [p.lat!, p.lon!]));
    return b.pad(0.05) as LatLngBoundsExpression;
  }, [valid, fitBounds]);

const clusterKey = useMemo(
    () => valid.map(p => String(p.id)).sort().join("|"),
    [valid]
  );

  return (
    <div className={"relative w-full h-[70vh] rounded-2xl overflow-hidden " + (className ?? "")}>
      <MapContainer
        center={initialCenter}
        zoom={initialZoom}
        bounds={bounds}
        scrollWheelZoom
        className="w-full h-full"
      >
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution="&copy; OpenStreetMap contributors"
        />

        <ViewEvents onBoundsChange={onBoundsChange} />

        <MarkerClusterGroup key={clusterKey} chunkedLoading>
                  {valid.map((p) => (
                    <Marker
                      key={String(p.id)}
                      position={[p.lat!, p.lon!]}
                      opacity={activeId != null && String(activeId) === String(p.id) ? 1 : 0.9}
                      eventHandlers={{ click: () => onPreview?.(p.id) }}
                    >
                      <Popup>
                        <PopupCard
                          p={p}
                          onPreview={() => onPreview?.(p.id)}
                          detailsHref={getDetailsHref ? getDetailsHref(p) : `/listings/${p.id}`}
                        />
                      </Popup>
                    </Marker>
                  ))}
                </MarkerClusterGroup>
      </MapContainer>
    </div>
  );
}

function AutoResize() {
  const map = useMap();
  React.useEffect(() => {
    const t = setTimeout(() => map.invalidateSize(), 50);
    return () => clearTimeout(t);
  }, [map]);
  return null;
}

function ViewEvents({
  onBoundsChange,
}: {
  onBoundsChange?: (bbox: { north: number; south: number; east: number; west: number }) => void;
}) {
  const map = useMap();
  useMapEvents({
    moveend: () => {
      if (!onBoundsChange) return;
      const b = map.getBounds();
      const bbox = { north: b.getNorth(), south: b.getSouth(), east: b.getEast(), west: b.getWest() };
      onBoundsChange(bbox);
    },
    zoomend: () => {
      if (!onBoundsChange) return;
      const b = map.getBounds();
      const bbox = { north: b.getNorth(), south: b.getSouth(), east: b.getEast(), west: b.getWest() };
      onBoundsChange(bbox);
    },
  });
  return null;
}

function PopupCard({ p, onPreview, detailsHref }: { p: PropertyLite; onPreview?: () => void; detailsHref: string }) {
  return (
    <div className="w-56">
      {p.image && (
        <img src={p.image} alt={p.title ?? "Listing"} className="w-full h-28 object-cover rounded-md mb-2" />
      )}
      <div className="text-sm font-semibold leading-tight line-clamp-2">{p.title || "Untitled"}</div>
      {p.city && <div className="text-xs text-gray-600 mt-0.5">{p.city}</div>}
      {p.price != null && <div className="text-sm font-medium mt-1">{String(p.price)}</div>}

      <div className="mt-2 grid grid-cols-2 gap-2">
        <button
          className="rounded-md border px-2 py-1 text-xs hover:bg-gray-50"
          onClick={onPreview}
        >
          Quick preview
        </button>
        <a
          href={detailsHref}
          className="rounded-md border px-2 py-1 text-center text-xs hover:bg-gray-50"
        >
          Open details
        </a>
      </div>
    </div>
  );
}
