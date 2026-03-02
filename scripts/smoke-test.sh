#!/bin/bash

# Orthopedic Surgeon Platform - Smoke Test Script
# This script verifies the health of all core services in the integrated environment.

API_URL="http://localhost:8080"
ADMIN_URL="http://localhost:4200"
PUBLIC_URL="http://localhost:4201"

echo "🚀 Starting Platform Smoke Tests..."

# 1. API Health Check
echo -n "Checking API Health... "
HEALTH=$(curl -s $API_URL/actuator/health | grep -o '"status":"UP"')
if [ "$HEALTH" == '"status":"UP"' ]; then
    echo "✅ UP"
else
    echo "❌ DOWN"
fi

# 2. Database Connectivity
echo -n "Checking Database Connectivity... "
DB=$(curl -s $API_URL/actuator/health | grep -o '"db":{"status":"UP"')
if [ "$DB" == '"db":{"status":"UP"' ]; then
    echo "✅ UP"
else
    echo "❌ DOWN"
fi

# 3. Redis Connectivity
echo -n "Checking Redis Connectivity... "
REDIS=$(curl -s $API_URL/actuator/health | grep -o '"redis":{"status":"UP"')
if [ "$REDIS" == '"redis":{"status":"UP"' ]; then
    echo "✅ UP"
else
    echo "❌ DOWN"
fi

# 4. Admin Dashboard Accessibility
echo -n "Checking Admin Dashboard... "
ADMIN_CODE=$(curl -s -o /dev/null -w "%{http_code}" $ADMIN_URL)
if [ "$ADMIN_CODE" == "200" ]; then
    echo "✅ Accessible"
else
    echo "❌ Error (Code: $ADMIN_CODE)"
fi

# 5. Public Site Accessibility
echo -n "Checking Public Site... "
PUBLIC_CODE=$(curl -s -o /dev/null -w "%{http_code}" $PUBLIC_URL)
if [ "$PUBLIC_CODE" == "200" ]; then
    echo "✅ Accessible"
else
    echo "❌ Error (Code: $PUBLIC_CODE)"
fi

echo "🎯 Smoke Test Completed."
