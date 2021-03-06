#!/bin/bash

# memory (kbytes)

EXE=$1
INFILE=$2
OUTFILE=$3
ERRORFILE=$4
METRICSFILE=$5
TIMELIMIT=$6 # ms
MEMORYLIMIT=$7 # KBytes
MAXFILE=$8
MAXTIME=$9
MAXMEMORY=${10}

ulimit -f ${MAXFILE}
ulimit -v ${MAXMEMORY}

#
# TODO
# record ulimit violation
# http://stackoverflow.com/questions/3043709/resident-set-size-rss-limit-has-no-effect/6365534#6365534
# 

/usr/bin/time -o ${METRICSFILE} -f "%U\n%M" ${EXE} 0< ${INFILE} 1> ${OUTFILE} 2> ${ERRORFILE}