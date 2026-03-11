import { useNavigate } from "react-router-dom";
import { useAppContext } from "../state/AppContext";

export default function CandyPage() {
  const { products, cart, addToCart, updateQuantity, cartTotal } = useAppContext();
  const navigate = useNavigate();

  return (
    <section className="catalog-layout">
      <div className="catalog-grid">
        {products.map((product) => (
          <article key={product.id} className="product-card">
            <h2>{product.name}</h2>
            <p>{product.description}</p>
            <strong>S/ {product.price.toFixed(2)}</strong>
            <button className="primary-button" type="button" onClick={() => addToCart(product)}>
              Agregar
            </button>
          </article>
        ))}
      </div>

      <aside className="summary-panel">
        <h2>Total</h2>
        {cart.length === 0 && <p>No hay productos seleccionados.</p>}
        {cart.map((item) => (
          <div key={item.id} className="cart-row">
            <span>{item.name}</span>
            <div className="quantity-box">
              <button type="button" onClick={() => updateQuantity(item.id, -1)}>
                -
              </button>
              <span>{item.quantity}</span>
              <button type="button" onClick={() => updateQuantity(item.id, 1)}>
                +
              </button>
            </div>
          </div>
        ))}
        <p className="total-amount">S/ {cartTotal.toFixed(2)}</p>
        <button className="primary-button" type="button" disabled={!cart.length} onClick={() => navigate("/payment")}>
          Continuar
        </button>
      </aside>
    </section>
  );
}
