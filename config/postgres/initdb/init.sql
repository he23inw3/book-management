CREATE ROLE for_test WITH LOGIN PASSWORD 'passw0rd';
CREATE DATABASE test_application OWNER for_test template =template0 encoding ='utf-8' lc_collate ='C' lc_ctype ='C';
GRANT ALL PRIVILEGES ON DATABASE test_application TO for_test;
CREATE SCHEMA AUTHORIZATION for_test;
