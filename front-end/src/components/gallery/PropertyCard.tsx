import React from "react";
import { Link } from "react-router-dom"; // ✅ you need this import
import type { Property } from "../../types/property";

export const PropertyCard: React.FC<{ item: Property }> = ({ item }) => (
  <Link to={`/listings/${item.id}`} className="block group">
    <figure className="overflow-hidden rounded-3xl border border-slate-200 shadow-sm transition hover:shadow-md">
      <img
        src={item.image}
        alt={item.title}
        className="h-64 w-full object-cover transition-transform duration-300 group-hover:scale-105"
        loading="lazy"
      />
      <figcaption className="p-4 bg-white">
        <h3 className="text-lg font-semibold text-slate-800 group-hover:text-blue-600 transition">
          {item.title}
        </h3>
        <p className="text-slate-600 text-sm">{item.location}</p>
        <p className="text-blue-600 font-semibold mt-2">{item.price}</p>
      </figcaption>
    </figure>
  </Link>
);
