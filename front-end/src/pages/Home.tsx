import React from "react";
import Page from "../components/layout/Page";
import { Section } from "../components/ui/Section";
import { SearchBar } from "../components/search/SearchBar";
import { SearchFilters } from "../components/search/SearchFilters";


import type { Filters } from "../types/filters";
import { PropertyGallery } from "../components/gallery/PropertyGallery";
import Navbar from "../components/layout/Navbar";
import Footer from "../components/layout/Footer";
import type { Property } from "../types/property";

const Home: React.FC = () => {
  const [filters, setFilters] = React.useState<Filters>({});
  const [properties, setProperties] = React.useState<Property[]>([]);
  const [loading, setLoading] = React.useState<boolean>(true);
  const [error, setError] = React.useState<string | null>(null);

    React.useEffect(() => {
      const loadFeatured = async () => {
        try {
          setLoading(true);
          setError(null);
          const res = await fetch(`/api/listings/featured?limit=12`);
          if (!res.ok) throw new Error(`HTTP ${res.status}`);
          const data: Property[] = await res.json();
          setProperties(data);
        } catch (e: any) {
          setError(e?.message ?? "Failed to load listings");
        } finally {
          setLoading(false);
        }
      };
      loadFeatured();
    }, []);

    // Hook up the SearchBar
    const handleSearch = async (q: string) => {
      try {
        setLoading(true);
        setError(null);
        const url = q?.trim()
          ? `/api/listings/search?q=${encodeURIComponent(q.trim())}&limit=24`
          : `/api/listings/featured?limit=12`;
        const res = await fetch(url);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data: Property[] = await res.json();
        setProperties(data);
      } catch (e: any) {
        setError(e?.message ?? "Search failed");
      } finally {
        setLoading(false);
      }
    };

  return (

    <Page>
      <Navbar />

    <Section title="Find Your Dream Home" center>
        <div className="mx-auto max-w-4xl px-4">
          <SearchBar onSearch={(q) => handleSearch(q)} />
          <div className="mt-4">
            {/* Filters are wired for later. If you want them active,
                read values from `filters` and include them in the fetch URL. */}
            <SearchFilters value={filters} onChange={setFilters} />
          </div>
        </div>
      </Section>

      <Section title="Featured Listings">
        <div className="mx-auto max-w-5xl px-4">
        {loading && <p>Loading...</p>}
        {!loading && <p className="text-red-600">Error: {error}</p>}
        {!loading && !error && <PropertyGallery items={properties} />}
        </div>
      </Section>

      <Footer />
    </Page>
  );
};

export default Home;
