import React from "react";
import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { PropertyGallery } from '../components/gallery/PropertyGallery'

const mockItems = [
  {
    id: "1",
    title: "Modern Studio",
    location: "Eindhoven, NL",
    price: "€1,000 / month",
    image: "studio.jpg",
  },
  {
    id: "2",
    title: "Spacious Loft",
    location: "Amsterdam, NL",
    price: "€2,100 / month",
    image: "loft.jpg",
  },
];

const renderWithRouter = (ui: React.ReactElement) =>
  render(<MemoryRouter>{ui}</MemoryRouter>);

describe("PropertyGallery", () => {
  it("renders 'No properties found.' when items is an empty array", () => {
    renderWithRouter(<PropertyGallery items={[]} />);

    expect(
      screen.getByText("No properties found.")
    ).toBeInTheDocument();
  });

  it("renders 'No properties found.' when items is undefined at runtime", () => {
    // @ts-expect-error: testing runtime behavior with undefined items
    renderWithRouter(<PropertyGallery items={undefined} />);

    expect(
      screen.getByText("No properties found.")
    ).toBeInTheDocument();
  });

  it("renders a grid of PropertyCard components for each item", () => {
    const { container } = renderWithRouter(
      <PropertyGallery items={mockItems} />
    );

    // Titles from the underlying PropertyCard
    expect(screen.getByText("Modern Studio")).toBeInTheDocument();
    expect(screen.getByText("Spacious Loft")).toBeInTheDocument();

    // Ensure grid container exists
    const grid = container.querySelector(
      ".grid.grid-cols-1.sm\\:grid-cols-2.lg\\:grid-cols-3"
    );
    expect(grid).not.toBeNull();

    // Ensure we have as many children as items
    expect(grid?.children.length).toBe(mockItems.length);
  });

  it("stretches each card to full height in its column", () => {
    const { container } = renderWithRouter(
      <PropertyGallery items={mockItems} />
    );

    const wrappers = container.querySelectorAll("div.h-full");
    expect(wrappers.length).toBe(mockItems.length);
  });

  it("matches snapshot", () => {
    const { container } = renderWithRouter(
      <PropertyGallery items={mockItems} />
    );
    expect(container).toMatchSnapshot();
  });
});
