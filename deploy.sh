#!/bin/sh

./mvnw -Psonatype-oss-release \
	clean package deploy -DskipTests

