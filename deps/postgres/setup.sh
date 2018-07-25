#!/usr/bin/env bash

set -e

CLI_ERR_MSG="Postgres CLI tools not available (psql). Using Postgres.app, look
at http://postgresapp.com/documentation/cli-tools.html. Aborting."
hash psql 2>/dev/null || { echo >&2 $CLI_ERR_MSG ; exit 1; }

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR


# Provision

## Create lumen role
psql -c "CREATE ROLE lumen WITH PASSWORD 'password' CREATEDB LOGIN;"

psql -v ON_ERROR_STOP=1 -U postgres <<-EOSQL
    CREATE USER dlebrero;
    CREATE DATABASE dlebrero;
    GRANT ALL PRIVILEGES ON DATABASE dlebrero TO dlebrero;
EOSQL

psql -U postgres dlebrero < dbexport.pgsql