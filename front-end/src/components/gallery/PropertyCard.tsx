import React from "react";
import type { Property } from "../../types/property";

export const PropertyCard: React.FC<{ item: Property }> = ({ item }) => (
  <figure className="overflow-hidden rounded-3xl border border-slate-200 shadow-sm">
    <img
      src={item.image}
      alt={item.title}
      className="h-64 w-full object-cover transition-transform duration-300 hover:scale-105"
      loading="lazy"
    />
    <figcaption className="p-4">
      <h3 className="text-lg font-semibold text-slate-800">{item.title}</h3>
      <p className="text-slate-600 text-sm">{item.location}</p>
      <p className="text-blue-600 font-semibold mt-2">{item.price}</p>
    </figcaption>
  </figure>
);
