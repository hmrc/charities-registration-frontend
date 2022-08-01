#!/usr/bin/env bash
sbt -Dmicroservice.services.address-lookup-frontend.port=6001 "run 9457"
