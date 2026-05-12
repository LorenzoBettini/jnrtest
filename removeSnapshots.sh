#!/bin/sh

./mvnw versions:set \
	-DgenerateBackupPoms=false \
	-DremoveSnapshot=true

