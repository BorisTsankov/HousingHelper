import React from "react";
import { SlidersHorizontal } from "lucide-react"; // optional, remove if not installed

type Props = { title?: string; children: React.ReactNode; className?: string };

export const FiltersPanel: React.FC<Props> = ({
  title = "Search & Filters",
  children,
  className = "",
}) => (
  <aside
    className={[
      "md:sticky md:top-24 h-fit",
      "rounded-2xl border border-gray-200 bg-white/90 backdrop-blur shadow-xl ring-1 ring-black/5",
      "overflow-hidden",
      className,
    ].join(" ")}
    aria-label={title}
  >
    <div className="bg-gradient-to-b from-gray-50 to-white border-b px-4 py-3">
      <div className="flex items-center gap-2">
        <SlidersHorizontal className="size-4 opacity-70" />
        <h2 className="text-sm font-semibold tracking-wide">{title}</h2>
      </div>
    </div>

    <div className="px-4 py-4">
      <div className="space-y-4">{children}</div>
    </div>

    <div className="h-px bg-gradient-to-r from-transparent via-gray-200 to-transparent" />
  </aside>
);

export default FiltersPanel;
