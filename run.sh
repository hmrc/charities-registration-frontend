#!/usr/bin/env bash
sbt -Dlogger.resource=logback.xml -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes "run 9457"
