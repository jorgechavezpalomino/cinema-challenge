import { NavLink, Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import CandyPage from "./pages/CandyPage";
import PaymentPage from "./pages/PaymentPage";

const menuItems = [
  { to: "/", label: "Home" },
  { to: "/candystore", label: "Dulceria" },
  { to: "/login", label: "Login" }
];

export default function App() {
  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark">C</span>
          <div>
            <strong>Cinema Checkout</strong>
            <small>Reto FullStack</small>
          </div>
        </div>
        <nav className="menu">
          {menuItems.map((item) => (
            <NavLink key={item.to} to={item.to} className={({ isActive }) => (isActive ? "menu-link active" : "menu-link")}>
              {item.label}
            </NavLink>
          ))}
        </nav>
      </header>

      <main className="page-container">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/candystore" element={<CandyPage />} />
          <Route path="/payment" element={<PaymentPage />} />
        </Routes>
      </main>
    </div>
  );
}
