-- Només s'executa quan el volum de PostgreSQL és buit.
-- Les taules i la importació de dades del directori s'afegiran en el desenvolupament.
CREATE SCHEMA IF NOT EXISTS ribera;


-- =====================================================================
-- Estructura del directori (Sprint 1) - SOLO TABLAS, SIN DATOS
-- Los valores de los CHECK coinciden con los de los ficheros de datos
-- (por ejemplo 'Públic', 'telèfon'), para poder importarlos tal cual.
-- =====================================================================

SET search_path TO ribera;

-- ---------------------------------------------------------------------
-- 1. FONTS (procedencia de los datos importados)
-- ---------------------------------------------------------------------
CREATE TABLE font (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codi_origen VARCHAR(20) UNIQUE,            -- F01, W03...
    referencia  TEXT NOT NULL                  -- URL o descripcion
);

-- ---------------------------------------------------------------------
-- 2. CATALOGOS (ampliables; el id es estable, el nombre puede cambiar)
--    Cada catalogo guarda el codigo de origen del fichero importado.
-- ---------------------------------------------------------------------
CREATE TABLE tipus_entitat (                   -- "forma_juridica" en los datos
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(150) NOT NULL UNIQUE,
    codi_origen VARCHAR(30) UNIQUE
);

CREATE TABLE tipus_centre (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(150) NOT NULL UNIQUE,
    codi_origen VARCHAR(30) UNIQUE
);

CREATE TABLE categoria_servei (                -- "Tipo" de SERVICIOS en el diagrama
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(150) NOT NULL UNIQUE,
    codi_origen VARCHAR(30) UNIQUE
);

CREATE TABLE collectiu (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(200) NOT NULL UNIQUE,
    codi_origen VARCHAR(30) UNIQUE
);

CREATE TABLE ambit_activitat (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(200) NOT NULL UNIQUE,
    codi_origen VARCHAR(30) UNIQUE
);

CREATE TABLE perfil_professional (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(200) NOT NULL UNIQUE,
    codi_origen VARCHAR(30) UNIQUE
);

-- ---------------------------------------------------------------------
-- 3. TERRITORIO (comarca se guarda una sola vez)
-- ---------------------------------------------------------------------
CREATE TABLE comarca (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL UNIQUE,
    codi_origen VARCHAR(30) UNIQUE,            -- COM_RA, COM_RB
    font_id     BIGINT REFERENCES font(id)
);

CREATE TABLE municipi (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(150) NOT NULL,
    comarca_id  BIGINT NOT NULL REFERENCES comarca(id),
    codi_origen VARCHAR(30) UNIQUE,            -- MUN01... (no es codigo INE)
    font_id     BIGINT REFERENCES font(id),
    UNIQUE (comarca_id, nom)
);

-- ---------------------------------------------------------------------
-- 4. ENTITAT (diagrama: Nom, Desc, Tipo, Públ/priv, ID clave, Tel, Web,
--    RRSS, Mail, Horario). Tel/Web/RRSS/Mail -> tabla contacte;
--    Horario -> tablas horari y franja_horaria.
-- ---------------------------------------------------------------------
CREATE TABLE entitat (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom               VARCHAR(200) NOT NULL,
    descripcio        TEXT,
    tipus_entitat_id  BIGINT REFERENCES tipus_entitat(id),   -- NULL = desconocido (no "altres")
    sector            VARCHAR(10) CHECK (sector IN ('Públic', 'Privat')),  -- NULL = desconocido
    imatge            VARCHAR(255),                           -- ruta/clave estable, max. 1 imagen
    -- direccion opcional (los datos de ejemplo la traen en algunas entidades)
    adreca            VARCHAR(255),
    municipi_id       BIGINT REFERENCES municipi(id),
    codi_postal       VARCHAR(5) CHECK (codi_postal ~ '^[0-9]{5}$'),
    adreca_font_id    BIGINT REFERENCES font(id),
    -- control interno
    estat_revisio     VARCHAR(20) NOT NULL DEFAULT 'pendent'
                      CHECK (estat_revisio IN ('pendent', 'en_revisio', 'revisat')),
    data_comprovacio  DATE,
    estat_publicacio  VARCHAR(20) NOT NULL DEFAULT 'esborrany'
                      CHECK (estat_publicacio IN ('esborrany', 'publicat')),
    notes_internes    TEXT,
    font_id           BIGINT REFERENCES font(id),
    font_importacio   VARCHAR(50),
    codi_origen       VARCHAR(30),
    UNIQUE (font_importacio, codi_origen)
);

CREATE TABLE entitat_ambit (
    entitat_id  BIGINT NOT NULL REFERENCES entitat(id) ON DELETE CASCADE,
    ambit_id    BIGINT NOT NULL REFERENCES ambit_activitat(id),
    PRIMARY KEY (entitat_id, ambit_id)
);

CREATE TABLE entitat_perfil (
    entitat_id  BIGINT NOT NULL REFERENCES entitat(id) ON DELETE CASCADE,
    perfil_id   BIGINT NOT NULL REFERENCES perfil_professional(id),
    PRIMARY KEY (entitat_id, perfil_id)
);

