import { Routes, Route } from "react-router-dom";
import Register from "./pages/Register";
import Home from "./pages/Home"
import Listings from "./pages/Listings"

export default function App() {
    return (
        <Routes>
            <Route path="/register" element={<Register />} />
            <Route path="/" element={<Home />} />
            <Route path="/listings" element={<Listings />} />

        </Routes>
    );
}
