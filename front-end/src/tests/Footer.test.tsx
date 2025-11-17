import React from "react";
import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import Footer from '../components/layout/Footer'

describe("Footer", () => {
  it("renders current year", () => {
    render(<Footer />);
    const year = new Date().getFullYear();
    expect(
      screen.getByText(`© ${year} HousingHelper. All rights reserved.`)
    ).toBeInTheDocument();
  });

  it("renders Facebook, Instagram, and LinkedIn links", () => {
    render(<Footer />);

    expect(screen.getByLabelText("Facebook")).toBeInTheDocument();
    expect(screen.getByLabelText("Instagram")).toBeInTheDocument();
    expect(screen.getByLabelText("LinkedIn")).toBeInTheDocument();
  });

  it("contains correct social URLs", () => {
    render(<Footer />);

    expect(screen.getByLabelText("Facebook").getAttribute("href")).toBe(
      "https://www.facebook.com/boris.tsankov.77"
    );
    expect(screen.getByLabelText("Instagram").getAttribute("href")).toBe(
      "https://www.instagram.com/borists_"
    );
    expect(screen.getByLabelText("LinkedIn").getAttribute("href")).toBe(
      "https://www.linkedin.com/in/boris-tsankov-profile"
    );
  });

  it("matches snapshot", () => {
    const { container } = render(<Footer />);
    expect(container).toMatchSnapshot();
  });
});
