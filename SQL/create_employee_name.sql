-- Table: public.employee_name

-- DROP TABLE public.employee_name;

CREATE TABLE public.employee_name
(
    owner_id bigint NOT NULL,
    name_key character varying(2) COLLATE pg_catalog."default" NOT NULL,
    name_value character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT fk_employee_name_to_owner FOREIGN KEY (owner_id)
        REFERENCES public.employee (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT fk_employee_name_to_company FOREIGN KEY (company_id)
        REFERENCES public.company (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.employee_name
    OWNER to pmssql;
COMMENT ON TABLE public.employee_name
    IS 'Normalized names of the employee';

-- Index: emname_name_index

-- DROP INDEX public.emname_name_index;

CREATE INDEX emname_name_index
    ON public.employee_name USING btree
    (company_id, name_key COLLATE pg_catalog."default", name_value COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: emname_owner_index

-- DROP INDEX public.emname_owner_index;

CREATE INDEX emname_owner_index
    ON public.employee_name USING btree
    (owner_id)
    TABLESPACE pg_default;
