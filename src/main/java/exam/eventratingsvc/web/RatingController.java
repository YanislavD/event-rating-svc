package exam.eventratingsvc.web;

import exam.eventratingsvc.service.RatingService;
import exam.eventratingsvc.web.dto.EventRatingSummaryResponse;
import exam.eventratingsvc.web.dto.RatingRequest;
import exam.eventratingsvc.web.dto.RatingResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RatingResponse> createRating(@Valid @RequestBody RatingRequest request) {
        RatingResponse response = ratingService.createRating(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventRatingSummaryResponse> getRatingsForEvent(@PathVariable UUID eventId) {
        EventRatingSummaryResponse response = ratingService.getRatingsForEvent(eventId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event/{eventId}/user/{userId}")
    public ResponseEntity<Boolean> hasUserRated(@PathVariable UUID eventId, @PathVariable UUID userId) {
        boolean hasRated = ratingService.hasUserRated(eventId, userId);
        return ResponseEntity.ok(hasRated);
    }
}


