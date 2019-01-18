# pmssql

Test application

## JHipster

See [README-jhipster.md](README-jhipster.md) for the original JHipster README.

## x

```

curl 'http://localhost:8080/api/authenticate' -s -H 'Accept: application/json' -H 'Content-Type: application/json' \
  --data '{"username":"admin","password":"admin"}' --output token.json
token=$(cat token.json | jq -r ".id_token")

curl 'http://localhost:8080/api/account' -H 'Accept: application/json' \
 -H "Authorization: Bearer ${token}"

curl 'http://localhost:8080/api/companies?page=0&size=20&sort=id,asc' -H 'Accept: application/json' \
 -H "Authorization: Bearer ${token}"

curl 'http://localhost:8080/api/employee-list' -H 'Accept: application/json' -H 'Content-Type: application/json' \
 -H "Authorization: Bearer ${token}" -X PUT  --data @d-00000000/f-00000000..json



```
