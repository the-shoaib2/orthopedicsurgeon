import { Appointment } from './appointment.models';
import { LabReport } from './lab-report.models';
 
export interface VitalSigns {
  id: string;
  systolic: number;
  diastolic: number;
  heartRate: number;
  temperature: number;
  weight: number;
  height: number;
  bmi: number;
  oxygenSaturation: number;
  notes?: string;
  recordedAt: string;
}
 
export interface RecordVitalsRequest {
  appointmentId?: string;
  bloodPressureSystolic: number;
  bloodPressureDiastolic: number;
  heartRate: number;
  temperature: number;
  weight: number;
  height: number;
  oxygenSaturation: number;
  notes?: string;
}
 
export interface PatientDashboard {
  upcomingAppointments: Appointment[];
  activePrescriptions: number;
  pendingPayments: number;
  unreadNotifications: number;
  lastVisitDate?: string;
  nextAppointment?: Appointment;
  recentLabReports: LabReport[];
  latestVitals?: VitalSigns;
}
 
export interface PatientTimeline {
  id: string;
  patientId: string;
  eventType: string;
  eventDate: string;
  description: string;
  referenceId?: string;
}
