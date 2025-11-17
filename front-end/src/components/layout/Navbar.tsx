import React from "react";
import { Home, Mail, User } from "lucide-react";
import { Button } from "../ui/Button";
import { Container } from "../ui/Container";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const Navbar: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated, user, logout } = useAuth();

  const handleUserClick = () => {
    if (!isAuthenticated) {
      navigate("/login");
    } else {
      navigate("/profile");
    }
  };

  const handleLogout = async () => {
    await logout();
    navigate("/register");
  };

  return (
    <header className="sticky top-0 z-40 bg-white/80 backdrop-blur border-b border-slate-200">
      <Container>
        <nav className="flex items-center justify-between py-3">
          <Link to="/" className="flex items-center gap-2 group">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-blue-600 text-white transition group-hover:bg-blue-700">
              <Home className="h-5 w-5" />
            </div>
            <span className="text-lg font-semibold text-slate-800 group-hover:text-slate-900">
              HousingHelper
            </span>
          </Link>

          <div className="flex items-center gap-4">
            <Button className="flex items-center gap-2" rounded="full">
              <Mail className="h-4 w-4" />
              Contact Us
            </Button>

            {isAuthenticated ? (
              <div className="flex items-center gap-2">
                <button
                  onClick={handleUserClick}
                  className="flex items-center gap-2 rounded-full px-3 py-1 hover:bg-slate-100 transition"
                >
                  <User className="h-5 w-5 text-slate-700" />
                  <span className="text-sm text-slate-700">
                    {user?.name || user?.email}
                  </span>
                </button>
                <button
                  onClick={handleLogout}
                  className="text-sm text-slate-500 hover:text-slate-800"
                >
                  Logout
                </button>
              </div>
            ) : (
              <button
                onClick={handleUserClick}
                className="p-2 rounded-full hover:bg-slate-100 transition"
              >
                <User className="h-6 w-6 text-slate-700" />
              </button>
            )}
          </div>
        </nav>
      </Container>
    </header>
  );
};

export default Navbar;
