import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { PropertyGallery } from '../components/gallery/PropertyGallery'
import { PropertyCard } from "../components/gallery/PropertyCard";
import type { Property } from "../../types/property";


describe("PropertyGallery", () => {
  it("renders 'No properties found.' when items array is empty", () => {
    render(<PropertyGallery items={[]} />);
    expect(screen.getByText("No properties found.")).toBeInTheDocument();
  });

  it("renders 'No properties found.' when items is undefined", () => {
    render(<PropertyGallery items={undefined as unknown as Property[]} />);
    expect(screen.getByText("No properties found.")).toBeInTheDocument();
  });

  it("renders one visual card (figure) per item", () => {
    const items: Property[] = [
      { id: 1, name: "Modern Apartment", price: 1200, location: "Eindhoven" },
      { id: 2, name: "Cozy Studio", price: 800, location: "Tilburg" },
    ];
    const { container } = render(<PropertyGallery items={items} />);

    const figures = container.querySelectorAll("figure");
    expect(figures.length).toBe(items.length);

    expect(screen.getByText(/Eindhoven/i)).toBeInTheDocument();
    expect(screen.getByText(/Tilburg/i)).toBeInTheDocument();
    expect(screen.getByText("1200")).toBeInTheDocument();
    expect(screen.getByText("800")).toBeInTheDocument();
  });

  it("applies the grid layout classes on the wrapper when items exist", () => {
    const items: Property[] = [{ id: 1, name: "Villa", price: 2500, location: "Eindhoven" }];
    const { container } = render(<PropertyGallery items={items} />);

    const grid = container.querySelector("div.grid.grid-cols-1");
    expect(grid).toBeTruthy();

    expect(grid?.className).toContain("sm:grid-cols-2");
    expect(grid?.className).toContain("lg:grid-cols-3");
  });
});