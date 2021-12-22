-- Table: public.padrone

-- DROP TABLE public.padrone;

CREATE TABLE IF NOT EXISTS public.ruolo
(
    id character varying(50),
    descrizione_Ruolo character(50)
)

TABLESPACE pg_default;


CREATE TABLE IF NOT EXISTS public.pair
(
    pair_value character varying(50)
)

TABLESPACE pg_default;