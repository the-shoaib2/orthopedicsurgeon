package com.orthopedic.api.modules.website.service.impl;

import com.orthopedic.api.modules.analytics.dto.response.SearchResultResponse;
import com.orthopedic.api.modules.analytics.dto.response.SearchSuggestionResponse;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.blog.dto.response.BlogPostSummaryResponse;
import com.orthopedic.api.modules.blog.entity.BlogPost;
import com.orthopedic.api.modules.blog.repository.BlogPostRepository;
import com.orthopedic.api.modules.contact.entity.ContactMessage;
import com.orthopedic.api.modules.contact.entity.NewsletterSubscriber;
import com.orthopedic.api.modules.contact.repository.ContactMessageRepository;
import com.orthopedic.api.modules.contact.repository.NewsletterSubscriberRepository;
import com.orthopedic.api.modules.doctor.dto.response.DoctorPublicResponse;
import com.orthopedic.api.modules.doctor.entity.Doctor;
import com.orthopedic.api.modules.doctor.repository.DoctorRepository;
import com.orthopedic.api.modules.hospital.dto.response.ServiceResponse;
import com.orthopedic.api.modules.hospital.entity.ServiceEntity;
import com.orthopedic.api.modules.hospital.repository.ServiceRepository;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.modules.website.dto.request.*;
import com.orthopedic.api.modules.website.dto.response.*;
import com.orthopedic.api.modules.website.entity.Menu;
import com.orthopedic.api.modules.website.entity.SeoMetadata;
import com.orthopedic.api.modules.website.entity.SiteSetting;
import com.orthopedic.api.modules.website.repository.*;
import com.orthopedic.api.modules.website.service.WebsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebsiteServiceImpl implements WebsiteService {

        private final SiteSettingRepository siteSettingRepository;
        private final MenuRepository menuRepository;
        private final SeoMetadataRepository seoMetadataRepository;
        private final DoctorRepository doctorRepository;
        private final BlogPostRepository blogPostRepository;
        private final ServiceRepository serviceRepository;
        private final HeroSlideRepository heroSlideRepository;
        private final TestimonialRepository testimonialRepository;
        private final PartnerRepository partnerRepository;
        private final AppointmentRepository appointmentRepository;
        private final PatientRepository patientRepository;
        private final TeamMemberRepository teamMemberRepository;
        private final FaqRepository faqRepository;
        private final GalleryItemRepository galleryItemRepository;
        private final ContactMessageRepository contactMessageRepository;
        private final NewsletterSubscriberRepository newsletterSubscriberRepository;
        private final AwardRepository awardRepository;

        @Override
        public Map<String, String> getPublicConfig() {
                return siteSettingRepository.findByIsPublicTrue().stream()
                                .collect(Collectors.toMap(SiteSetting::getKey, SiteSetting::getValue));
        }

        @Override
        public List<MenuResponse> getMenus(String type) {
                List<Menu> rootMenus = menuRepository.findByTypeAndParentIsNullAndIsActiveTrueOrderByOrderAsc(type);
                return rootMenus.stream()
                                .map(this::mapToMenuResponse)
                                .collect(Collectors.toList());
        }

        @Override
        public SeoResponse getSeoMetadata(String slug) {
                return seoMetadataRepository.findBySlug(slug)
                                .map(this::mapToSeoResponse)
                                .orElse(null);
        }

        @Override
        public List<WorkingHourResponse> getWorkingHours() {
                Map<String, String> config = getPublicConfig();
                List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
                                "Sunday");

                return days.stream()
                                .map(day -> {
                                        String key = "hospital.hours." + day.toLowerCase();
                                        String hours = config.getOrDefault(key, "09:00 AM - 05:00 PM");
                                        boolean isClosed = hours.equalsIgnoreCase("CLOSED");
                                        return WorkingHourResponse.builder()
                                                        .day(day)
                                                        .hours(hours)
                                                        .isClosed(isClosed)
                                                        .build();
                                })
                                .collect(Collectors.toList());
        }

        @Override
        public List<HeroSlideResponse> getHeroSlides() {
                return heroSlideRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                                .map(slide -> HeroSlideResponse.builder()
                                                .id(slide.getId())
                                                .title(slide.getTitle())
                                                .subtitle(slide.getSubtitle())
                                                .description(slide.getDescription())
                                                .imageUrl(slide.getImageUrl())
                                                .buttonText(slide.getButtonText())
                                                .buttonLink(slide.getButtonLink())
                                                .displayOrder(slide.getDisplayOrder())
                                                .isActive(slide.getIsActive())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public IdentityResponse getIdentity() {
                Map<String, String> config = getPublicConfig();
                return IdentityResponse.builder()
                                .logoUrl(config.getOrDefault("site.logo", "/assets/logo.png"))
                                .faviconUrl(config.getOrDefault("site.favicon", "/favicon.ico"))
                                .siteName(config.getOrDefault("site.name", "Orthopedic Surgeon"))
                                .primaryColor(config.getOrDefault("site.primary_color", "#1e3a8a"))
                                .secondaryColor(config.getOrDefault("site.secondary_color", "#64748b"))
                                .build();
        }

        @Override
        public AnnouncementResponse getAnnouncements() {
                Map<String, String> config = getPublicConfig();
                boolean isActive = Boolean.parseBoolean(config.getOrDefault("announcement.active", "false"));
                if (!isActive)
                        return null;

                return AnnouncementResponse.builder()
                                .text(config.get("announcement.text"))
                                .link(config.get("announcement.link"))
                                .type(config.getOrDefault("announcement.type", "INFO"))
                                .isActive(true)
                                .build();
        }

        @Override
        public List<TestimonialResponse> getFeaturedTestimonials() {
                return testimonialRepository.findByIsFeaturedTrueAndIsVerifiedTrueOrderByCreatedAtDesc().stream()
                                .limit(10)
                                .map(t -> TestimonialResponse.builder()
                                                .id(t.getId())
                                                .patientName(t.getPatientName())
                                                .patientAvatar(t.getPatientAvatar())
                                                .content(t.getContent())
                                                .rating(t.getRating())
                                                .isVerified(t.getIsVerified())
                                                .isFeatured(t.getIsFeatured())
                                                .doctorName(t.getDoctor() != null
                                                                ? t.getDoctor().getUser().getFirstName() + " "
                                                                                + t.getDoctor().getUser().getLastName()
                                                                : null)
                                                .createdAt(t.getCreatedAt())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public HomeStatsResponse getHomeStats() {
                Map<String, String> config = getPublicConfig();
                return HomeStatsResponse.builder()
                                .totalDoctors(doctorRepository.count())
                                .totalPatients(patientRepository.count())
                                .totalAppointments(appointmentRepository.count())
                                .totalServices(serviceRepository.count())
                                .yearsExperience(config.getOrDefault("site.stats.experience", "15+"))
                                .successRate(config.getOrDefault("site.stats.success_rate", "99%"))
                                .build();
        }

        @Override
        public List<PartnerResponse> getActivePartners() {
                return partnerRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                                .map(p -> PartnerResponse.builder()
                                                .id(p.getId())
                                                .name(p.getName())
                                                .logoUrl(p.getLogoUrl())
                                                .websiteUrl(p.getWebsiteUrl())
                                                .displayOrder(p.getDisplayOrder())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public List<AwardResponse> getActiveAwards() {
                return awardRepository.findByIsActiveTrueOrderByAwardYearDescDisplayOrderAsc().stream()
                                .map(a -> AwardResponse.builder()
                                                .id(a.getId())
                                                .title(a.getTitle())
                                                .description(a.getDescription())
                                                .imageUrl(a.getImageUrl())
                                                .awardedBy(a.getAwardedBy())
                                                .awardYear(a.getAwardYear())
                                                .displayOrder(a.getDisplayOrder())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public List<DoctorPublicResponse> getFeaturedDoctors() {
                return doctorRepository.findByIsFeaturedTrueAndStatus(Doctor.DoctorStatus.ACTIVE).stream()
                                .map(d -> DoctorPublicResponse.builder()
                                                .id(d.getId())
                                                .fullName(d.getUser().getFirstName() + " " + d.getUser().getLastName())
                                                .specialization(d.getSpecialization())
                                                .bio(d.getBio())
                                                .hospitalName(d.getHospital() != null ? d.getHospital().getName()
                                                                : null)
                                                .consultationFee(d.getConsultationFee())
                                                .experienceYears(d.getExperienceYears() != null ? d.getExperienceYears()
                                                                : 0)
                                                .status(d.getStatus())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public List<ServiceResponse> getFeaturedServices() {
                return serviceRepository.findByIsFeaturedTrueAndStatus(ServiceEntity.ServiceStatus.ACTIVE).stream()
                                .map(s -> ServiceResponse.builder()
                                                .id(s.getId())
                                                .hospitalId(s.getHospital() != null ? s.getHospital().getId() : null)
                                                .hospitalName(s.getHospital() != null ? s.getHospital().getName()
                                                                : null)
                                                .name(s.getName())
                                                .description(s.getDescription())
                                                .price(s.getPrice())
                                                .category(s.getCategory())
                                                .status(s.getStatus())
                                                .durationMinutes(s.getDurationMinutes())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public List<TeamMemberResponse> getTeamMembers() {
                return teamMemberRepository.findByShowOnWebsiteTrueOrderByDisplayOrderAsc().stream()
                                .map(t -> TeamMemberResponse.builder()
                                                .id(t.getId())
                                                .name(t.getUser() != null
                                                                ? t.getUser().getFirstName() + " "
                                                                                + t.getUser().getLastName()
                                                                : t.getTitle())
                                                .role(t.getTitle())
                                                .specialization(t.getSpecialization())
                                                .bio(t.getBio())
                                                .photoUrl(t.getImageUrl())
                                                .displayOrder(t.getDisplayOrder())
                                                .facebookUrl(t.getSocialLinks() != null
                                                                ? t.getSocialLinks().get("facebook")
                                                                : null)
                                                .twitterUrl(t.getSocialLinks() != null
                                                                ? t.getSocialLinks().get("twitter")
                                                                : null)
                                                .linkedinUrl(t.getSocialLinks() != null
                                                                ? t.getSocialLinks().get("linkedin")
                                                                : null)
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public List<FaqResponse> getFaqs(String category) {
                if (category != null) {
                        return faqRepository.findByIsActiveTrueAndCategory(category, PageRequest.of(0, 100))
                                        .getContent().stream()
                                        .map(f -> FaqResponse.builder()
                                                        .id(f.getId())
                                                        .question(f.getQuestion())
                                                        .answer(f.getAnswer())
                                                        .category(f.getCategory())
                                                        .displayOrder(f.getDisplayOrder())
                                                        .build())
                                        .collect(Collectors.toList());
                }
                return faqRepository.findByIsActiveTrueOrderByCategoryAscDisplayOrderAsc().stream()
                                .map(f -> FaqResponse.builder()
                                                .id(f.getId())
                                                .question(f.getQuestion())
                                                .answer(f.getAnswer())
                                                .category(f.getCategory())
                                                .displayOrder(f.getDisplayOrder())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public List<GalleryItemResponse> getGalleryItems(String category) {
                if (category != null) {
                        return galleryItemRepository.findByIsActiveTrueAndCategory(category, PageRequest.of(0, 100))
                                        .getContent().stream()
                                        .map(g -> GalleryItemResponse.builder()
                                                        .id(g.getId())
                                                        .title(g.getTitle())
                                                        .description(g.getDescription())
                                                        .imageUrl(g.getImageUrl())
                                                        .category(g.getCategory())
                                                        .displayOrder(g.getDisplayOrder())
                                                        .build())
                                        .collect(Collectors.toList());
                }
                return galleryItemRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                                .map(g -> GalleryItemResponse.builder()
                                                .id(g.getId())
                                                .title(g.getTitle())
                                                .description(g.getDescription())
                                                .imageUrl(g.getImageUrl())
                                                .category(g.getCategory())
                                                .displayOrder(g.getDisplayOrder())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        public List<BlogPostSummaryResponse> getLatestBlogPosts(int limit) {
                return blogPostRepository.findByStatus(BlogPost.BlogPostStatus.PUBLISHED,
                                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent()
                                .stream()
                                .map(p -> BlogPostSummaryResponse.builder()
                                                .id(p.getId())
                                                .title(p.getTitle())
                                                .slug(p.getSlug())
                                                .excerpt(p.getExcerpt())
                                                .featuredImageUrl(p.getFeaturedImageUrl())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public void subscribeNewsletter(NewsletterRequest request) {
                if (newsletterSubscriberRepository.existsByEmail(request.getEmail())) {
                        return;
                }
                NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                                .email(request.getEmail())
                                .isActive(true)
                                .build();
                newsletterSubscriberRepository.save(subscriber);
        }

        @Override
        @Transactional
        public void submitContactForm(ContactRequest request) {
                ContactMessage message = ContactMessage.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .subject(request.getSubject())
                                .message(request.getMessage())
                                .phone(request.getPhone())
                                .status(ContactMessage.ContactMessageStatus.NEW)
                                .build();
                contactMessageRepository.save(message);
        }

        @Override
        @Transactional
        public void submitLead(ContactRequest request) {
                ContactMessage lead = ContactMessage.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .subject("LEAD: " + request.getSubject())
                                .message(request.getMessage())
                                .phone(request.getPhone())
                                .status(ContactMessage.ContactMessageStatus.LEAD)
                                .build();
                contactMessageRepository.save(lead);
        }

        @Override
        public SearchResultResponse search(String query, String type, String ipAddress) {
                List<DoctorPublicResponse> doctors = new ArrayList<>();
                List<BlogPostSummaryResponse> blogPosts = new ArrayList<>();
                List<ServiceResponse> services = new ArrayList<>();

                if (type == null || type.equalsIgnoreCase("all") || type.equalsIgnoreCase("doctors")) {
                        doctors = doctorRepository.searchDoctors(query).stream()
                                        .map(d -> DoctorPublicResponse.builder()
                                                        .id(d.getId())
                                                        .fullName(d.getUser().getFirstName() + " "
                                                                        + d.getUser().getLastName())
                                                        .specialization(d.getSpecialization())
                                                        .bio(d.getBio())
                                                        .hospitalName(d.getHospital() != null
                                                                        ? d.getHospital().getName()
                                                                        : null)
                                                        .consultationFee(d.getConsultationFee())
                                                        .experienceYears(d.getExperienceYears() != null
                                                                        ? d.getExperienceYears()
                                                                        : 0)
                                                        .status(d.getStatus())
                                                        .build())
                                        .collect(Collectors.toList());
                }

                if (type == null || type.equalsIgnoreCase("all") || type.equalsIgnoreCase("blog")) {
                        blogPosts = blogPostRepository.searchPosts(query).stream()
                                        .map(p -> BlogPostSummaryResponse.builder()
                                                        .id(p.getId())
                                                        .title(p.getTitle())
                                                        .slug(p.getSlug())
                                                        .excerpt(p.getExcerpt())
                                                        .featuredImageUrl(p.getFeaturedImageUrl())
                                                        .build())
                                        .collect(Collectors.toList());
                }

                if (type == null || type.equalsIgnoreCase("all") || type.equalsIgnoreCase("services")) {
                        services = serviceRepository.searchServices(query).stream()
                                        .map(s -> ServiceResponse.builder()
                                                        .id(s.getId())
                                                        .hospitalId(s.getHospital() != null ? s.getHospital().getId()
                                                                        : null)
                                                        .hospitalName(s.getHospital() != null
                                                                        ? s.getHospital().getName()
                                                                        : null)
                                                        .name(s.getName())
                                                        .description(s.getDescription())
                                                        .price(s.getPrice())
                                                        .category(s.getCategory())
                                                        .status(s.getStatus())
                                                        .durationMinutes(s.getDurationMinutes())
                                                        .build())
                                        .collect(Collectors.toList());
                }

                return SearchResultResponse.builder()
                                .query(query)
                                .type(type)
                                .totalResults(doctors.size() + blogPosts.size() + services.size())
                                .doctors(doctors)
                                .blogPosts(blogPosts)
                                .services(services)
                                .build();
        }

        @Override
        public SearchSuggestionResponse getSearchSuggestions(String query) {
                List<String> suggestions = new ArrayList<>();

                doctorRepository.searchDoctors(query).stream().limit(3)
                                .forEach(d -> suggestions
                                                .add(d.getUser().getFirstName() + " " + d.getUser().getLastName()));

                blogPostRepository.searchPosts(query).stream().limit(3)
                                .forEach(p -> suggestions.add(p.getTitle()));

                serviceRepository.searchServices(query).stream().limit(4)
                                .forEach(s -> suggestions.add(s.getName()));

                return SearchSuggestionResponse.builder()
                                .suggestions(suggestions.stream().distinct().limit(10).collect(Collectors.toList()))
                                .build();
        }

        private MenuResponse mapToMenuResponse(Menu menu) {
                return MenuResponse.builder()
                                .id(menu.getId())
                                .title(menu.getTitle())
                                .url(menu.getUrl())
                                .order(menu.getOrder())
                                .children(menu.getChildren() != null ? menu.getChildren().stream()
                                                .filter(Menu::isActive)
                                                .map(this::mapToMenuResponse)
                                                .collect(Collectors.toList()) : List.of())
                                .build();
        }

        private SeoResponse mapToSeoResponse(SeoMetadata seo) {
                return SeoResponse.builder()
                                .slug(seo.getSlug())
                                .title(seo.getTitle())
                                .description(seo.getDescription())
                                .keywords(seo.getKeywords())
                                .ogImage(seo.getOgImage())
                                .canonicalUrl(seo.getCanonicalUrl())
                                .build();
        }
}
