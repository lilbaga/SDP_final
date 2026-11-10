#!/bin/sh
# Compiles the project and runs the tests
mkdir -p out
javac -d out $(find src -name "*.java") && java -cp out rental.RentalTests
