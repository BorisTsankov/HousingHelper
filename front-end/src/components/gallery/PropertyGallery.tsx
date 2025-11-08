import React from "react";
import type { Property } from "../../types/property";
import { PropertyCard } from "./PropertyCard";

type Props = { items: Property[] };

export const PropertyGallery: React.FC<Props> = ({ items }) => {
  if (!items?.length)
    return <p className="text-center text-slate-500">No properties found.</p>;

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 items-stretch">
      {items.map((item) => (
        <div key={item.id} className="h-full">
          <PropertyCard item={item} />
        </div>
      ))}
    </div>
  );
};
