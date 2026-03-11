import { useNavigate } from "react-router-dom";
import { useAppContext } from "../state/AppContext";

export default function HomePage() {
  const { premieres } = useAppContext();
  const navigate = useNavigate();

  return (
    <section className="home-list">
      <div className="hero-copy home-header">
        <p className="eyebrow">Premieres</p>
        <h1>Reserva tu experiencia antes de llegar al cine</h1>
        <p>Selecciona cualquiera de las peliculas para continuar con el flujo de compra.</p>
      </div>

      <div className="movie-list">
        {premieres.map((premiere) => (
          <article key={premiere.id} className="movie-row">
            <button className="poster-card movie-poster" onClick={() => navigate("/login")}>
              <img src={premiere.imageUrl} alt={premiere.title} />
            </button>

            <div className="hero-copy movie-copy">
              <p className="eyebrow">Pelicula</p>
            <h2>{premiere.title}</h2>
              <p>{premiere.description}</p>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
