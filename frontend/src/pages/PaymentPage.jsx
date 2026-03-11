import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppContext } from "../state/AppContext";

const initialForm = {
  cardNumber: "",
  expiration: "",
  cvv: "",
  email: "",
  name: "",
  documentType: "DNI",
  documentNumber: ""
};

export default function PaymentPage() {
  const { apiBase, cart, cartTotal, user, token, setCart } = useAppContext();
  const [form, setForm] = useState({
    ...initialForm,
    email: user?.email ?? "",
    name: user?.name ?? ""
  });
  const [success, setSuccess] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [paymentError, setPaymentError] = useState("");
  const navigate = useNavigate();

  const onChange = (event) => {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setPaymentError("");

    try {
      const completeResponse = await fetch(`${apiBase.gateway}/checkout`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          ...form,
          amount: cartTotal,
          items: cart
        })
      });

      if (!completeResponse.ok) {
        const errorText = await completeResponse.text();
        throw new Error(errorText || "La transaccion fue rechazada.");
      }

      setCart([]);
      setSuccess(true);
    } catch (error) {
      setPaymentError(error.message);
    }

    setSubmitting(false);
  };

  return (
    <section className="payment-layout">
      <form className="panel payment-form" onSubmit={handleSubmit}>
        <p className="eyebrow">Pago</p>
        <h1>Finaliza tu compra</h1>
        <label>
          Numero de tarjeta
          <input name="cardNumber" pattern="\d{16}" maxLength="16" required value={form.cardNumber} onChange={onChange} />
        </label>
        <div className="split-fields">
          <label>
            Fecha de expiracion
            <input name="expiration" placeholder="MM/YY" required value={form.expiration} onChange={onChange} />
          </label>
          <label>
            CVV
            <input name="cvv" pattern="\d{3,4}" maxLength="4" required value={form.cvv} onChange={onChange} />
          </label>
        </div>
        <label>
          Correo electronico
          <input name="email" type="email" required value={form.email} onChange={onChange} />
        </label>
        <label>
          Nombre
          <input name="name" required value={form.name} onChange={onChange} />
        </label>
        <div className="split-fields">
          <label>
            Tipo de documento
            <select name="documentType" value={form.documentType} onChange={onChange}>
              <option value="DNI">DNI</option>
              <option value="CE">CE</option>
              <option value="PASAPORTE">Pasaporte</option>
            </select>
          </label>
          <label>
            Numero de documento
            <input name="documentNumber" required value={form.documentNumber} onChange={onChange} />
          </label>
        </div>
        <button className="primary-button" type="submit" disabled={!cart.length || submitting}>
          {submitting ? "Procesando..." : `Pagar S/ ${cartTotal.toFixed(2)}`}
        </button>
        {paymentError && <p>{paymentError}</p>}
      </form>

      <aside className="panel secondary-panel">
        <h2>Resumen</h2>
        {cart.map((item) => (
          <div key={item.id} className="cart-row">
            <span>{item.name} x{item.quantity}</span>
            <strong>S/ {(item.quantity * item.price).toFixed(2)}</strong>
          </div>
        ))}
      </aside>

      {success && (
        <div className="modal-backdrop">
          <div className="modal-card">
            <h2>Compra correcta</h2>
            <button className="primary-button" type="button" onClick={() => navigate("/")}>
              Volver al inicio
            </button>
          </div>
        </div>
      )}
    </section>
  );
}
