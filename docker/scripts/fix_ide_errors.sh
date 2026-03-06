#!/bin/bash

# Ensure we are in the project root
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/../.."
cd "$PROJECT_ROOT"

echo "Current working directory: $(pwd)"

echo "========================================================"
echo "   Logistics Platform - DE Error Fixer"
echo "========================================================"
echo "This script will:"
echo "1. Clean the project to remove stale artifacts."
echo "2. Run a full Maven install to generate all sources (Lombok, MapStruct)."
echo "3. This ensures your IDE (VS Code/IntelliJ) can find all classes."
echo "========================================================"

# Detect Maven
if [ -x "$(command -v mvn)" ]; then
  MAVEN_CMD="mvn"
  echo "[INFO] Using system Maven: $(which mvn)"
elif [ -f "./mvnw" ]; then
  MAVEN_CMD="./mvnw"
  echo "[INFO] Using existing Maven Wrapper."
else
  echo "[ERROR] Maven (mvn) not found in PATH and ./mvnw not present."
  echo "Please install Maven or ensure it is in your PATH."
  echo "Try: brew install maven"
  exit 1
fi

echo ""
echo "[START] Running clean install (skipping tests)..."
echo "This may take a few minutes..."
echo ""

$MAVEN_CMD clean install -pl logistic-app -am -DskipTests

if [ $? -eq 0 ]; then
  echo ""
  echo "========================================================"
  echo "   SUCCEESS! Build completed."
  echo "========================================================"
  echo "ACTION REQUIRED:"
  echo "1. Switch back to your IDE."
  echo "2. VS Code: Press Cmd+Shift+P -> 'Developer: Reload Window'"
  echo "3. IntelliJ: File -> Invalidate Caches / Restart -> Just Restart"
  echo "========================================================"
else
  echo ""
  echo "[ERROR] Build failed. Please check the logs above."
  exit 1
fi