-- ---------------------------------------------------------------------
-- 5. CENTRE (diagrama: Nom, Desc, Tipo, Dir, Contacto, Horario)
--    "titula" y "gestiona": una entidad -> muchos centros (FK en centre).
--    Titular y gestor pueden coincidir y pueden ser NULL (no acreditados).
-- ---------------------------------------------------------------------
CREATE TABLE centre (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom               VARCHAR(200) NOT NULL,
    descripcio        TEXT,
    tipus_centre_id   BIGINT REFERENCES tipus_centre(id),     -- NULL = desconocido
    titular_id        BIGINT REFERENCES entitat(id),          -- "titula"
    gestor_id         BIGINT REFERENCES entitat(id),          -- "gestiona"
    imatge            VARCHAR(255),
    -- direccion (el CP no identifica el municipio)
    municipi_id       BIGINT REFERENCES municipi(id),
    nucli_poblacio    VARCHAR(150),
    codi_postal       VARCHAR(5) CHECK (codi_postal ~ '^[0-9]{5}$'),
    via               VARCHAR(255),
    numero            VARCHAR(30),
    complements       VARCHAR(255),
    adreca_font_id    BIGINT REFERENCES font(id),
    -- coordenadas (NULL si se desconocen; nunca 0,0)
    latitud           NUMERIC(9,6) CHECK (latitud  BETWEEN -90  AND 90),
    longitud          NUMERIC(9,6) CHECK (longitud BETWEEN -180 AND 180),
    precisio_punt     VARCHAR(30)
                      CHECK (precisio_punt IN ('exacta', 'aproximada', 'punt_publicat', 'proposta_automatica')),
    font_punt_id      BIGINT REFERENCES font(id),
    -- control interno
    estat_revisio     VARCHAR(20) NOT NULL DEFAULT 'pendent'
                      CHECK (estat_revisio IN ('pendent', 'en_revisio', 'revisat')),
    data_comprovacio  DATE,
    estat_publicacio  VARCHAR(20) NOT NULL DEFAULT 'esborrany'
                      CHECK (estat_publicacio IN ('esborrany', 'publicat')),
    notes_internes    TEXT,
    font_id           BIGINT REFERENCES font(id),
    font_importacio   VARCHAR(50),
    codi_origen       VARCHAR(30),
    UNIQUE (font_importacio, codi_origen),
    -- latitud y longitud van juntas, y si hay punto se indica su precision
    CHECK ((latitud IS NULL) = (longitud IS NULL)),
    CHECK (latitud IS NULL OR precisio_punt IS NOT NULL)
);

-- ---------------------------------------------------------------------
-- 6. SERVEI (diagrama: Nom, Desc, Tipo)
-- ---------------------------------------------------------------------
CREATE TABLE servei (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom               VARCHAR(250) NOT NULL,
    descripcio        TEXT,
    categoria_id      BIGINT REFERENCES categoria_servei(id),
    via_acces         TEXT,                                   -- cita, derivacion... (texto breve)
    imatge            VARCHAR(255),
    -- control interno
    estat_revisio     VARCHAR(20) NOT NULL DEFAULT 'pendent'
                      CHECK (estat_revisio IN ('pendent', 'en_revisio', 'revisat')),
    data_comprovacio  DATE,
    estat_publicacio  VARCHAR(20) NOT NULL DEFAULT 'esborrany'
                      CHECK (estat_publicacio IN ('esborrany', 'publicat')),
    notes_internes    TEXT,
    font_id           BIGINT REFERENCES font(id),
    font_importacio   VARCHAR(50),
    codi_origen       VARCHAR(30),
    UNIQUE (font_importacio, codi_origen)
);

CREATE TABLE servei_collectiu (
    servei_id    BIGINT NOT NULL REFERENCES servei(id) ON DELETE CASCADE,
    collectiu_id BIGINT NOT NULL REFERENCES collectiu(id),
    PRIMARY KEY (servei_id, collectiu_id)
);

-- Modalidades combinables. Valores fijos, sin tabla de catalogo.
CREATE TABLE servei_modalitat (
    servei_id  BIGINT NOT NULL REFERENCES servei(id) ON DELETE CASCADE,
    modalitat  VARCHAR(20) NOT NULL
               CHECK (modalitat IN ('presencial', 'domiciliaria', 'telefonica', 'en_linia')),
    PRIMARY KEY (servei_id, modalitat)
);

-- Territorio atendido: un municipio concreto O una comarca completa.
CREATE TABLE servei_cobertura (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    servei_id    BIGINT NOT NULL REFERENCES servei(id) ON DELETE CASCADE,
    municipi_id  BIGINT REFERENCES municipi(id),
    comarca_id   BIGINT REFERENCES comarca(id),
    CHECK ((municipi_id IS NOT NULL) <> (comarca_id IS NOT NULL))
);
CREATE UNIQUE INDEX uq_cobertura_municipi ON servei_cobertura (servei_id, municipi_id) WHERE municipi_id IS NOT NULL;
CREATE UNIQUE INDEX uq_cobertura_comarca  ON servei_cobertura (servei_id, comarca_id)  WHERE comarca_id  IS NOT NULL;

