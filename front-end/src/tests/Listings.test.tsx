import React from "react";
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import Listings from "../pages/Listings"; // adjust if needed
import userEvent from "@testing-library/user-event";


vi.mock("../components/layout/Page", () => ({
  __esModule: true,
  default: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="page">{children}</div>
  ),
}));

vi.mock("../components/layout/Navbar", () => ({
  __esModule: true,
  default: () => <div data-testid="navbar">Navbar</div>,
}));

vi.mock("../components/layout/Footer", () => ({
  __esModule: true,
  default: () => <div data-testid="footer">Footer</div>,
}));

vi.mock("../components/ui/Section", () => ({
  __esModule: true,
  Section: ({
    children,
    title,
  }: {
    children: React.ReactNode;
    title?: string;
  }) => (
    <section data-testid="section">
      {title && <h2>{title}</h2>}
      {children}
    </section>
  ),
}));

vi.mock("../components/layout/FiltersPanel", () => ({
  __esModule: true,
  default: ({
    children,
  }: {
    children: React.ReactNode;
    className?: string;
  }) => <aside data-testid="filters-panel">{children}</aside>,
}));

vi.mock("../components/search/SearchBar", () => ({
  __esModule: true,
  SearchBar: ({
    onSearch,
    defaultValue,
  }: {
    onSearch: (text: string) => void;
    defaultValue?: string;
    className?: string;
  }) => (
    <input
      aria-label="search-input"
      defaultValue={defaultValue}
      onChange={(e) => onSearch((e.target as HTMLInputElement).value)}
    />
  ),
}));

vi.mock("../components/search/SearchFiltersAdvanced", () => ({
  __esModule: true,
  SearchFiltersAdvanced: () => (
    <div data-testid="filters-advanced">FiltersAdvanced</div>
  ),
}));

vi.mock("../components/gallery/PropertyGallery", () => ({
  __esModule: true,
  PropertyGallery: ({ items }: { items: any[] }) => (
    <div data-testid="property-gallery">Gallery ({items.length} items)</div>
  ),
}));

vi.mock("../components/listings/MapWithListings", () => ({
  __esModule: true,
  default: ({ items }: { items: any[] }) => (
    <div data-testid="map-view">Map with {items.length} items</div>
  ),
}));

const mockFetch = vi.fn();

beforeEach(() => {
  mockFetch.mockReset();

  global.fetch = mockFetch;
});

afterEach(() => {
  vi.restoreAllMocks();
});

function renderListings(initialUrl: string = "/listings") {
  return render(
    <MemoryRouter initialEntries={[initialUrl]}>
      <Routes>
        <Route path="/listings" element={<Listings />} />
      </Routes>
    </MemoryRouter>
  );
}

