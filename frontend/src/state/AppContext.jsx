import { createContext, useContext, useEffect, useState } from "react";

const AppContext = createContext(null);
const API_BASE = {
  gateway: import.meta.env.VITE_GATEWAY_API ?? "http://localhost:8080/api"
};

export function AppProvider({ children }) {
  const [premieres, setPremieres] = useState([]);
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState([]);
  const [user, setUser] = useState(null);
  const [token, setToken] = useState("");

  useEffect(() => {
    fetch(`${API_BASE.gateway}/premieres`)
      .then((response) => response.json())
      .then(setPremieres)
      .catch(() => setPremieres([]));

    fetch(`${API_BASE.gateway}/candystore`)
      .then((response) => response.json())
      .then(setProducts)
      .catch(() => setProducts([]));
  }, []);

  const addToCart = (product) => {
    setCart((current) => {
      const existing = current.find((item) => item.id === product.id);
      if (existing) {
        return current.map((item) => (item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item));
      }
      return [...current, { ...product, quantity: 1 }];
    });
  };

  const updateQuantity = (productId, delta) => {
    setCart((current) =>
      current
        .map((item) => (item.id === productId ? { ...item, quantity: Math.max(0, item.quantity + delta) } : item))
        .filter((item) => item.quantity > 0)
    );
  };

  const cartTotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const value = {
    apiBase: API_BASE,
    premieres,
    products,
    cart,
    cartTotal,
    addToCart,
    updateQuantity,
    user,
    setUser,
    token,
    setToken,
    setCart
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export function useAppContext() {
  return useContext(AppContext);
}
