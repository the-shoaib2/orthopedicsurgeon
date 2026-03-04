package com.orthopedic.api.rbac.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RolePermissionMapping {
    private static final Map<String, Set<Permission>> mapping = new ConcurrentHashMap<>();

    static {
        mapping.put("SUPER_ADMIN'", EnumSet.allOf(Permission.class));

        mapping.put("ADMIN", EnumSet.of(
                Permission.MANAGE_USERS,
                Permission.MANAGE_DOCTORS,
                Permission.MANAGE_APPOINTMENTS_ALL,
                Permission.VIEW_REPORTS_ALL,
                Permission.MANAGE_PAYMENTS,
                Permission.UPLOAD_REPORTS,
                Permission.VIEW_PRESCRIPTIONS,
                Permission.MANAGE_SERVICES));

        mapping.put("DOCTOR", EnumSet.of(
                Permission.MANAGE_APPOINTMENTS_OWN,
                Permission.VIEW_REPORTS_OWN,
                Permission.UPLOAD_REPORTS,
                Permission.VIEW_PRESCRIPTIONS));

        mapping.put("PATIENT", EnumSet.of(
                Permission.MANAGE_APPOINTMENTS_OWN,
                Permission.VIEW_PRESCRIPTIONS));

        // Add other roles as needed
    }

    public static Set<Permission> getPermissions(String role) {
        return mapping.getOrDefault(role, EnumSet.noneOf(Permission.class));
    }
}