-- ---------------------------------------------------------------------
-- 7. PRESTACIONS (diagrama: la entidad presta un servicio)
--    Un servicio puede existir sin prestacion (prestador desconocido).
-- ---------------------------------------------------------------------
CREATE TABLE prestacio (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    servei_id   BIGINT NOT NULL REFERENCES servei(id) ON DELETE CASCADE,
    entitat_id  BIGINT NOT NULL REFERENCES entitat(id),
    font_id     BIGINT REFERENCES font(id),
    UNIQUE (servei_id, entitat_id)
);

-- "Se ofrece en" (en la pizarra: "Ofertas en centros"):
-- centros donde una prestacion concreta se ofrece (0 o mas).
CREATE TABLE prestacio_centre (
    prestacio_id BIGINT NOT NULL REFERENCES prestacio(id) ON DELETE CASCADE,
    centre_id    BIGINT NOT NULL REFERENCES centre(id),
    font_id      BIGINT REFERENCES font(id),
    PRIMARY KEY (prestacio_id, centre_id)
);

-- ---------------------------------------------------------------------
-- 8. CONTACTES (telefono, correo, web y redes; varios por propietario)
--    Cada contacto pertenece a UNA entidad, UN centro o UN servicio.
-- ---------------------------------------------------------------------
CREATE TABLE contacte (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entitat_id  BIGINT REFERENCES entitat(id) ON DELETE CASCADE,
    centre_id   BIGINT REFERENCES centre(id)  ON DELETE CASCADE,
    servei_id   BIGINT REFERENCES servei(id)  ON DELETE CASCADE,
    tipus       VARCHAR(20) NOT NULL
                CHECK (tipus IN ('telèfon', 'correu', 'web', 'xarxa_social')),
    valor       VARCHAR(500) NOT NULL,
    font_id     BIGINT REFERENCES font(id),
    CHECK (num_nonnulls(entitat_id, centre_id, servei_id) = 1)
);

-- ---------------------------------------------------------------------
-- 9. HORARIS (varios intervalos por dia, horas libres)
--    Sin filas de horario = horario desconocido (no "cerrado").
-- ---------------------------------------------------------------------
CREATE TABLE horari (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entitat_id     BIGINT REFERENCES entitat(id) ON DELETE CASCADE,
    centre_id      BIGINT REFERENCES centre(id)  ON DELETE CASCADE,
    servei_id      BIGINT REFERENCES servei(id)  ON DELETE CASCADE,
    tipus          VARCHAR(50),
    descripcio     TEXT,                                -- excepciones, "con cita"...
    zona_horaria   VARCHAR(50) NOT NULL DEFAULT 'Europe/Madrid',
    vigent_des_de  DATE,
    vigent_fins_a  DATE,
    font_id        BIGINT REFERENCES font(id),
    CHECK (num_nonnulls(entitat_id, centre_id, servei_id) = 1),
    CHECK (vigent_des_de IS NULL OR vigent_fins_a IS NULL OR vigent_des_de <= vigent_fins_a)
);

CREATE TABLE franja_horaria (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    horari_id         BIGINT NOT NULL REFERENCES horari(id) ON DELETE CASCADE,
    dia_setmana       SMALLINT NOT NULL CHECK (dia_setmana BETWEEN 1 AND 7),  -- 1=lunes ... 7=domingo
    hora_inici        TIME NOT NULL,
    hora_fi           TIME NOT NULL,
    final_dia_seguent BOOLEAN NOT NULL DEFAULT FALSE,
    CHECK (final_dia_seguent OR hora_inici < hora_fi)
);

-- ---------------------------------------------------------------------
-- 10. INDICES para las consultas de filtros
-- ---------------------------------------------------------------------
CREATE INDEX ix_centre_municipi    ON centre (municipi_id);
CREATE INDEX ix_centre_tipus       ON centre (tipus_centre_id);
CREATE INDEX ix_centre_titular     ON centre (titular_id);
CREATE INDEX ix_centre_gestor      ON centre (gestor_id);
CREATE INDEX ix_municipi_comarca   ON municipi (comarca_id);
CREATE INDEX ix_prestacio_entitat  ON prestacio (entitat_id);
CREATE INDEX ix_prestcentre_centre ON prestacio_centre (centre_id);
CREATE INDEX ix_servei_categoria   ON servei (categoria_id);
CREATE INDEX ix_cobertura_servei   ON servei_cobertura (servei_id);
CREATE INDEX ix_contacte_entitat   ON contacte (entitat_id);
CREATE INDEX ix_contacte_centre    ON contacte (centre_id);
CREATE INDEX ix_contacte_servei    ON contacte (servei_id);
CREATE INDEX ix_horari_entitat     ON horari (entitat_id);
CREATE INDEX ix_horari_centre      ON horari (centre_id);
CREATE INDEX ix_horari_servei      ON horari (servei_id);
CREATE INDEX ix_franja_horari      ON franja_horaria (horari_id);