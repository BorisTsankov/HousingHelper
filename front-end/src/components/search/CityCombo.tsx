import React, { useEffect, useMemo, useRef, useState } from "react";
import { FILTER_FIELD_CLASS } from "../../styles/filterField";

type Option = { value: string; label: string };
type CityComboProps = {
  value?: string;
  onChange: (v: string | undefined) => void;
  options: Option[];
  placeholder?: string;
  limit?: number;
};

export const CityCombo: React.FC<CityComboProps> = ({
  value, onChange, options, placeholder = "City (type to search)", limit = 5,
}) => {
  const wrapRef = useRef<HTMLDivElement>(null);
  const [open, setOpen] = useState(false);
  const [input, setInput] = useState("");
  const [activeIndex, setActiveIndex] = useState(-1);

  useEffect(() => {
    const sel = options.find(o => o.value === value);
    setInput(sel?.label ?? "");
  }, [value, options]);

  const filtered = useMemo(() => {
    const q = input.trim().toLowerCase();
    if (!q) return [];
    const starts = options.filter(o => o.label.toLowerCase().startsWith(q));
    const contains = options.filter(
      o => !o.label.toLowerCase().startsWith(q) && o.label.toLowerCase().includes(q)
    );
    return [...starts, ...contains].slice(0, limit);
  }, [input, options, limit]);

  useEffect(() => {
    const onDoc = (e: MouseEvent) => {
      if (!wrapRef.current?.contains(e.target as Node)) {
        setOpen(false); setActiveIndex(-1);
      }
    };
    document.addEventListener("mousedown", onDoc);
    return () => document.removeEventListener("mousedown", onDoc);
  }, []);

  const select = (opt?: Option) => {
    if (!opt) { onChange(undefined); setInput(""); setOpen(false); setActiveIndex(-1); return; }
    onChange(opt.value); setInput(opt.label); setOpen(false); setActiveIndex(-1);
  };

  return (
    <div ref={wrapRef} className="relative">
      <input
        className={`${FILTER_FIELD_CLASS} pr-8`}
        type="text"
        placeholder={placeholder}
        value={input}
        onChange={e => { setInput(e.target.value); setOpen(true); setActiveIndex(-1); }}
        onFocus={() => { if (input.trim()) setOpen(true); }}
        onKeyDown={e => {
          if (e.key === "ArrowDown") { e.preventDefault(); setOpen(true); setActiveIndex(i => Math.min(i + 1, filtered.length - 1)); }
          else if (e.key === "ArrowUp") { e.preventDefault(); setActiveIndex(i => Math.max(i - 1, 0)); }
          else if (e.key === "Enter") { if (open && activeIndex >= 0) { e.preventDefault(); select(filtered[activeIndex]); } }
          else if (e.key === "Escape") { setOpen(false); setActiveIndex(-1); }
        }}
        aria-autocomplete="list"
        aria-expanded={open}
        aria-haspopup="listbox"
      />

      <span className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2">
        ▾
      </span>

      {open && (
        <ul
          role="listbox"
          className="absolute z-20 mt-1 w-full max-h-56 overflow-auto rounded-lg border border-gray-200 bg-white shadow-lg"
        >
          {input.trim() === "" && (
            <li className="px-3 py-2 text-gray-500 select-none">Type to search cities</li>
          )}
          {input.trim() !== "" && filtered.length === 0 && (
            <li className="px-3 py-2 text-gray-500 select-none">No matches</li>
          )}
          {filtered.map((opt, idx) => (
            <li
              key={opt.value}
              role="option"
              aria-selected={idx === activeIndex}
              className={`px-3 py-2 cursor-pointer ${idx === activeIndex ? "bg-gray-100" : ""}`}
              onMouseEnter={() => setActiveIndex(idx)}
              onMouseDown={(e) => e.preventDefault()}
              onClick={() => select(opt)}
            >
              {opt.label}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};
