#!/usr/bin/env bash

sbt scalafmtAll scalastyleAll clean compile coverage Test/test it/test coverageOff dependencyUpdates coverageReport