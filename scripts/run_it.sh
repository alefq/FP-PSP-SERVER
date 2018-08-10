#!/usr/bin/env bash
mvn clean verify -P docker -Dspring.profiles.active=it -Dskip.surefire.tests
