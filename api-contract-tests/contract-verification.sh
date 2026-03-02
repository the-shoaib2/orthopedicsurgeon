#!/bin/bash

# API Contract Verification Script
# Validates backend endpoint existence and response shapes.

BASE_URL="http://localhost:8080/api/v1"
jq --version > /dev/null 2>&1 || { echo "Error: jq is required but not installed. Exiting."; exit 1; }

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔍 STARTING API CONTRACT VERIFICATION"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

FAILED=0

validate_endpoint() {
    local label=$1
    local method=$2
    local path=$3
    local expected_shape=$4
    
    echo -n "Checking $label ($method $path)... "
    
    # Run curl
    RESPONSE=$(curl -s -X "$method" "$BASE_URL$path")
    
    # Check if endpoint exists (not 404)
    if [[ "$RESPONSE" == *"Not Found"* || "$RESPONSE" == *"404"* ]]; then
        echo "FAILED (404 Not Found)"
        FAILED=$((FAILED + 1))
        return 1
    fi
    
    # Validate shape using jq
    if echo "$RESPONSE" | jq -e "$expected_shape" > /dev/null 2>&1; then
        echo "PASS"
    else
        echo "FAILED (Invalid Response Shape)"
        echo "Response was: $RESPONSE"
        FAILED=$((FAILED + 1))
        return 1
    fi
}

# 1. Public Auth Endpoints
# Note: These will return 4xx because we don't provide body, but we check if they EXIST (not 404)
validate_endpoint "Auth: Login" "POST" "/auth/login" ".status? // .error?"
validate_endpoint "Auth: Register" "POST" "/auth/register" ".status? // .error?"
validate_endpoint "Auth: Refresh" "POST" "/auth/refresh" ".status? // .error?"

# 2. Public Metadata Endpoints
validate_endpoint "Hospitals: List" "GET" "/hospitals" ".data"
validate_endpoint "Doctors: List" "GET" "/doctors" ".data"

# 3. Role/Guard Checks (Expect 401/403 but must exist)
validate_endpoint "Admin: User Management" "GET" "/admin/users" ".status? // .error?"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
if [ $FAILED -eq 0 ]; then
    echo "✅ ALL CONTRACTS VERIFIED"
    exit 0
else
    echo "❌ $FAILED CONTRACTS FAILED"
    exit 1
fi
