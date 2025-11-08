import { Routes, Route } from "react-router-dom";
import Register from "./pages/Register";
import Home from "./pages/Home"
import Listings from "./pages/Listings"
import ListingDetails from "./pages/ListingDetails"
import Map from "./pages/Map"

export default function App() {
    return (
        <Routes>
            <Route path="/register" element={<Register />} />
            <Route path="/" element={<Home />} />
            <Route path="/listings" element={<Listings />} />
            <Route path="/listings/:id" element={<ListingDetails />} />

        </Routes>
    );
}
