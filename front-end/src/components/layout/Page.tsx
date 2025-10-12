import React, { PropsWithChildren } from "react";

type PageProps = {
  className?: string;
};

export default function Page({ children, className }: PropsWithChildren<PageProps>) {
  return (
    <div className={`min-h-screen flex flex-col ${className ?? ""}`}>
      {children}
    </div>
  );
}
