-- Table: public.employee

-- DROP TABLE public.employee;

CREATE TABLE public.employee
(
    id bigint NOT NULL,
    surname character varying(255) COLLATE pg_catalog."default" NOT NULL,
    given_name character varying(255) COLLATE pg_catalog."default",
    date_of_birth date,
    gender character varying(255) COLLATE pg_catalog."default",
    postal_code character varying(255) COLLATE pg_catalog."default",
    city character varying(255) COLLATE pg_catalog."default",
    street_address character varying(255) COLLATE pg_catalog."default",
    company_id bigint NOT NULL,
    CONSTRAINT pk_employee PRIMARY KEY (id),
    CONSTRAINT fk_employee_company_id FOREIGN KEY (company_id)
        REFERENCES public.company (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.employee
    OWNER to pmssql;
COMMENT ON TABLE public.employee
    IS 'The Employee entity.';

-- Index: employee_dob_index

-- DROP INDEX public.employee_dob_index;

CREATE INDEX employee_dob_index
    ON public.employee USING btree
    (date_of_birth)
    TABLESPACE pg_default;
