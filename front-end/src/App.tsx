import { Routes, Route } from "react-router-dom";
import Register from "./pages/Register";
import Home from "./pages/Home";
import Listings from "./pages/Listings";
import ListingDetails from "./pages/ListingDetails";
import Login from "./pages/Login";
import Profile from "./pages/Profile";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/routing/ProtectedRoute";

export default function App() {
  return (
    <AuthProvider>

      <Routes>
          <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<Home />} />

        <Route
          path="/listings"
          element={
            <ProtectedRoute>
              <Listings />
            </ProtectedRoute>
          }
        />

        <Route
          path="/listings/:id"
          element={
            <ProtectedRoute>
              <ListingDetails />
            </ProtectedRoute>
          }
        />

           <Route
                    path="/profile"
                    element={
                      <ProtectedRoute>
                        <Profile />
                      </ProtectedRoute>
                    }
                  />
                </Routes>
              </AuthProvider>
  );
}
