package com.orthopedic.api.modules.payment.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.appointment.entity.Appointment;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.modules.payment.dto.request.CreatePaymentRequest;
import com.orthopedic.api.modules.payment.dto.response.PaymentResponse;
import com.orthopedic.api.modules.payment.entity.Payment;
import com.orthopedic.api.modules.payment.mapper.PaymentMapper;
import com.orthopedic.api.modules.payment.repository.PaymentRepository;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.BusinessException;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final PaymentMapper paymentMapper;
    private final io.micrometer.core.instrument.Counter paymentSuccessCounter;

    @Override
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "CREATE_PAYMENT", entityName = "Payment")
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Payment payment = paymentMapper.toEntity(request);
        
        if (request.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
            payment.setAppointment(appointment);
        }

        payment.setPatient(patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found")));

        // Calculate totals
        BigDecimal tax = payment.getAmount().multiply(new BigDecimal("0.05")); // 5% tax example
        payment.setTaxAmount(tax);
        Payment saved = paymentRepository.save(payment);
        paymentSuccessCounter.increment();
        return paymentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id, User currentUser) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        validateOwnership(payment, currentUser);
        
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByAppointment(UUID appointmentId, User currentUser) {
        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for this appointment"));
        
        validateOwnership(payment, currentUser);
        
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPatientPayments(UUID patientId, Pageable pageable, User currentUser) {
        validatePatientAccess(patientId, currentUser);
        Page<Payment> page = paymentRepository.findAllByPatientId(patientId, pageable);
        return PageResponse.fromPage(page.map(paymentMapper::toResponse));
    }

    private void validatePatientAccess(UUID patientId, User currentUser) {
        if (hasAnyRole(currentUser, "ROLE_ADMIN", "ROLE_STAFF", "ROLE_SUPER_ADMIN")) {
            return;
        }
        if (hasRole(currentUser, "ROLE_PATIENT")) {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            if (!patient.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your records");
            }
        } else {
            throw new AccessDeniedException("Access denied: Insufficient permissions");
        }
    }

    @Override
    @com.orthopedic.api.modules.audit.annotation.LogMutation(action = "PROCESS_PAYMENT", entityName = "Payment")
    public PaymentResponse processPayment(UUID id, String transactionId) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        if (payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
            throw new BusinessException("Payment is already completed");
        }

        Payment saved = paymentRepository.save(payment);
        paymentSuccessCounter.increment();
        return paymentMapper.toResponse(saved);
    }

    private void validateOwnership(Payment payment, User currentUser) {
        if (hasAnyRole(currentUser, "ROLE_ADMIN", "ROLE_STAFF", "ROLE_SUPER_ADMIN")) {
            return;
        }
        
        if (hasRole(currentUser, "ROLE_PATIENT")) {
            if (!payment.getPatient().getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied: Not your payment record");
            }
        }
    }

    private boolean hasRole(User user, String role) {
        return user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }

    private boolean hasAnyRole(User user, String... roles) {
        List<String> roleList = Arrays.asList(roles);
        return user.getAuthorities().stream().anyMatch(a -> roleList.contains(a.getAuthority()));
    }
}
