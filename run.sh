#!/bin/sh
# Compiles the project and runs the demo (Linux / macOS / Git Bash)
mkdir -p out
javac -d out $(find src -name "*.java") && java -cp out rental.Main
