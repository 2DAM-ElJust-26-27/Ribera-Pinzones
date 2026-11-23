-- Només s'executa quan el volum de PostgreSQL és buit.
-- Les taules i la importació de dades del directori s'afegiran en el desenvolupament.
CREATE SCHEMA IF NOT EXISTS ribera;


-- =====================================================================
-- Estructura del directori (Sprint 1) - SOLO TABLAS, SIN DATOS
-- Tablas y relaciones: segun el diagrama (Entitat, Centre, Servei,
-- Prestacio). Campos: segun la actividad "Informacio del directori".
-- Se ejecuta tras 00_esquema.sql (orden alfabetico), solo con el
-- volumen de PostgreSQL vacio.
-- =====================================================================
SET search_path TO ribera;
 
-- ---------------------------------------------------------------------
-- 1. CATALOGOS (ampliables; el id es estable y no depende del nombre)
-- ---------------------------------------------------------------------
CREATE TABLE tipus_entitat (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom  VARCHAR(150) NOT NULL UNIQUE
);
 
CREATE TABLE tipus_centre (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom  VARCHAR(150) NOT NULL UNIQUE
);
 
CREATE TABLE categoria_servei (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom  VARCHAR(150) NOT NULL UNIQUE
);
 
CREATE TABLE collectiu (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom  VARCHAR(150) NOT NULL UNIQUE
);
 
CREATE TABLE ambit_activitat (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom  VARCHAR(150) NOT NULL UNIQUE
);
 
CREATE TABLE perfil_professional (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom  VARCHAR(150) NOT NULL UNIQUE
);
 
-- ---------------------------------------------------------------------
-- 2. TERRITORIO (una comarca tiene varios municipios)
-- ---------------------------------------------------------------------
CREATE TABLE comarca (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom  VARCHAR(100) NOT NULL UNIQUE
);
 
CREATE TABLE municipi (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom         VARCHAR(100) NOT NULL,
    comarca_id  BIGINT NOT NULL REFERENCES comarca(id),
    UNIQUE (comarca_id, nom)
);
 
-- ---------------------------------------------------------------------
-- 3. ENTITAT
--    Telefono y correo son campos independientes (ninguno obligatorio).
--    Tipo desconocido = NULL (no equivale a "altres").
--    Sector independiente del tipo (no se deduce).
-- ---------------------------------------------------------------------
CREATE TABLE entitat (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom               VARCHAR(200) NOT NULL,
    descripcio        TEXT,                                   -- descripcion breve
    tipus_entitat_id  BIGINT REFERENCES tipus_entitat(id),
    sector            VARCHAR(10) CHECK (sector IN ('Públic', 'Privat')),
    telefon           VARCHAR(30),
    correu            VARCHAR(255),
    web               VARCHAR(500),
    imatge            VARCHAR(255),                           -- ruta/clave estable (max. 1 imagen)
    -- control interno (no se muestra en la consulta publica)
    estat_revisio     VARCHAR(20) NOT NULL DEFAULT 'pendent'
                      CHECK (estat_revisio IN ('pendent', 'en_revisio', 'revisat')),
    data_comprovacio  DATE,
    procedencia       TEXT,
    estat_publicacio  VARCHAR(20) NOT NULL DEFAULT 'esborrany'
                      CHECK (estat_publicacio IN ('esborrany', 'publicat')),
    notes_internes    TEXT
);
 
-- Enlaces a redes sociales (puede haber varios por entidad)
CREATE TABLE xarxa_social (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entitat_id  BIGINT NOT NULL REFERENCES entitat(id) ON DELETE CASCADE,
    url         VARCHAR(500) NOT NULL
);
 
-- Relacion de la entidad con ambitos de actividad y perfiles profesionales
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
-- 4. CENTRE
--    Diagrama: una ENTITAT titula 1..N centros y gestiona 1..N centros.
--    Titular y gestora pueden coincidir y pueden ser NULL (fuente no
--    lo indica). Dos sedes con direcciones distintas = dos centros.
-- ---------------------------------------------------------------------
CREATE TABLE centre (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom               VARCHAR(200) NOT NULL,
    descripcio        TEXT,
    tipus_centre_id   BIGINT REFERENCES tipus_centre(id),
    titular_id        BIGINT REFERENCES entitat(id),          -- "titula"
    gestor_id         BIGINT REFERENCES entitat(id),          -- "gestiona"
    -- direccion (el CP no identifica el municipio)
    municipi_id       BIGINT REFERENCES municipi(id),
    nucli_poblacio    VARCHAR(100),
    codi_postal       VARCHAR(5) CHECK (codi_postal ~ '^[0-9]{5}$'),
    via               VARCHAR(255),                           -- seleccion de catalogo o entrada manual
    numero            VARCHAR(20),
    complements       VARCHAR(255),
    -- coordenadas (solo si se conocen) y su precision
    latitud           NUMERIC(9,6) CHECK (latitud  BETWEEN -90  AND 90),
    longitud          NUMERIC(9,6) CHECK (longitud BETWEEN -180 AND 180),
    precisio_punt     VARCHAR(30)
                      CHECK (precisio_punt IN ('exacta', 'aproximada', 'proposta_automatica')),
    -- contacto, horario e imagen
    telefon           VARCHAR(30),
    correu            VARCHAR(255),
    web               VARCHAR(500),
    horari_observacions TEXT,                                 -- excepciones estacionales, cita previa
    imatge            VARCHAR(255),
    -- control interno
    estat_revisio     VARCHAR(20) NOT NULL DEFAULT 'pendent'
                      CHECK (estat_revisio IN ('pendent', 'en_revisio', 'revisat')),
    data_comprovacio  DATE,
    procedencia       TEXT,
    estat_publicacio  VARCHAR(20) NOT NULL DEFAULT 'esborrany'
                      CHECK (estat_publicacio IN ('esborrany', 'publicat')),
    notes_internes    TEXT,
    -- latitud y longitud van juntas, y si hay punto se indica su precision
    CHECK ((latitud IS NULL) = (longitud IS NULL)),
    CHECK (latitud IS NULL OR precisio_punt IS NOT NULL)
);
 
