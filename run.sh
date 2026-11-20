#!/bin/bash
# Helper script to compile and run the Hospital Patient Management project.
set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"
BIN="$ROOT/bin"

echo "Compiling sources..."
mkdir -p "$BIN"
javac -d "$BIN" "$ROOT"/src/hospital/*.java

echo "Running HospitalMain..."
echo
java -cp "$BIN" hospital.HospitalMain
