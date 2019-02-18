ANALYZE employee_name;
ANALYZE employee;
ANALYZE company;
ANALYZE jhi_user;

-- Check results
-- select * from pg_stats where tablename = 'employee';
-- select * from pg_stats where tablename = 'employee_name';
-- select * from pg_stats where tablename = 'employee_name' and attname = 'name_value';
