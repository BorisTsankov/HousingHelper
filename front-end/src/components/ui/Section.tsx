import React from "react";
import { Container } from "./Container";

type SectionProps = React.PropsWithChildren<{ title?: string; center?: boolean }>;

export const Section: React.FC<SectionProps> = ({ title, center, children }) => (
  <section className="py-12">
    <Container>
      {title && (
        <h2
          className={`text-2xl font-bold text-slate-900 ${
            center ? "text-center" : ""
          }`}
        >
          {title}
        </h2>
      )}
      <div className={title ? "mt-8" : ""}>{children}</div>
    </Container>
  </section>
);
