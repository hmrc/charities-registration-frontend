#!/usr/bin/env bash

sbt clean scalafmtAll compile coverage Test/test it/test coverageOff dependencyUpdates coverageReport
