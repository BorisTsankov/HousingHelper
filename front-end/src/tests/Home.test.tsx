import React from "react";
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Home from "../pages/Home";


vi.mock("../components/layout/Page", () => ({
  __esModule: true,
  default: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="page">{children}</div>
  ),
}));

vi.mock("../components/layout/Navbar", () => ({
  __esModule: true,
  default: () => <nav data-testid="navbar">Navbar</nav>,
}));

vi.mock("../components/layout/Footer", () => ({
  __esModule: true,
  default: () => <footer data-testid="footer">Footer</footer>,
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

vi.mock("../components/search/SearchBar", () => ({
  __esModule: true,
  SearchBar: ({ onSearch }: { onSearch: (text: string) => void }) => (
    <input
      aria-label="search-input"
      onChange={(e) => onSearch((e.target as HTMLInputElement).value)}
    />
  ),
}));

vi.mock("../components/search/SearchFiltersSmall", () => ({
  __esModule: true,
  SearchFiltersSmall: ({
    value,
    onChange,
  }: {
    value: any;
    onChange: (filters: any) => void;
    options: any;
  }) => (
    <button
      type="button"
      onClick={() =>
        onChange({
          ...value,
          type: "apartment",
          city: "Eindhoven",
          minPrice: 1000,
          maxPrice: 2000,
        })
      }
    >
      set-filters
    </button>
  ),
}));

vi.mock("../components/gallery/PropertyGallery", () => ({
  __esModule: true,
  PropertyGallery: ({ items }: { items: any[] }) => (
    <div data-testid="property-gallery">
      PropertyGallery ({items.length} items)
    </div>
  ),
}));


vi.mock("../assets/home_photo.jpeg", () => ({
  __esModule: true,
  default: "home_photo.jpeg",
}));

const mockFetch = vi.fn();

beforeEach(() => {
  mockFetch.mockReset();

  global.fetch = mockFetch;


  vi.spyOn(window, "location", "get").mockReturnValue({
    ...window.location,

    assign: vi.fn(),
  });
});

afterEach(() => {
  vi.restoreAllMocks();
});

describe("Home page", () => {
  it("shows loading then renders PropertyGallery when listings load", async () => {
    const user = userEvent.setup();


    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        types: [],
        cities: [],
        priceBuckets: [],
        bedrooms: [],
        bathrooms: [],
        furnished: [],
        petsAllowed: [],
      }),
    });


    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({
        items: [
          {
            id: "1",
            title: "Nice flat",
          },
        ],
      }),
    });

    render(<Home />);


    expect(screen.getByText("Loading...")).toBeInTheDocument();


    await waitFor(() => {
      expect(screen.queryByText("Loading...")).not.toBeInTheDocument();
    });

    expect(screen.getByTestId("property-gallery")).toBeInTheDocument();
    expect(screen.queryByText("No listings found.")).not.toBeInTheDocument();
  });

  it("shows error message when listings fetch fails", async () => {

    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        types: [],
        cities: [],
        priceBuckets: [],
        bedrooms: [],
        bathrooms: [],
        furnished: [],
        petsAllowed: [],
      }),
    });


    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 500,
      json: async () => ({}),
    });

    render(<Home />);

    await waitFor(() => {
      expect(screen.queryByText("Loading...")).not.toBeInTheDocument();
    });


    expect(screen.getByText("HTTP 500")).toBeInTheDocument();
  });

  it("shows empty state when no listings are returned", async () => {

    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        types: [],
        cities: [],
        priceBuckets: [],
        bedrooms: [],
        bathrooms: [],
        furnished: [],
        petsAllowed: [],
      }),
    });


    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({
        items: [],
      }),
    });

    render(<Home />);

    await waitFor(() => {
      expect(screen.queryByText("Loading...")).not.toBeInTheDocument();
    });

    expect(screen.getByText("No listings found.")).toBeInTheDocument();
    expect(
      screen.getByText("Please try again later.")
    ).toBeInTheDocument();
    expect(screen.queryByTestId("property-gallery")).not.toBeInTheDocument();
  });

  it("navigates to /listings with query and filters when searching", async () => {
    const user = userEvent.setup();


    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        types: [],
        cities: [],
        priceBuckets: [],
        bedrooms: [],
        bathrooms: [],
        furnished: [],
        petsAllowed: [],
      }),
    });


    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => ({
        items: [],
      }),
    });

    render(<Home />);


    const filtersBtn = screen.getByText("set-filters");
    await user.click(filtersBtn);

    const searchInput = screen.getByLabelText("search-input");
    await user.type(searchInput, "test search");

    const assignMock = (window.location as any).assign as ReturnType<
      typeof vi.fn
    >;


    expect(assignMock).toHaveBeenCalledWith(
      "/listings?q=test+search&type=apartment&city=Eindhoven&minPrice=1000&maxPrice=2000"
    );
  });
});
