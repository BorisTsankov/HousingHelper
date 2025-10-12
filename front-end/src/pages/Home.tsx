import React from "react";
import Page from "../components/layout/Page";
import { Section } from "../components/ui/Section";
import { SearchBar } from "../components/search/SearchBar";
import type { SearchFilters, Filters } from "../components/search/SearchFilters";
import { PropertyGallery } from "../components/gallery/PropertyGallery";
import Navbar from "../components/layout/Navbar";
import Footer from "../components/layout/Footer";
import type { Property } from "../types/property";

const Home: React.FC = () => {
  const [filters, setFilters] = React.useState<Filters>({});

  const properties: Property[] = [
    {
      id: "1",
      title: "Modern Apartment in City Center",
      image: "https://via.placeholder.com/400x250?text=Apartment",
      price: "€1200/mo",
      location: "Eindhoven",
    },
    {
      id: "2",
      title: "Cozy Studio Near Fontys",
      image: "https://via.placeholder.com/400x250?text=Studio",
      price: "€850/mo",
      location: "Eindhoven North",
    },
    {
      id: "3",
      title: "Spacious Loft with Balcony",
      image: "https://via.placeholder.com/400x250?text=Loft",
      price: "€1500/mo",
      location: "Strijp-S",
    },
  ];

  return (

    <Page>
      <Navbar />

      <Section title="Find Your Dream Home" center>
        <div className="mx-auto max-w-4xl px-4">
          <SearchBar onSearch={(q) => console.log({ q, filters })} />
          <div className="mt-4">
            <SearchFilters value={filters} onChange={setFilters} />
          </div>
        </div>
      </Section>

      <Section title="Featured Listings">
        <div className="mx-auto max-w-5xl px-4">
          <PropertyGallery items={properties} />
        </div>
      </Section>

      <Footer />
    </Page>
  );
};

export default Home;
