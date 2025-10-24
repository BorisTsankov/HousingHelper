import React from "react";
import { Facebook, Instagram, Linkedin } from "lucide-react";
import { Container } from "../ui/Container";

const Footer: React.FC = () => {
  return (
    <footer className="border-t border-slate-200 bg-blue-50/70 mt-16">
      <Container>
        <div className="py-8 text-center">
          <p className="text-sm text-slate-600">
            © {new Date().getFullYear()} HousingHelper. All rights reserved.
          </p>

          <div className="mt-4 flex justify-center gap-6 text-slate-600">
            <a href="https://www.facebook.com/boris.tsankov.77" aria-label="Facebook" className="hover:text-blue-600">
              <Facebook className="h-5 w-5" />
            </a>
            <a href="https://www.instagram.com/borists_" aria-label="Instagram" className="hover:text-blue-600">
              <Instagram className="h-5 w-5" />
            </a>
            <a href="https://www.linkedin.com/in/boris-tsankov-profile" aria-label="LinkedIn" className="hover:text-blue-600">
              <Linkedin className="h-5 w-5" />
            </a>
          </div>
        </div>
      </Container>
    </footer>
  );
};

export default Footer;
