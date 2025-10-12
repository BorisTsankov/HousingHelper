import React from "react";
import { Home, Mail } from "lucide-react";
import { Button } from "../ui/Button";
import { Container } from "../ui/Container";

const Navbar: React.FC = () => {
  return (
    <header className="sticky top-0 z-40 bg-white/80 backdrop-blur border-b border-slate-200">
      <Container>
        <nav className="flex items-center justify-between py-3">
          {/* Logo + Title */}
          <div className="flex items-center gap-2">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-blue-600 text-white">
              <Home className="h-5 w-5" />
            </div>
            <span className="text-lg font-semibold text-slate-800">
              HousingHelper
            </span>
          </div>

          {/* Contact button */}
          <Button className="flex items-center gap-2" rounded="full">
            <Mail className="h-4 w-4" />
            Contact Us
          </Button>
        </nav>
      </Container>
    </header>
  );
};

export default Navbar;
