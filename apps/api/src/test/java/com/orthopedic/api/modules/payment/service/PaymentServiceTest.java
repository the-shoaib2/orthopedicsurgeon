package com.orthopedic.api.modules.payment.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.payment.dto.request.CreatePaymentRequest;
import com.orthopedic.api.modules.payment.dto.response.PaymentResponse;
import com.orthopedic.api.modules.payment.entity.Payment;
import com.orthopedic.api.modules.payment.mapper.PaymentMapper;
import com.orthopedic.api.modules.payment.repository.PaymentRepository;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.modules.patient.entity.Patient;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private PaymentMapper paymentMapper;
    @Mock private Counter paymentSuccessCounter;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User currentUser;
    private Payment payment;
    private Patient patient;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("patient@test.com");
        currentUser.setRoles(Collections.emptySet());

        patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setUser(currentUser);

        payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setPatient(patient);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setStatus(Payment.PaymentStatus.PENDING);
    }

    @Test
    void createPayment_Success() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setPatientId(patient.getId());
        request.setAmount(new BigDecimal("100.00"));

        when(paymentMapper.toEntity(any())).thenReturn(payment);
        when(patientRepository.findById(any())).thenReturn(Optional.of(patient));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponse());

        PaymentResponse response = paymentService.createPayment(request);

        assertNotNull(response);
        verify(paymentSuccessCounter).increment();
    }

    @Test
    void processPayment_Success() {
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentMapper.toResponse(any())).thenReturn(new PaymentResponse());

        PaymentResponse response = paymentService.processPayment(payment.getId(), "TRANS123");

        assertNotNull(response);
        assertEquals(Payment.PaymentStatus.COMPLETED, payment.getStatus());
        verify(paymentSuccessCounter, times(1)).increment();
    }

    @Test
    void getPaymentById_AccessDenied() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@test.com");
        otherUser.setRoles(Collections.emptySet());

        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        assertThrows(AccessDeniedException.class, () -> paymentService.getPaymentById(payment.getId(), otherUser));
    }
}
