#!/bin/bash

# Calculate total test execution time by summing only the time attributes from the testsuite elements
# Using a more compatible awk syntax for macOS and other platforms
echo -e "\n========================================"
echo "TOTAL SUREFIRE EXECUTION TIME: $(awk '
/<testsuite/ {
  for (i=1; i<=NF; i++) {
    if ($i ~ /^time=/) {
      gsub(/time="/, "", $i);
      gsub(/".*/, "", $i);
      total += $i;
    }
  }
}
END {
  printf "%.3f seconds", total
}' target/surefire-reports/TEST-*.xml)"
echo "========================================"