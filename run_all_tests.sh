#!/usr/bin/env bash

sbt scalafmtAll scalastyleAll clean compile coverage Test/test IntegrationTest/test coverageOff dependencyUpdates coverageReport
