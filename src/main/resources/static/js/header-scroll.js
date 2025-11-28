/* ======================
   BARRA SUPERIOR BLANCA
====================== */
.barra-top-blanca {
  width: 100%;
  height: 2rem;
  background: white;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1001;
}

/* ======================
   HEADER
====================== */
#header {
  background: rgba(255, 255, 255, 0); /* transparente */
  position: fixed;
  top: 2rem; /* debajo de la barra blanca */
  left: 0;
  width: 100%;
  height: 70px;
  z-index: 1000;
  display: flex;
  align-items: center;
  padding: 0 30px;
  transition: background 0.4s ease, box-shadow 0.4s ease;
  backdrop-filter: blur(8px);
}

#header.scroll-active {
  background: #ffffff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.25);
}

/* ======================
   HERO / IMAGEN DE FONDO
====================== */
.hero {
  background-image: url('/imagenes/img_2.png');
  background-size: cover;
  background-position: center top;
  background-repeat: no-repeat;
  min-height: 400px;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 0;
}

.hero::before {
  content: "";
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(10,122,99,0.3);
  z-index: 0;
}

/* ======================
   MAIN / CONTENIDO
====================== */
.cuerpo_indrugs {
  position: relative;
  z-index: 1;
  margin-top: 160px; /* espacio suficiente para barra + header */
  margin-bottom: 100px; /* espacio para footer */
}

/* ======================
   FOOTER
====================== */
footer {
  background: #ffffff;
  padding: 20px 0;
  text-align: center;
  border-top: 3px solid #0a7a63;
  font-family: 'Poppins', sans-serif;
  color: #222;
  position: relative; /* NO FIXED */
  width: 100%;
  z-index: 10;
}

/* ======================
   OTROS ESTILOS DE HEADER
====================== */
.cabeza_indrugs {
  width: 100%;
  color: #222;
}

.logo_nav_items {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 2.3rem;
  font-weight: 800;
  text-decoration: none;
  color: #ffffff;
  position: relative;
  z-index: 2;
}

.barra_2 {
  display: flex;
  gap: 20px;
  list-style: none;
  padding: 0;
  margin: 0;
  position: relative;
  z-index: 2;
}

.barra_2 a {
  text-decoration: none;
  font-size: 1rem;
  font-weight: 600;
  color: #ffffff;
  padding: 8px 14px;
  border-radius: 20px;
  background: rgba(10, 122, 99, 0.4);
  transition: background 0.3s ease, transform 0.2s ease;
}

.barra_2 a:hover {
  background: rgba(10, 122, 99, 0.6);
  transform: scale(1.06);
}

.barra_1 {
  display: flex;
  gap: 16px;
  list-style: none;
  padding: 0;
  margin: 0;
  position: relative;
  z-index: 2;
}

.barra_1 img {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  transition: transform 0.2s, filter 0.2s;
}

.barra_1 img:hover {
  transform: scale(1.18);
  filter: brightness(1.3);
}

/* GRAFICOS */
.contenedor-grafico {
  background: white;
  padding: 25px;
  border-radius: 12px;
  margin-bottom: 35px;
  box-shadow: 0 0 10px rgba(0,0,0,0.12);
}
