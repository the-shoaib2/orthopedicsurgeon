package com.orthopedic.api.modules.health.entity;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.patient.entity.Patient;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "medical_documents", indexes = {
        @Index(name = "idx_medical_docs_patient", columnList = "patient_id, is_deleted")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDocument {

    public enum DocumentType {
        XRAY, MRI, CT_SCAN, BLOOD_TEST, ECG, ULTRASOUND,
        DISCHARGE_SUMMARY, REFERRAL, INSURANCE, OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    @Builder.Default
    private DocumentType documentType = DocumentType.OTHER;

    @Column(name = "document_name", length = 300, nullable = false)
    private String documentName;

    @Column(name = "file_url", length = 1000, nullable = false)
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "document_date")
    private LocalDate documentDate;

    @Column(name = "is_private", nullable = false)
    @Builder.Default
    private Boolean isPrivate = false;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
        if (documentType == null)
            documentType = DocumentType.OTHER;
        if (isPrivate == null)
            isPrivate = false;
        if (isDeleted == null)
            isDeleted = false;
    }
}
