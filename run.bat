@echo off
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
if %errorlevel% neq 0 exit /b 1
java -cp out rental.Main
