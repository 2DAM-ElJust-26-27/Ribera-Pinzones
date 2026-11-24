SET search_path TO ribera;

ALTER TABLE centre
DROP CONSTRAINT centre_precisio_punt_check;

ALTER TABLE centre
ADD CONSTRAINT centre_precisio_punt_check
CHECK (
    precisio_punt IN (
        'exacta',
        'aproximada',
        'proposta_automatica',
        'punt_publicat'
    )
);