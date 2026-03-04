import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse, VitalSigns, RecordVitalsRequest, PatientDashboard, PatientTimeline } from '@repo/types';
 
@Injectable({ providedIn: 'root' })
export class HealthService {
  private http = inject(HttpClient);
  private adminApiUrl = 'http://localhost:8080/api/v1/admin/health';
  private patientApiUrl = 'http://localhost:8080/api/v1/patient/health';
 
  // Admin/Doctor endpoints
  recordPatientVitals(patientId: string, data: RecordVitalsRequest): Observable<ApiResponse<VitalSigns>> {
    return this.http.post<ApiResponse<VitalSigns>>(`${this.adminApiUrl}/vitals/${patientId}`, data);
  }
 
  getPatientVitalHistory(patientId: string, page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<VitalSigns>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<VitalSigns>>>(`${this.adminApiUrl}/vitals/${patientId}/history`, { params });
  }
 
  getPatientTimelineForAdmin(patientId: string, page: number = 0, size: number = 20): Observable<ApiResponse<PageResponse<PatientTimeline>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<PatientTimeline>>>(`${this.adminApiUrl}/timeline/${patientId}`, { params });
  }
 
  // Patient endpoints (if needed for the admin to view a consolidated "patient view")
  getPatientDashboard(patientId: string): Observable<ApiResponse<PatientDashboard>> {
    // Note: In a real app, admin might have a dedicated dashboard view or reuse patient data
    return this.http.get<ApiResponse<PatientDashboard>>(`${this.patientApiUrl}/dashboard`);
  }
}
