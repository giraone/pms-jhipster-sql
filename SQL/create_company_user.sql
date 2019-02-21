-- Table: public.company_user

-- DROP TABLE public.company_user;

CREATE TABLE public.company_user
(
    user_id bigint NOT NULL,
    company_id bigint NOT NULL,
    CONSTRAINT company_user_pkey PRIMARY KEY (company_id, user_id),
    CONSTRAINT fk_company_user_company_id FOREIGN KEY (company_id)
        REFERENCES public.company (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_company_user_user_id FOREIGN KEY (user_id)
        REFERENCES public.jhi_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.company_user
    OWNER to pmssql;