describe("Listings page", () => {
  it("loads listings and shows gallery after skeleton", async () => {
    const filtersResponse = {
      types: [],
      cities: [],
      priceBuckets: [],
      bedrooms: [],
      bathrooms: [],
      furnished: [],
      petsAllowed: [],
    };

    const listingsResponse = {
      items: [
        { id: "1", title: "Listing 1" },
        { id: "2", title: "Listing 2" },
      ],
      total: 2,
    };

    mockFetch.mockImplementation((url: string) => {
      if (url.startsWith("/api/listings/filters")) {
        return Promise.resolve({
          ok: true,
          json: async () => filtersResponse,
        } as Response);
      }
      if (url.startsWith("/api/listings")) {
        return Promise.resolve({
          ok: true,
          status: 200,
          json: async () => listingsResponse,
        } as Response);
      }
      return Promise.reject(new Error(`Unexpected url: ${url}`));
    });

    const { container } = renderListings();


    expect(container.querySelector(".animate-pulse")).toBeInTheDocument();


    const gallery = await screen.findByTestId("property-gallery");
    expect(gallery).toHaveTextContent("Gallery (2 items)");


    expect(
      screen.getByText("Found 2 results")
    ).toBeInTheDocument();
  });

  it("shows error state when listings fetch fails", async () => {
    const filtersResponse = {
      types: [],
      cities: [],
      priceBuckets: [],
      bedrooms: [],
      bathrooms: [],
      furnished: [],
      petsAllowed: [],
    };

    mockFetch.mockImplementation((url: string) => {
      if (url.startsWith("/api/listings/filters")) {
        return Promise.resolve({
          ok: true,
          json: async () => filtersResponse,
        } as Response);
      }
      if (url.startsWith("/api/listings")) {
        return Promise.resolve({
          ok: false,
          status: 500,
          json: async () => ({}),
        } as Response);
      }
      return Promise.reject(new Error(`Unexpected url: ${url}`));
    });

    renderListings();

    await waitFor(() => {
      expect(
        screen.getByText("Something went wrong")
      ).toBeInTheDocument();
    });

    expect(screen.getByText("HTTP 500")).toBeInTheDocument();
  });

  it("shows empty state when no listings are returned", async () => {
    const filtersResponse = {
      types: [],
      cities: [],
      priceBuckets: [],
      bedrooms: [],
      bathrooms: [],
      furnished: [],
      petsAllowed: [],
    };

    const listingsResponse = {
      items: [],
      total: 0,
    };

    mockFetch.mockImplementation((url: string) => {
      if (url.startsWith("/api/listings/filters")) {
        return Promise.resolve({
          ok: true,
          json: async () => filtersResponse,
        } as Response);
      }
      if (url.startsWith("/api/listings")) {
        return Promise.resolve({
          ok: true,
          status: 200,
          json: async () => listingsResponse,
        } as Response);
      }
      return Promise.reject(new Error(`Unexpected url: ${url}`));
    });

    renderListings();

    await waitFor(() => {
      expect(
        screen.getByText("No listings found")
      ).toBeInTheDocument();
    });
  });

  it("requests the next page when clicking Next (pagination)", async () => {
    const filtersResponse = {
      types: [],
      cities: [],
      priceBuckets: [],
      bedrooms: [],
      bathrooms: [],
      furnished: [],
      petsAllowed: [],
    };

    mockFetch.mockImplementation((url: string) => {
      if (url.startsWith("/api/listings/filters")) {
        return Promise.resolve({
          ok: true,
          json: async () => filtersResponse,
        } as Response);
      }
      if (url.startsWith("/api/listings")) {
        const u = new URL(url, "http://localhost");
        const page = u.searchParams.get("page");
        if (page === "0") {
          return Promise.resolve({
            ok: true,
            status: 200,
            json: async () => ({
              items: [{ id: "1", title: "Listing 1" }],
              total: 24,
            }),
          } as Response);
        }
        if (page === "1") {
          return Promise.resolve({
            ok: true,
            status: 200,
            json: async () => ({
              items: [{ id: "2", title: "Listing 2" }],
              total: 24,
            }),
          } as Response);
        }
        return Promise.reject(new Error(`Unexpected page: ${page}`));
      }
      return Promise.reject(new Error(`Unexpected url: ${url}`));
    });

    const user = userEvent.setup();
    renderListings();


    await screen.findByTestId("property-gallery");

    const nextBtn = screen.getByText("Next") as HTMLButtonElement;
    expect(nextBtn).not.toBeDisabled();

    await user.click(nextBtn);



    await waitFor(() => {
      const urls = mockFetch.mock.calls.map((c) => c[0] as string);
      expect(urls.some((u) => u.includes("page=1"))).toBe(true);


      const next = screen.getByText("Next") as HTMLButtonElement;
      expect(next).toBeDisabled();
    });
  });


  it("performs search and includes q param in API call", async () => {
    const filtersResponse = {
      types: [],
      cities: [],
      priceBuckets: [],
      bedrooms: [],
      bathrooms: [],
      furnished: [],
      petsAllowed: [],
    };

    const listingsInitial = {
      items: [],
      total: 0,
    };

    const listingsSearched = {
      items: [{ id: "1", title: "Searched listing" }],
      total: 1,
    };

    mockFetch.mockImplementation((url: string) => {
      if (url.startsWith("/api/listings/filters")) {
        return Promise.resolve({
          ok: true,
          json: async () => filtersResponse,
        } as Response);
      }
      if (url.startsWith("/api/listings")) {
        const u = new URL(url, "http://localhost");
        const q = u.searchParams.get("q");
        if (!q) {
          return Promise.resolve({
            ok: true,
            status: 200,
            json: async () => listingsInitial,
          } as Response);
        }
        if (q === "Eindhoven") {
          return Promise.resolve({
            ok: true,
            status: 200,
            json: async () => listingsSearched,
          } as Response);
        }
        return Promise.reject(new Error(`Unexpected q: ${q}`));
      }
      return Promise.reject(new Error(`Unexpected url: ${url}`));
    });

    const user = userEvent.setup();
    renderListings();


    await screen.findByText("No listings found");

    const input = screen.getByLabelText("search-input") as HTMLInputElement;
    await user.type(input, "Eindhoven");


    await waitFor(() => {
      const urls = mockFetch.mock.calls.map((c) => c[0] as string);
      expect(
        urls.some((u) => u.includes("q=Eindhoven"))
      ).toBe(true);
    });



  });


  it("switches to map view when Map button is clicked", async () => {
    const filtersResponse = {
      types: [],
      cities: [],
      priceBuckets: [],
      bedrooms: [],
      bathrooms: [],
      furnished: [],
      petsAllowed: [],
    };

    const listingsResponse = {
      items: [
        {
          id: "1",
          title: "Map Listing",
          lat: 51.4416,
          lon: 5.4697,
          price: 1500,
          image: "https://example.com/image.jpg",
          city: "Eindhoven",
        },
      ],
      total: 1,
    };

    mockFetch.mockImplementation((url: string) => {
      if (url.startsWith("/api/listings/filters")) {
        return Promise.resolve({
          ok: true,
          json: async () => filtersResponse,
        } as Response);
      }
      if (url.startsWith("/api/listings")) {
        return Promise.resolve({
          ok: true,
          status: 200,
          json: async () => listingsResponse,
        } as Response);
      }
      return Promise.reject(new Error(`Unexpected url: ${url}`));
    });

    renderListings();

    await screen.findByTestId("property-gallery");

    const mapButton = screen.getByText("Map");
    mapButton.click();

    await waitFor(() => {
      expect(screen.getByTestId("map-view")).toBeInTheDocument();
    });

    expect(
      screen.queryByTestId("property-gallery")
    ).not.toBeInTheDocument();
  });

  it("starts directly in map view when view=map in URL", async () => {
    const filtersResponse = {
      types: [],
      cities: [],
      priceBuckets: [],
      bedrooms: [],
      bathrooms: [],
      furnished: [],
      petsAllowed: [],
    };

    const listingsResponse = {
      items: [
        {
          id: "2",
          title: "Map First Listing",
          lat: 51.4416,
          lon: 5.4697,
          price: 1200,
          image: null,
          city: "Eindhoven",
        },
      ],
      total: 1,
    };

    mockFetch.mockImplementation((url: string) => {
      if (url.startsWith("/api/listings/filters")) {
        return Promise.resolve({
          ok: true,
          json: async () => filtersResponse,
        } as Response);
      }
      if (url.startsWith("/api/listings")) {
        return Promise.resolve({
          ok: true,
          status: 200,
          json: async () => listingsResponse,
        } as Response);
      }
      return Promise.reject(new Error(`Unexpected url: ${url}`));
    });

    renderListings("/listings?view=map");

    await waitFor(() => {
      expect(screen.getByTestId("map-view")).toBeInTheDocument();
    });

    expect(
      screen.queryByTestId("property-gallery")
    ).not.toBeInTheDocument();
  });
});
