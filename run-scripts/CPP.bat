@echo off

REM EXE=$1
REM INFILE=$2
REM OUTFILE=$3
REM ERRORFILE=$4
REM TIMEOUT=$5

REM TIMEOUTVALUE=%6

REM above is old
REM changing to
REM EXE=$1
REM INFILE=$2
REM OUTFILE=$3
REM ERRORFILE=$4
REM TIMEOUT=$5
REM MEMORYOUT=$6
REM MAXTIME=$6
REM MAXMEMORY=$7
REM MAXFILE=$8

REG ADD "HKCU\SOFTWARE\MICROSOFT\WINDOWS\WINDOWS ERROR REPORTING" /f /v ForceQueue /t REG_DWORD /d 1

Powershell.exe -executionpolicy RemoteSigned -Command "Measure-Command { Get-Content -ReadCount 0 %2 | %1 1> %3 2> %4 } > %5; If ($LASTEXITCODE -ne 0) { echo LASTEXITCODE=$LASTEXITCODE >> %4 } "

REG ADD "HKCU\SOFTWARE\MICROSOFT\WINDOWS\WINDOWS ERROR REPORTING" /f /v ForceQueue /t REG_DWORD /d 0

