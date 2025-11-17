import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import React from "react";
import { PropertyCard } from '../components/gallery/PropertyCard'

const mockItem = {
  id: "123",
  title: "Cozy Apartment",
  location: "Amsterdam, NL",
  price: "€1,500 / month",
  image: "test-image.jpg",
};

describe("PropertyCard (Vitest)", () => {
  const setup = () =>
    render(
      <MemoryRouter>
        <PropertyCard item={mockItem} />
      </MemoryRouter>
    );

  it("renders the property title", () => {
    setup();
    expect(screen.getByText("Cozy Apartment")).toBeInTheDocument();
  });

  it("renders the property location", () => {
    setup();
    expect(screen.getByText("Amsterdam, NL")).toBeInTheDocument();
  });

  it("renders the property price", () => {
    setup();
    expect(screen.getByText("€1,500 / month")).toBeInTheDocument();
  });

  it("renders the image with correct src & alt", () => {
    setup();
    const img = screen.getByRole("img") as HTMLImageElement;
    expect(img.src).toContain(mockItem.image);
    expect(img.alt).toBe(mockItem.title);
  });

  it("wraps everything in a correct <Link>", () => {
    setup();
    const link = screen.getByRole("link");
    expect(link.getAttribute("href")).toBe(`/listings/${mockItem.id}`);
  });

  it("includes hover-related classes", () => {
    setup();
    const figure = screen.getByRole("img").closest("figure");
    expect(figure?.className).toContain("hover:shadow-md");
  });

  it("matches snapshot", () => {
    const { container } = setup();
    expect(container).toMatchSnapshot();
  });
});
