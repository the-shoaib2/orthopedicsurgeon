export enum Role {
  SUPER_ADMIN = 'ROLE_SUPER_ADMIN',
  ADMIN = 'ROLE_ADMIN',
  DOCTOR = 'ROLE_DOCTOR',
  PATIENT = 'ROLE_PATIENT',
  STAFF = 'ROLE_STAFF',
  ACCOUNTANT = 'ROLE_ACCOUNTANT',
  LAB_TECH = 'ROLE_LAB_TECH',
  PHARMACY = 'ROLE_PHARMACY'
}

export enum Permission {
  MANAGE_USERS = 'MANAGE_USERS',
  MANAGE_DOCTORS = 'MANAGE_DOCTORS',
  MANAGE_PATIENTS = 'MANAGE_PATIENTS',
  MANAGE_HOSPITALS = 'MANAGE_HOSPITALS',
  BOOK_APPOINTMENT = 'BOOK_APPOINTMENT',
  VIEW_AUDIT_LOGS = 'VIEW_AUDIT_LOGS'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED'
}

export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER'
}

export enum BloodGroup {
  A_POSITIVE = 'A_POSITIVE',
  A_NEGATIVE = 'A_NEGATIVE',
  B_POSITIVE = 'B_POSITIVE',
  B_NEGATIVE = 'B_NEGATIVE',
  AB_POSITIVE = 'AB_POSITIVE',
  AB_NEGATIVE = 'AB_NEGATIVE',
  O_POSITIVE = 'O_POSITIVE',
  O_NEGATIVE = 'O_NEGATIVE'
}

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: Role[];
  permissions: Permission[];
  status: UserStatus;
  isTwoFactorEnabled: boolean;
  imageUrl?: string;
  createdAt: string;
}
