import React from "react";
import { Link } from "react-router-dom";
import type { Property } from "../../types/property";

export const PropertyCard: React.FC<{ item: Property }> = ({ item }) => (
  <Link to={`/listings/${item.id}`} className="block group h-full">
    <figure className="flex flex-col h-full overflow-hidden rounded-3xl border border-slate-200 shadow-sm transition hover:shadow-md">
      <img
        src={item.image}
        alt={item.title}
        className="h-64 w-full object-cover transition-transform duration-300 group-hover:scale-105"
        loading="lazy"
      />
      <figcaption className="flex flex-col justify-between flex-grow p-4 bg-white">
        <div>
          <h3 className="text-lg font-semibold text-slate-800 group-hover:text-blue-600 transition">
            {item.title}
          </h3>
          <p className="text-slate-600 text-sm">{item.location}</p>
        </div>
        <p className="text-blue-600 font-semibold mt-3">{item.price}</p>
      </figcaption>
    </figure>
  </Link>
);
