package com.orthopedic.api.modules.contact.service.impl;

import com.orthopedic.api.modules.contact.dto.request.ContactMessageRequest;
import com.orthopedic.api.modules.contact.dto.request.NewsletterSubscribeRequest;
import com.orthopedic.api.modules.contact.entity.ContactMessage;
import com.orthopedic.api.modules.contact.entity.NewsletterSubscriber;
import com.orthopedic.api.modules.contact.repository.ContactMessageRepository;
import com.orthopedic.api.modules.contact.repository.NewsletterSubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl {

    private final ContactMessageRepository contactMessageRepository;
    private final NewsletterSubscriberRepository newsletterSubscriberRepository;

    @Transactional
    public void submitContactMessage(ContactMessageRequest request, String ipAddress) {
        ContactMessage message = ContactMessage.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .subject(request.subject())
                .message(request.message())
                .ipAddress(ipAddress)
                .status(ContactMessage.ContactMessageStatus.NEW)
                .build();

        contactMessageRepository.save(message);
        sendAutoReplyAsync(request.email(), request.name());
        notifyAdminsAsync(message);
    }

    @Transactional
    public void subscribeNewsletter(NewsletterSubscribeRequest request) {
        Optional<NewsletterSubscriber> existing = newsletterSubscriberRepository.findByEmail(request.email());
        if (existing.isPresent()) {
            NewsletterSubscriber sub = existing.get();
            if (!sub.getIsActive()) {
                sub.setIsActive(true);
                newsletterSubscriberRepository.save(sub);
            }
            return;
        }

        NewsletterSubscriber sub = NewsletterSubscriber.builder()
                .email(request.email())
                .name(request.name())
                .isActive(true)
                .build();

        newsletterSubscriberRepository.save(sub);
    }

    @Async
    protected void sendAutoReplyAsync(String email, String name) {
        // Implementation for email service
        // Implementation for email service
    }

    @Async
    protected void notifyAdminsAsync(ContactMessage message) {
        // Implementation for notifying admins
        // Implementation for notifying admins
    }
}
