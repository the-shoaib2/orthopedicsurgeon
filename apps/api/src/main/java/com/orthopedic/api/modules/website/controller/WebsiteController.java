package com.orthopedic.api.modules.website.controller;

import com.orthopedic.api.modules.analytics.dto.response.SearchResultResponse;
import com.orthopedic.api.modules.analytics.dto.response.SearchSuggestionResponse;
import com.orthopedic.api.modules.blog.dto.response.BlogPostSummaryResponse;
import com.orthopedic.api.modules.doctor.dto.response.DoctorPublicResponse;
import com.orthopedic.api.modules.hospital.dto.response.ServiceResponse;
import com.orthopedic.api.modules.website.dto.request.*;
import com.orthopedic.api.modules.website.dto.response.*;
import com.orthopedic.api.modules.website.service.WebsiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/website")
@RequiredArgsConstructor
public class WebsiteController {

    private final WebsiteService websiteService;

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getPublicConfig() {
        return ResponseEntity.ok(websiteService.getPublicConfig());
    }

    @GetMapping("/menus")
    public ResponseEntity<List<MenuResponse>> getMenus(@RequestParam(defaultValue = "HEADER") String type) {
        return ResponseEntity.ok(websiteService.getMenus(type));
    }

    @GetMapping("/navigation")
    public ResponseEntity<List<MenuResponse>> getNavigation() {
        return ResponseEntity.ok(websiteService.getMenus("HEADER"));
    }

    @GetMapping("/seo/{slug}")
    public ResponseEntity<SeoResponse> getSeoMetadata(@PathVariable String slug) {
        return ResponseEntity.ok(websiteService.getSeoMetadata(slug));
    }

    @GetMapping("/working-hours")
    public ResponseEntity<List<WorkingHourResponse>> getWorkingHours() {
        return ResponseEntity.ok(websiteService.getWorkingHours());
    }

    @GetMapping("/hero")
    public ResponseEntity<List<HeroSlideResponse>> getHeroSlides() {
        return ResponseEntity.ok(websiteService.getHeroSlides());
    }

    @GetMapping("/identity")
    public ResponseEntity<IdentityResponse> getIdentity() {
        return ResponseEntity.ok(websiteService.getIdentity());
    }

    @GetMapping("/announcements")
    public ResponseEntity<AnnouncementResponse> getAnnouncements() {
        return ResponseEntity.ok(websiteService.getAnnouncements());
    }

    @GetMapping("/testimonials")
    public ResponseEntity<List<TestimonialResponse>> getFeaturedTestimonials() {
        return ResponseEntity.ok(websiteService.getFeaturedTestimonials());
    }

    @GetMapping("/stats")
    public ResponseEntity<HomeStatsResponse> getHomeStats() {
        return ResponseEntity.ok(websiteService.getHomeStats());
    }

    @GetMapping("/partners")
    public ResponseEntity<List<PartnerResponse>> getActivePartners() {
        return ResponseEntity.ok(websiteService.getActivePartners());
    }

    @GetMapping("/awards")
    public ResponseEntity<List<AwardResponse>> getActiveAwards() {
        return ResponseEntity.ok(websiteService.getActiveAwards());
    }

    @GetMapping("/featured-doctors")
    public ResponseEntity<List<DoctorPublicResponse>> getFeaturedDoctors() {
        return ResponseEntity.ok(websiteService.getFeaturedDoctors());
    }

    @GetMapping("/featured-services")
    public ResponseEntity<List<ServiceResponse>> getFeaturedServices() {
        return ResponseEntity.ok(websiteService.getFeaturedServices());
    }

    @GetMapping("/team")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers() {
        return ResponseEntity.ok(websiteService.getTeamMembers());
    }

    @GetMapping("/faqs")
    public ResponseEntity<List<FaqResponse>> getFaqs(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(websiteService.getFaqs(category));
    }

    @GetMapping("/gallery")
    public ResponseEntity<List<GalleryItemResponse>> getGalleryItems(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(websiteService.getGalleryItems(category));
    }

    @GetMapping("/blog/latest")
    public ResponseEntity<List<BlogPostSummaryResponse>> getLatestBlogPosts(
            @RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(websiteService.getLatestBlogPosts(limit));
    }

    @PostMapping("/newsletter/subscribe")
    public ResponseEntity<Void> subscribeNewsletter(@Valid @RequestBody NewsletterRequest request) {
        websiteService.subscribeNewsletter(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/contact")
    public ResponseEntity<Void> submitContactForm(@Valid @RequestBody ContactRequest request) {
        websiteService.submitContactForm(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/leads")
    public ResponseEntity<Void> submitLead(@Valid @RequestBody ContactRequest request) {
        websiteService.submitLead(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResultResponse> search(
            @RequestParam String query,
            @RequestParam(required = false) String type,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress) {
        return ResponseEntity.ok(websiteService.search(query, type, ipAddress));
    }

    @GetMapping("/search/suggestions")
    public ResponseEntity<SearchSuggestionResponse> getSearchSuggestions(@RequestParam String query) {
        return ResponseEntity.ok(websiteService.getSearchSuggestions(query));
    }
}
