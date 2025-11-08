import React from "react";

type Props = React.SelectHTMLAttributes<HTMLSelectElement>;

export const Select = React.forwardRef<HTMLSelectElement, Props>(
  ({ className = "", children, ...rest }, ref) => (
    <select ref={ref} className={className} {...rest}>
      {children}
    </select>
  )
);
Select.displayName = "Select";
