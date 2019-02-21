-- Table: public.company

-- DROP TABLE public.company;

CREATE TABLE public.company
(
    id bigint NOT NULL,
    external_id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    postal_code character varying(255) COLLATE pg_catalog."default",
    city character varying(255) COLLATE pg_catalog."default",
    street_address character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT pk_company PRIMARY KEY (id),
    CONSTRAINT ux_company_external_id UNIQUE (external_id)

)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.company
    OWNER to pmssql;
COMMENT ON TABLE public.company
    IS 'The Company entity.';
