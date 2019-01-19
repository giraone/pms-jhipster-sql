# pmssql

Test application for database performance with company/employee model.

## JHipster

See [README-jhipster.md](README-jhipster.md) for the original JHipster README.

## Build and run

```
./mvnw
-/mvnw -Pprod
```

If running in production mode, use `docker-compose -f src/main/docker/postgresql.yml up -d` to start the
production PostgreSQL database. It will use a docker volume in `~/volumes/jhipster/pmssql/postgresql`.

## Re-create model

```
jhipster import-jdl ./jhipster-jdl.jh --force
```

## CURL samples

The last PUT call uses data from [the testdata-generator project on GitHub](https://github.com/giraone/testdata-generator).

```

token=$(curl 'http://localhost:8080/api/authenticate' -s -H 'Accept: application/json' -H 'Content-Type: application/json' \
  --data '{"username":"admin","password":"admin"}' | jq -r ".id_token")

curl 'http://localhost:8080/api/account' -H 'Accept: application/json' \
 -H "Authorization: Bearer ${token}"

curl 'http://localhost:8080/api/company' -H 'Accept: application/json' -H 'Content-Type: application/json' \
 -H "Authorization: Bearer ${token}" -X PUT  --data @company.json

curl 'http://localhost:8080/api/companies?page=0&size=20&sort=id,asc' -H 'Accept: application/json' \
 -H "Authorization: Bearer ${token}"

curl 'http://localhost:8080/api/employee-list' -H 'Accept: application/json' -H 'Content-Type: application/json' \
 -H "Authorization: Bearer ${token}" -X PUT  --data @../data-5M/d-00000000/f-00000000.json


```
