package com.orthopedic.api.modules.website.service;

import com.orthopedic.api.modules.analytics.dto.response.SearchResultResponse;
import com.orthopedic.api.modules.analytics.dto.response.SearchSuggestionResponse;
import com.orthopedic.api.modules.blog.dto.response.BlogPostSummaryResponse;
import com.orthopedic.api.modules.doctor.dto.response.DoctorPublicResponse;
import com.orthopedic.api.modules.hospital.dto.response.ServiceResponse;
import com.orthopedic.api.modules.website.dto.request.*;
import com.orthopedic.api.modules.website.dto.response.*;

import java.util.List;
import java.util.Map;

public interface WebsiteService {
    // Group B: Config & SEO
    Map<String, String> getPublicConfig();

    List<MenuResponse> getMenus(String type);

    SeoResponse getSeoMetadata(String slug);

    List<WorkingHourResponse> getWorkingHours();

    // Group C: Search & Navigation
    SearchResultResponse search(String query, String type, String ipAddress);

    SearchSuggestionResponse getSearchSuggestions(String query);

    // Group D: Hero & Identity
    List<HeroSlideResponse> getHeroSlides();

    IdentityResponse getIdentity();

    AnnouncementResponse getAnnouncements();

    // Group E: Trust & Authority
    List<TestimonialResponse> getFeaturedTestimonials();

    HomeStatsResponse getHomeStats();

    List<PartnerResponse> getActivePartners();

    List<AwardResponse> getActiveAwards();

    List<DoctorPublicResponse> getFeaturedDoctors();

    List<ServiceResponse> getFeaturedServices();

    // Group F: Staff & Culture
    List<TeamMemberResponse> getTeamMembers();

    List<FaqResponse> getFaqs(String category);

    List<GalleryItemResponse> getGalleryItems(String category);

    // Group G: Social & Community
    List<BlogPostSummaryResponse> getLatestBlogPosts(int limit);

    void subscribeNewsletter(NewsletterRequest request);

    // Group H: Communication
    void submitContactForm(ContactRequest request);

    void submitLead(ContactRequest request);
}
