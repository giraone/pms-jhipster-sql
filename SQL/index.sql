CREATE INDEX emname_owner_index
    ON public.employee_name USING btree
    (owner_id)
    TABLESPACE pg_default;

CREATE INDEX emname_name_index
    ON public.employee_name USING btree
    (name_key COLLATE pg_catalog."default", name_value COLLATE pg_catalog."default")
    TABLESPACE pg_default;

CREATE INDEX employee_dob_index
    ON public.employee USING btree
    (date_of_birth)
    TABLESPACE pg_default;

