import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppContext } from "../state/AppContext";

export default function LoginPage() {
  const { apiBase, setUser, setToken } = useAppContext();
  const [welcomeName, setWelcomeName] = useState("");
  const [googleReady, setGoogleReady] = useState(false);
  const [loginError, setLoginError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const clientId =
      import.meta.env.VITE_GOOGLE_CLIENT_ID ??
      "381774390951-c5o208n82ehm87ph3mltuispajpj2b8o.apps.googleusercontent.com";

    const renderGoogleButton = () => {
      if (!window.google?.accounts?.id) {
        return false;
      }

      window.google.accounts.id.initialize({
        client_id: clientId,
        callback: async ({ credential }) => {
          setLoginError("");
          try {
            const response = await fetch(`${apiBase.gateway}/auth/google`, {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ credential }),
            });

            if (!response.ok) {
              const errorText = await response.text();
              throw new Error(
                errorText || "No se pudo validar el login con Google.",
              );
            }

            const data = await response.json();
            setUser(data.user);
            setToken(data.token);
            setWelcomeName(data.user.name);
          } catch (error) {
            setLoginError(error.message);
          }
        },
      });

      const container = document.getElementById("google-signin");
      if (!container) {
        return false;
      }

      container.innerHTML = "";
      window.google.accounts.id.renderButton(container, {
        theme: "outline",
        size: "large",
        shape: "pill",
        text: "signin_with",
        width: 280,
      });
      setGoogleReady(true);
      return true;
    };

    if (renderGoogleButton()) {
      return undefined;
    }

    const intervalId = window.setInterval(() => {
      if (renderGoogleButton()) {
        window.clearInterval(intervalId);
      }
    }, 300);

    return () => window.clearInterval(intervalId);
  }, [apiBase.gateway, setToken, setUser]);

  const continueAsGuest = async () => {
    const response = await fetch(`${apiBase.gateway}/auth/guest`, {
      method: "POST",
    });
    const data = await response.json();
    setUser(data.user);
    setToken(data.token);
    navigate("/candystore");
  };

  return (
    <section className="login-layout">
      <div className="panel google-login-panel">
        <p className="eyebrow">Login</p>
        <h1>Ingresa con Google</h1>
        <p>
          Usa el boton oficial de Google para autenticarte y continuar con la
          compra.
        </p>
        <div id="google-signin" className="google-signin-slot" />
        {!googleReady && <p>Cargando Google Sign-In...</p>}
        {loginError && <p>{loginError}</p>}
      </div>

      <div className="panel secondary-panel">
        <h2>Ingreso rapido</h2>
        <p>Si no deseas autenticarte, continua como invitado.</p>
        <button
          className="ghost-button"
          type="button"
          onClick={continueAsGuest}
        >
          Invitado
        </button>
      </div>

      {welcomeName && (
        <div className="modal-backdrop">
          <div className="modal-card">
            <h2>Bienvenido, {welcomeName}</h2>
            <button
              className="primary-button"
              type="button"
              onClick={() => navigate("/candystore")}
            >
              Aceptar
            </button>
          </div>
        </div>
      )}
    </section>
  );
}
