
SET search_path TO ribera;

-- =====================================================
-- 1. DIRECCIONES Y PROCEDENCIA
-- =====================================================

-- Las entidades también pueden tener dirección y municipio.
ALTER TABLE entitat
    ADD COLUMN IF NOT EXISTS adreca TEXT,
    ADD COLUMN IF NOT EXISTS municipi_id BIGINT REFERENCES municipi(id),
    ADD COLUMN IF NOT EXISTS font_codi VARCHAR(20),
    ADD COLUMN IF NOT EXISTS adreca_font_codi VARCHAR(20);

-- Guardar la dirección original completa de los centros.
ALTER TABLE centre
    ADD COLUMN IF NOT EXISTS adreca TEXT,
    ADD COLUMN IF NOT EXISTS font_codi VARCHAR(20),
    ADD COLUMN IF NOT EXISTS adreca_font_codi VARCHAR(20);

-- Procedencia de los servicios.
ALTER TABLE servei
    ADD COLUMN IF NOT EXISTS font_codi VARCHAR(20);

-- Procedencia de las prestaciones.
ALTER TABLE prestacio
    ADD COLUMN IF NOT EXISTS font_codi VARCHAR(20);

ALTER TABLE prestacio_centre
    ADD COLUMN IF NOT EXISTS font_codi VARCHAR(20);


-- =====================================================
-- 2. CONTACTOS
-- =====================================================

CREATE TABLE IF NOT EXISTS contacte (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    entitat_id BIGINT REFERENCES entitat(id) ON DELETE CASCADE,
    centre_id BIGINT REFERENCES centre(id) ON DELETE CASCADE,
    servei_id BIGINT REFERENCES servei(id) ON DELETE CASCADE,

    tipus VARCHAR(50) NOT NULL,
    valor VARCHAR(500) NOT NULL,
    font_codi VARCHAR(20),

    CHECK (
        num_nonnulls(entitat_id, centre_id, servei_id) = 1
    )
);


-- =====================================================
-- 3. HORARIOS DOCUMENTALES
-- =====================================================

CREATE TABLE IF NOT EXISTS horari (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    entitat_id BIGINT REFERENCES entitat(id) ON DELETE CASCADE,
    centre_id BIGINT REFERENCES centre(id) ON DELETE CASCADE,
    servei_id BIGINT REFERENCES servei(id) ON DELETE CASCADE,

    observacions TEXT,
    font_codi VARCHAR(20),

    CHECK (
        num_nonnulls(entitat_id, centre_id, servei_id) = 1
    )
);


-- =====================================================
-- 4. FRANJAS HORARIAS
-- =====================================================

-- Solo ejecutar sobre una base de datos sin datos
-- de franjas anteriores.
DROP TABLE IF EXISTS franja_horaria;

CREATE TABLE franja_horaria (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    horari_id BIGINT NOT NULL
        REFERENCES horari(id) ON DELETE CASCADE,

    dia_setmana SMALLINT NOT NULL
        CHECK (dia_setmana BETWEEN 1 AND 7),

    hora_inici TIME NOT NULL,
    hora_fi TIME NOT NULL,

    final_dia_seguent BOOLEAN NOT NULL DEFAULT FALSE,

    CHECK (
        hora_inici < hora_fi
        OR final_dia_seguent = TRUE
    )
);


-- =====================================================
-- 5. CONTROL DE IMPORTACIONES
-- =====================================================

CREATE TABLE IF NOT EXISTS importacio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    clau VARCHAR(100) NOT NULL UNIQUE,
    versio INTEGER NOT NULL,
    data_importacio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- Relaciona los códigos de origen con los ID de PostgreSQL.
CREATE TABLE IF NOT EXISTS registre_importacio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    importacio_id BIGINT NOT NULL
        REFERENCES importacio(id) ON DELETE CASCADE,

    tipus_registre VARCHAR(50) NOT NULL,
    codi_origen VARCHAR(100) NOT NULL,

    id_postgres BIGINT NOT NULL,

    UNIQUE (
        importacio_id,
        tipus_registre,
        codi_origen
    )
);


-- Font

CREATE TABLE IF NOT EXISTS font (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codi VARCHAR(20) NOT NULL UNIQUE,
    referencia TEXT NOT NULL
);