-- ---------------------------------------------------------------------
-- 5. SERVEI / PROGRAMA
-- ---------------------------------------------------------------------
CREATE TABLE servei (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nom                 VARCHAR(250) NOT NULL,
    descripcio          TEXT,
    categoria_id        BIGINT REFERENCES categoria_servei(id),
    via_acces           TEXT,                                 -- cita, derivacion, condiciones conocidas
    telefon             VARCHAR(30),
    correu              VARCHAR(255),
    web                 VARCHAR(500),
    horari_observacions TEXT,
    imatge              VARCHAR(255),
    -- control interno
    estat_revisio       VARCHAR(20) NOT NULL DEFAULT 'pendent'
                        CHECK (estat_revisio IN ('pendent', 'en_revisio', 'revisat')),
    data_comprovacio    DATE,
    procedencia         TEXT,
    estat_publicacio    VARCHAR(20) NOT NULL DEFAULT 'esborrany'
                        CHECK (estat_publicacio IN ('esborrany', 'publicat')),
    notes_internes      TEXT
);
 
CREATE TABLE servei_collectiu (
    servei_id     BIGINT NOT NULL REFERENCES servei(id) ON DELETE CASCADE,
    collectiu_id  BIGINT NOT NULL REFERENCES collectiu(id),
    PRIMARY KEY (servei_id, collectiu_id)
);
 
-- Modalidades combinables (valores fijos, sin tabla de catalogo)
CREATE TABLE servei_modalitat (
    servei_id  BIGINT NOT NULL REFERENCES servei(id) ON DELETE CASCADE,
    modalitat  VARCHAR(20) NOT NULL
               CHECK (modalitat IN ('presencial', 'domiciliaria', 'telefonica', 'en_linia')),
    PRIMARY KEY (servei_id, modalitat)
);
 
-- Territorio atendido: municipios concretos o comarcas completas
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
-- 6. PRESTACIO (diagrama: las ENTITATs prestan SERVEIs)
--    Un servei puede ser prestado por varias entitats y viceversa.
-- ---------------------------------------------------------------------
CREATE TABLE prestacio (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    servei_id   BIGINT NOT NULL REFERENCES servei(id) ON DELETE CASCADE,
    entitat_id  BIGINT NOT NULL REFERENCES entitat(id),
    UNIQUE (servei_id, entitat_id)
);
 
-- Diagrama: "se ofrece en" (CENTRE - PRESTACIO). Una prestacion se ofrece
-- en 0 o mas centros; sin filas = sin centro conocido.
CREATE TABLE prestacio_centre (
    prestacio_id  BIGINT NOT NULL REFERENCES prestacio(id) ON DELETE CASCADE,
    centre_id     BIGINT NOT NULL REFERENCES centre(id),
    PRIMARY KEY (prestacio_id, centre_id)
);
 
-- ---------------------------------------------------------------------
-- 7. HORARIOS (varios intervalos por dia, horas libres)
--    Cada franja pertenece a UN centro o a UN servicio.
--    Sin franjas = horario desconocido (no significa "cerrado").
-- ---------------------------------------------------------------------
CREATE TABLE franja_horaria (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    centre_id    BIGINT REFERENCES centre(id) ON DELETE CASCADE,
    servei_id    BIGINT REFERENCES servei(id) ON DELETE CASCADE,
    dia_setmana  SMALLINT NOT NULL CHECK (dia_setmana BETWEEN 1 AND 7),  -- 1=lunes ... 7=domingo
    hora_inici   TIME NOT NULL,
    hora_fi      TIME NOT NULL,
    CHECK ((centre_id IS NOT NULL) <> (servei_id IS NOT NULL)),
    CHECK (hora_inici < hora_fi)
);
 
-- ---------------------------------------------------------------------
-- 8. INDICES para las consultas por filtros
-- ---------------------------------------------------------------------
CREATE INDEX ix_municipi_comarca   ON municipi (comarca_id);
CREATE INDEX ix_centre_municipi    ON centre (municipi_id);
CREATE INDEX ix_centre_tipus       ON centre (tipus_centre_id);
CREATE INDEX ix_centre_titular     ON centre (titular_id);
CREATE INDEX ix_centre_gestor      ON centre (gestor_id);
CREATE INDEX ix_servei_categoria   ON servei (categoria_id);
CREATE INDEX ix_prestacio_entitat  ON prestacio (entitat_id);
CREATE INDEX ix_prestcentre_centre ON prestacio_centre (centre_id);
CREATE INDEX ix_cobertura_servei   ON servei_cobertura (servei_id);
CREATE INDEX ix_xarxa_entitat      ON xarxa_social (entitat_id);
CREATE INDEX ix_franja_centre      ON franja_horaria (centre_id);
CREATE INDEX ix_franja_servei      ON franja_horaria (servei_id);