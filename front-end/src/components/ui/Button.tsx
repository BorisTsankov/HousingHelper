import React from "react";
type Props = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: "primary" | "ghost";
  rounded?: "md" | "full";
};
export const Button: React.FC<Props> = ({
  className = "",
  variant = "primary",
  rounded = "full",
  ...rest
}) => {
  const base = "inline-flex items-center justify-center font-semibold px-6 py-3 focus:outline-none focus:ring-2 focus:ring-offset-2";
  const variants = {
    primary: "bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500",
    ghost: "bg-transparent text-slate-700 hover:bg-slate-100 focus:ring-slate-400",
  };
  const radii = { md: "rounded-md", full: "rounded-full" };
  return <button className={`${base} ${variants[variant]} ${radii[rounded]} ${className}`} {...rest} />;
};
