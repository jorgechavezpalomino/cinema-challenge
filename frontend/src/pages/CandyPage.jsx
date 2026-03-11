import { useNavigate } from "react-router-dom";
import { useAppContext } from "../state/AppContext";

export default function CandyPage() {
  const { products, cart, addToCart, updateQuantity, cartTotal } = useAppContext();
  const navigate = useNavigate();

  return (
    <section className="catalog-layout">
      <div className="catalog-main">
        <div className="catalog-header panel">
          <p className="eyebrow">Dulceria</p>
          <h1>Arma tu combo antes de llegar al cine</h1>
          <p>Elige uno o varios productos y revisa el total en tiempo real antes de continuar al pago.</p>
        </div>

        <div className="catalog-grid">
        {products.map((product) => (
          <article key={product.id} className="product-card">
            <div className="product-copy">
              <h2>{product.name}</h2>
              <p>{product.description}</p>
            </div>

            <div className="product-footer">
              <strong className="product-price">S/ {product.price.toFixed(2)}</strong>
              <button className="primary-button product-button" type="button" onClick={() => addToCart(product)}>
                Agregar
              </button>
            </div>
          </article>
        ))}
        </div>
      </div>

      <aside className="summary-panel">
        <p className="eyebrow">Resumen</p>
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
