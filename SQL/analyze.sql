ANALYZE employee_name;
ANALYZE employee;
ANALYZE company;
ANALYZE jhi_user;

-- Check results
-- select * from pg_stats where tablename = 'employee';
-- select * from pg_stats where tablename = 'employee_name';
-- select * from pg_stats where tablename = 'employee_name' and attname = 'name_value';

-- DROP STATISTICS employee_name_stat_1
-- CREATE STATISTICS employee_name_stat_1(dependencies) on owner_id, company_id, name_key, name_value from employee_name;

-- DROP STATISTICS employee_stat_1
-- CREATE STATISTICS employee_stat_1(dependencies) on id, company_id, date_of_birth from employee;
