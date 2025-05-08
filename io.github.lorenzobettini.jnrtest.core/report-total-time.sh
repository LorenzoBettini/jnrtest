#!/bin/bash

# Calculate total test execution time by summing only the time attributes from the testsuite elements
echo -e "\n========================================"
echo "TOTAL SUREFIRE EXECUTION TIME: $(awk '/<testsuite.*time=/ {match($0, /time="([0-9.]+)"/, arr); total+=arr[1]} END{printf "%.3f seconds", total}' target/surefire-reports/TEST-*.xml)"
echo "========================================\n"