# pmssql

Test application for database performance with company/employee model.

## JHipster

See [ORIGINAL-README-jhipster.md](ORIGINAL-README-jhipster.md) for the original JHipster README.
Most of the generated resources and services are left untouched to be used as an administraion (CRUD) api.
The added resources are using the base URL `/domain-api` and all classes are named _<Entity>DomainResource_,
_<Entity>DomainService_ and _<Entity>DomainImpl_.

## Build and run

```
# Run with development settings
./mvnw
# Run with production settings
./mvnw -Pprod
# Docker run with production settings
./mvnw -Pprod jib:dockerBuild
# Normal Maven production build
mvn -Pprod package
# Manual Java run
java -jar target/pmssql-0.0.1-SNAPSHOT.war
# Frontend with life reload
npm start
```

If running in production mode, use `docker-compose -f src/main/docker/postgresql.yml up -d` to start the
production PostgreSQL database. It will use a docker volume in `~/volumes/jhipster/pmssql/postgresql`.

You can also run a _pgAdmin4_ using `docker-compose -f src/main/docker/pgadmin4.yml up -d`.
Both docker compose files share a common network

## Re-create data model

```
jhipster import-jdl ./jhipster-jdl.jh --force
```

## Security

In constrast to the standard JHipster generated projects, only users with the role ADMIN can access the
generated CRUD REST services under the `/api` URL. All REST interfaces for "normal" users are placed
under the URL `domain-api` and are authorized to reflect \*multi-tenancy` (a user of a company can see
only employees of his/her company).

## CURL samples (admin API)

```
BASE_URL="http://localhost:8080"

token=$(curl "${BASE_URL}/api/authenticate" -s -H 'Accept: application/json' -H 'Content-Type: application/json' \
  --data '{"username":"admin","password":"admin"}' | jq -r ".id_token")

curl "${BASE_URL}/api/companies?page=0&size=20&sort=id,asc" -H 'Accept: application/json' \
 -H "Authorization: Bearer ${token}"

curl "${BASE_URL}/api/employees?page=0&size=20&sort=id,asc" -H 'Accept: application/json' \
 -H "Authorization: Bearer ${token}"

```

## CURL samples (domain API)

The first PUT call uses data from [the testdata-generator project on GitHub](https://github.com/giraone/testdata-generator).

```

curl "${BASE_URL}/domain-api/employee-list" -H 'Accept: application/json' -H 'Content-Type: application/json' \
 -H "Authorization: Bearer ${token}" -X PUT  --data @../data-5M/d-00000000/f-00000000.json

curl "${BASE_URL}/domain-api/employees?companyExternalId=l-00000060&surnamePrefix=A&page=0&size=20&sort=id,asc" \
 -H 'Accept: application/json' -H "Authorization: Bearer ${token}"
curl "${BASE_URL}/domain-api/employees?companyExternalId=l-00000060&surnamePrefix=Ar&page=0&size=20&sort=id,asc" \
 -H 'Accept: application/json' -H "Authorization: Bearer ${token}"

http://localhost:8080/domain-api/employees?surnamePrefix=X&page=0&size=20&externalCompanyId=l-00000060&sort=id,asc
```

## Performance of bulk load

-   Bulk load with 10 Mio employees on local PC with docker PostgreSQL: 120 minutes

## Database metrics and query samples

```
# number of companies
select count(*) from company
> 10000

# number of employees
select count(*) from employee
> 10000000

# largest companies
select count(company_id) as count, company_id
from employee
group by company_id
order by count desc

> "39714"	"1727"
> "39688"	"1534"
> "39544"	"2314"

# smallest companies
select count(company_id) as count, company_id
from employee
group by company_id
order by count asc

> "303"	"76917"
> "303"	"46415"
> "307"	"36421"

# surname distribution within a company
select count(surname) as count, surname
from employee
where company_id = 1727
group by surname
order by count desc

> "935"	"Müller"
> "674"	"Schmidt"
> "440"	"Schneider"

# all users of a company
select u.login from company_user cu, jhi_user u
where cu.users_id = u.id
and cu.companies_id = 1001

```
