import { Routes, Route } from "react-router-dom";
import Register from "./Register";

export default function App() {
    return (
        <Routes>
            <Route path="/register" element={<Register />} />
        </Routes>
    );
}
