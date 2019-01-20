# pmssql

Test application for database performance with company/employee model.

## JHipster

See [README-jhipster.md](README-jhipster.md) for the original JHipster README.
Most of the generated resources and services are left untouched to be used as an administraion (CRUD) api.
The added resources are using the base URL `/domain-api` and all classes are named _<Entity>DomainResource_,
_<Entity>DomainService_ and _<Entity>DomainImpl_.

## Build and run

```
./mvnw
-/mvnw -Pprod
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
under the URL `api-domain` and are authorized to reflect \*multi-tenancy` (a user of a company can see
only employees of his/her company).

TODO:

-   Use @Secured()
-   Use @Preauthorize
-   Load "companies" on login to "session"

## CURL samples (admin API)

The last PUT call uses data from [the testdata-generator project on GitHub](https://github.com/giraone/testdata-generator).

```

token=$(curl 'http://localhost:8080/api/authenticate' -s -H 'Accept: application/json' -H 'Content-Type: application/json' \
  --data '{"username":"admin","password":"admin"}' | jq -r ".id_token")

curl 'http://localhost:8080/api/companies?page=0&size=20&sort=id,asc' -H 'Accept: application/json' \
 -H "Authorization: Bearer ${token}"

curl 'http://localhost:8080/api/employees?page=0&size=20&sort=id,asc' -H 'Accept: application/json' \
 -H "Authorization: Bearer ${token}"

curl 'http://localhost:8080/api/employee-list' -H 'Accept: application/json' -H 'Content-Type: application/json' \
 -H "Authorization: Bearer ${token}" -X PUT  --data @../data-5M/d-00000000/f-00000000.json

```

## CURL samples (domain API)

```

curl 'http://localhost:8080/domain-api/employees?companyExternalId=s-00005422-00000009&surnamePrefix=A&page=0&size=20&sort=id,asc' \
 -H 'Accept: application/json' -H "Authorization: Bearer ${token}"
curl 'http://localhost:8080/domain-api/employees?companyExternalId=s-00005422-00000009&surnamePrefix=Ar&page=0&size=20&sort=id,asc' \
 -H 'Accept: application/json' -H "Authorization: Bearer ${token}"

```

## Performance of bulk load

-   Bulk load with 10 Mio employees on local PC with docker PostgreSql: 118 minutes

## Database metrics and query samples

```
select count(*) from company
> 10000

select count(*) from employee
> 10000000

select count(company_id) as count, company_id
from employee
group by company_id
order by count desc

> "39714"	"1727"
> "39688"	"1534"
> "39544"	"2314"

select count(company_id) as count, company_id
from employee
group by company_id
order by count asc

> "303"	"76917"
> "303"	"46415"
> "307"	"36421"

select count(surname) as count, surname
from employee
where company_id = 1727
group by surname
order by count desc
> "935"	"Müller"
> "674"	"Schmidt"
> "440"	"Schneider"
```
