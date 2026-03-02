import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Doctor, PageResponse, ApiResponse, HospitalSummary } from '@repo/types';

@Injectable({ providedIn: 'root' })
export class PublicApiService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1';

  getDoctors(params?: any): Observable<ApiResponse<PageResponse<Doctor>>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(key => {
        if (params[key]) httpParams = httpParams.append(key, params[key]);
      });
    }
    return this.http.get<ApiResponse<PageResponse<Doctor>>>(`${this.apiUrl}/doctors`, { params: httpParams });
  }

  getDoctorById(id: string): Observable<ApiResponse<Doctor>> {
    return this.http.get<ApiResponse<Doctor>>(`${this.apiUrl}/doctors/${id}`);
  }

  getHospitals(): Observable<ApiResponse<HospitalSummary[]>> {
    return this.http.get<ApiResponse<HospitalSummary[]>>(`${this.apiUrl}/hospitals/summary`);
  }

  bookAppointment(data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/appointments/book`, data);
  }
}
