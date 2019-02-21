#!/bin/bash

BASE_URL="http://localhost:8080"
#BASE_URL="http://pmssql-env.p2wirtvhmn.eu-west-1.elasticbeanstalk.com"
#BASE_URL="https://pmssql.cfapps.io"

ROOT_DATA_DIR="../data-10M"

token=$(curl "${BASE_URL}/api/authenticate" -s -H 'Accept: application/json' -H 'Content-Type: application/json' \
  --data '{"username":"admin","password":"admin"}' | jq -r ".id_token")
if [[ ${token} == "" ]]; then
  exit 1
fi

typeset -i d=0

while (( d < 10 )); do
  dir=$(printf "d-%08d" $d)
  typeset -i f=0
  while (( f < 1000 )); do
    file=$(printf "%s/%s/f-%08d.json" "${ROOT_DATA_DIR}" "${dir}" $f)
    echo "${file}"
    typeset -i start=$(date +%s)
    count=$(curl "${BASE_URL}/bulk-api/employee-list" -s -H 'Accept: application/json' -H 'Content-Type: application/json' \
     -H "Authorization: Bearer ${token}" -X PUT  --data "@${file}")
    if [[ $? != 0 || $count != 1000 ]]; then
      echo $count
      exit 1
    fi
    typeset -i end=$(date +%s)
    let secs=$end-$start
    echo " processing time was ${secs} seconds"
    let f+=1
  done

  sleep 5
  let d+=1
done

