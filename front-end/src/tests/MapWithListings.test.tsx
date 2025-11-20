import React from "react";
import { describe, it, expect, vi } from "vitest";
import { render, screen, fireEvent, within } from "@testing-library/react";


vi.mock("leaflet", () => {
  const latLngBounds = (coords: any[]) => ({
    coords,
    pad: (padValue: number) => ({ coords, padValue }),
  });

  return {
    default: {
      Icon: {
        Default: {
          mergeOptions: vi.fn(),
        },
      },
      latLngBounds,
    },
    latLngBounds,
  };
});


vi.mock("react-leaflet", () => {
  return {
    MapContainer: ({ children, bounds }: any) => (
      <div data-testid="map" data-bounds={bounds ? "has-bounds" : "no-bounds"}>
        {children}
      </div>
    ),
    TileLayer: () => <div data-testid="tile-layer" />,
    Marker: ({ children, position, opacity, eventHandlers }: any) => (
      <div
        data-testid="marker"
        data-position={JSON.stringify(position)}
        data-opacity={opacity}
        onClick={() => eventHandlers?.click?.()}
      >
        {children}
      </div>
    ),
    Popup: ({ children }: any) => <div data-testid="popup">{children}</div>,
    useMap: () => ({
      invalidateSize: vi.fn(),
      getBounds: () => ({
        getNorth: () => 1,
        getSouth: () => 2,
        getEast: () => 3,
        getWest: () => 4,
      }),
    }),
    useMapEvents: (handlers: any) => {

      handlers?.moveend?.();
      handlers?.zoomend?.();
      return {};
    },
  };
});


vi.mock("react-leaflet-markercluster", () => ({
  __esModule: true,
  default: ({ children }: any) => (
    <div data-testid="cluster">{children}</div>
  ),
}));

import MapWithListings, { PropertyLite } from "../components/listings/MapWithListings";

const baseItems: PropertyLite[] = [
  {
    id: 1,
    title: "Nice flat",
    lat: 52.37,
    lon: 4.89,
    price: "€1,500",
    image: "img1.jpg",
    city: "Amsterdam",
  },
  {
    id: 2,
    title: "Cozy studio",
    lat: 52.38,
    lon: 4.91,
    price: "€1,200",
    image: "img2.jpg",
    city: "Amsterdam",
  },
  {
    id: 3,
    title: "Invalid coords",
    lat: null,
    lon: null,
  },
];

describe("MapWithListings", () => {
  it("filters out items without valid coordinates", () => {
    render(<MapWithListings items={baseItems} />);

    const markers = screen.getAllByTestId("marker");

    expect(markers.length).toBe(2);
  });

  it("sets bounds when there are valid items", () => {
    const { getByTestId } = render(<MapWithListings items={baseItems} />);
    const map = getByTestId("map");
    expect(map.getAttribute("data-bounds")).toBe("has-bounds");
  });

  it("does not set bounds when there are no valid items", () => {
    const items: PropertyLite[] = [
      { id: 1, lat: null, lon: null },
      { id: 2 },
    ];
    const { getByTestId } = render(<MapWithListings items={items} />);
    const map = getByTestId("map");
    expect(map.getAttribute("data-bounds")).toBe("no-bounds");
  });

  it("uses activeId to change marker opacity", () => {
    render(<MapWithListings items={baseItems} activeId={2} />);

    const markers = screen.getAllByTestId("marker");
    const opacities = markers.map((m) => m.getAttribute("data-opacity"));


    expect(opacities[0]).toBe("0.9");

    expect(opacities[1]).toBe("1");
  });

  it("calls onPreview when a marker is clicked", () => {
    const onPreview = vi.fn();

    render(<MapWithListings items={baseItems} onPreview={onPreview} />);

    const markers = screen.getAllByTestId("marker");
    fireEvent.click(markers[0]);

    expect(onPreview).toHaveBeenCalledTimes(1);
    expect(onPreview).toHaveBeenCalledWith(1);
  });

  it("renders popup content and calls onPreview when 'Quick preview' clicked", () => {
    const onPreview = vi.fn();

    render(<MapWithListings items={baseItems} onPreview={onPreview} />);


    const popups = screen.getAllByTestId("popup");
    const firstPopup = popups[0];
    const utils = within(firstPopup);


    expect(utils.getByText("Nice flat")).toBeInTheDocument();
    expect(utils.getByText("Amsterdam")).toBeInTheDocument();
    expect(utils.getByText("€1,500")).toBeInTheDocument();

    const buttons = utils.getAllByText("Quick preview");
    fireEvent.click(buttons[0]);

    expect(onPreview).toHaveBeenCalledWith(1);
  });

  it("uses default details link when getDetailsHref is not provided", () => {
    render(<MapWithListings items={baseItems} />);

    const links = screen.getAllByText("Open details");
    expect(links[0]).toHaveAttribute("href", "/listings/1");
    expect(links[1]).toHaveAttribute("href", "/listings/2");
  });

  it("uses custom getDetailsHref when provided", () => {
    render(
      <MapWithListings
        items={baseItems}
        getDetailsHref={(p) => `/custom/${p.id}`}
      />
    );

    const links = screen.getAllByText("Open details");
    expect(links[0]).toHaveAttribute("href", "/custom/1");
    expect(links[1]).toHaveAttribute("href", "/custom/2");
  });

  it("calls onBoundsChange on move/zoom events", () => {
    const onBoundsChange = vi.fn();

    render(<MapWithListings items={baseItems} onBoundsChange={onBoundsChange} />);


    expect(onBoundsChange).toHaveBeenCalledTimes(2);
    expect(onBoundsChange).toHaveBeenCalledWith({
      north: 1,
      south: 2,
      east: 3,
      west: 4,
    });
  });
});
