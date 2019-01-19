#!/bin/bash

BASE_URL="http://localhost:8080/api"
ROOT_DATA_DIR="../data-5M"

token=$(curl "${BASE_URL}/authenticate" -s -H 'Accept: application/json' -H 'Content-Type: application/json' \
  --data '{"username":"admin","password":"admin"}' | jq -r ".id_token")
if [[ ${token} == "" ]]; then
  exit 1
fi

typeset -i d=0

while (( d < 1 )); do
  dir=$(printf "d-%08d" $d)
  typeset -i f=0
  while (( f < 1000 )); do
    file=$(printf "%s/%s/f-%08d.json" "${ROOT_DATA_DIR}" "${dir}" $f)
    echo "${file}"
    count=$(curl "${BASE_URL}/employee-list" -s -H 'Accept: application/json' -H 'Content-Type: application/json' \
     -H "Authorization: Bearer ${token}" -X PUT  --data "@${file}")
    if [[ $? != 0 || $count != 1000 ]]; then
      exit 1
    fi
    let f+=1
  done
  let d+=1
done

