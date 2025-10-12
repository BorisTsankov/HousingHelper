import React from "react";
export const Card: React.FC<React.PropsWithChildren<{className?: string}>> = ({ children, className = "" }) => (
  <div className={`rounded-3xl border border-slate-200 bg-white shadow-sm ${className}`}>{children}</div>
);
