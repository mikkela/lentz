#!/usr/bin/env bash
set -euo pipefail

# Where to put generated C++ sources
OUT="src/frontend/parser"
GRAM="grammar/lentz.g4"

# Resolve ANTLR jar (use env var if set, else try brew)
if [[ -z "${ANTLR_JAR:-}" ]]; then
  if command -v brew >/dev/null 2>&1; then
    export ANTLR_JAR="$(brew --prefix antlr)/libexec"/antlr-4.*-complete.jar
  fi
fi
if [[ ! -f "${ANTLR_JAR:-/nope}" ]]; then
  echo "ERROR: ANTLR_JAR not set and could not be found via Homebrew."
  echo "Install with: brew install antlr  (then export ANTLR_JAR=...)"
  exit 1
fi

echo "[gen] Using ANTLR jar: $ANTLR_JAR"
rm -rf "$OUT"
mkdir -p "$OUT"

# Generate C++ lexer/parser (+visitor is useful)
java -jar "$ANTLR_JAR" -Dlanguage=Cpp -visitor -Xexact-output-dir -o "$OUT" "$GRAM"

# Optional: show what was generated
echo "[gen] Generated:"
ls -1 "$OUT" | sed 's/^/  - /'