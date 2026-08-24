#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

if docker compose version >/dev/null 2>&1; then
    COMPOSE=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
    COMPOSE=(docker-compose)
else
    echo "No se encontro Docker Compose. Instala Docker Desktop o Docker Compose." >&2
    exit 1
fi

if command -v mvn >/dev/null 2>&1; then
    MVN=(mvn)
elif [[ -x "$PROJECT_ROOT/mvnw" ]]; then
    MVN=("$PROJECT_ROOT/mvnw")
else
    echo "No se encontro Maven ni el wrapper mvnw en la raiz del proyecto." >&2
    exit 1
fi

echo "Deteniendo contenedores existentes..."
"${COMPOSE[@]}" down

echo "Compilando backend..."
cd "$PROJECT_ROOT/backend"
"${MVN[@]}" -f pom.xml clean compile package -DskipTests
cd "$PROJECT_ROOT"
echo "Construyendo e iniciando la aplicacion..."
"${COMPOSE[@]}" up -d --build
echo "Aplicacion iniciada correctamente."
echo "Frontend: http://localhost:5173"
echo "Backend:  http://localhost:8080"
