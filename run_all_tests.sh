#!/usr/bin/env bash

sbt scalafmtAll scalastyleAll clean compile coverage test it:test coverageOff dependencyUpdates coverageReport
