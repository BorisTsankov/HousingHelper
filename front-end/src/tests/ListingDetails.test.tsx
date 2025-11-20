import React from "react";
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import {
  MemoryRouter,
  Routes,
  Route,
} from "react-router-dom";

import ListingDetails from "../pages/ListingDetails";



vi.mock("../components/layout/Navbar", () => ({
  __esModule: true,
  default: () => <div data-testid="navbar">Navbar</div>,
}));

vi.mock("lucide-react", () => ({
  __esModule: true,
  MapPin: (props: any) => <span data-testid="icon-map" {...props} />,
  Bed: (props: any) => <span data-testid="icon-bed" {...props} />,
  Bath: (props: any) => <span data-testid="icon-bath" {...props} />,
  Ruler: (props: any) => <span data-testid="icon-ruler" {...props} />,
  Euro: (props: any) => <span data-testid="icon-euro" {...props} />,
  ShieldCheck: (props: any) => <span data-testid="icon-shield" {...props} />,
  Home: (props: any) => <span data-testid="icon-home" {...props} />,
  ExternalLink: (props: any) => <span data-testid="icon-external" {...props} />,
  ImageIcon: (props: any) => <span data-testid="icon-image" {...props} />,
  Calendar: (props: any) => <span data-testid="icon-calendar" {...props} />,
}));

const mockFetch = vi.fn();

beforeEach(() => {
  mockFetch.mockReset();

  global.fetch = mockFetch;
});

afterEach(() => {
  vi.restoreAllMocks();
});

function renderWithRouter(id: string) {
  return render(
    <MemoryRouter initialEntries={[`/listings/${id}`]}>
      <Routes>
        <Route path="/listings/:id" element={<ListingDetails />} />
      </Routes>
    </MemoryRouter>
  );
}

describe("ListingDetails", () => {
  it("shows loading skeleton initially then renders listing details on success", async () => {
    const property = {
      id: "123",
      title: "Nice Apartment in Eindhoven",
      image: "https://example.com/image.jpg",
      photosCount: 5,
      street: "Coolstreet",
      houseNumber: "10",
      unit: "A",
      postalCode: "1234 AB",
      city: "Eindhoven",
      country: "Netherlands",
      location: "Eindhoven, Netherlands",
      displayPrice: "€1,500",
      displayDeposit: "€3,000",
      deposit: 3000,
      rentPeriod: "Per month",
      minimumLeaseMonths: 12,
      availableFrom: "2025-01-01",
      availableUntil: "2026-01-01",
      status: "Available",
      areaM2: 60,
      bedrooms: 2,
      bathrooms: 1,
      rooms: 3,
      propertyType: "Apartment",
      furnishingType: "Furnished",
      energyLabel: "A",
      description: "Very nice apartment in the city center.",
      lat: 51.4416,
      lon: 5.4697,
      canonicalUrl: "https://example.com/listing/123",
    };

    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => property,
      text: async () => "",
    });

    const { container } = renderWithRouter("123");


    expect(container.querySelector(".animate-pulse")).toBeInTheDocument();


    await waitFor(() => {
      expect(
        screen.getByText("Nice Apartment in Eindhoven")
      ).toBeInTheDocument();
    });

    expect(screen.getByTestId("navbar")).toBeInTheDocument();
    expect(
      screen.getByRole("img", { name: "Nice Apartment in Eindhoven" })
    ).toBeInTheDocument();


    expect(screen.getByText("5")).toBeInTheDocument();


    expect(
      screen.getAllByText(
        "Coolstreet 10 A, 1234 AB Eindhoven, Netherlands"
      )[0]
    ).toBeInTheDocument();


    const priceEls = screen.getAllByText("€1,500");
    expect(priceEls.length).toBeGreaterThanOrEqual(1);


    expect(screen.getAllByText("60 m²")).toHaveLength(2);
    expect(screen.getByText("2 beds")).toBeInTheDocument();
    expect(screen.getByText("1 bath")).toBeInTheDocument();


    expect(screen.getByText("Property type")).toBeInTheDocument();
    expect(screen.getByText("Apartment")).toBeInTheDocument();
    expect(screen.getByText("Furnishing")).toBeInTheDocument();
    expect(screen.getByText("Furnished")).toBeInTheDocument();
    expect(screen.getByText("Energy label")).toBeInTheDocument();
    expect(screen.getByText("A")).toBeInTheDocument();


    expect(
      screen.getByText("Very nice apartment in the city center.")
    ).toBeInTheDocument();
  });

  it("shows error state when fetch fails", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 404,
      text: async () => "Not found",
      json: async () => ({}),
    });

    renderWithRouter("999");

    await waitFor(() => {
      expect(
        screen.getByText("Could not load listing")
      ).toBeInTheDocument();
    });

    expect(screen.getByText("Not found")).toBeInTheDocument();
    expect(screen.getByText("Go back")).toBeInTheDocument();
  });

  it("returns null when there is no data (e.g. if id is missing) - no crash", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({}),
      text: async () => "",
    });

    render(
      <MemoryRouter initialEntries={["/listings/"]}>
        <Routes>
          <Route path="/listings/*" element={<ListingDetails />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByTestId("navbar")).toBeInTheDocument();
    });
  });

  it("renders map iframe when lat and lon are provided", async () => {
    const property = {
      id: "123",
      title: "Map Test Property",
      image: "https://example.com/image.jpg",
      photosCount: 0,
      street: "Mapstreet",
      houseNumber: "1",
      unit: "",
      postalCode: "0000 AA",
      city: "Eindhoven",
      country: "Netherlands",
      location: "Eindhoven, Netherlands",
      displayPrice: "€1,000",
      displayDeposit: "€2,000",
      deposit: 2000,
      rentPeriod: "Per month",
      minimumLeaseMonths: 6,
      availableFrom: "2025-01-01",
      availableUntil: "2025-12-31",
      status: "Available",
      areaM2: 50,
      bedrooms: 1,
      bathrooms: 1,
      rooms: 2,
      propertyType: "Studio",
      furnishingType: "Unfurnished",
      energyLabel: "B",
      description: "",
      lat: 51.4416,
      lon: 5.4697,
      canonicalUrl: "",
    };

    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => property,
      text: async () => "",
    });

    renderWithRouter("123");

    await waitFor(() => {
      expect(
        screen.getByText("Map Test Property")
      ).toBeInTheDocument();
    });

    const iframe = screen.getByTitle("Google Maps") as HTMLIFrameElement;
    expect(iframe).toBeInTheDocument();
    expect(iframe.src).toContain("https://www.google.com/maps/embed/v1/place");
    expect(iframe.src).toContain("51.4416");
    expect(iframe.src).toContain("5.4697");
  });
});
