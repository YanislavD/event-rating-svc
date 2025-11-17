package exam.eventratingsvc.service;

import exam.eventratingsvc.model.Rating;
import exam.eventratingsvc.repository.RatingRepository;
import exam.eventratingsvc.web.dto.EventRatingSummaryResponse;
import exam.eventratingsvc.web.dto.RatingRequest;
import exam.eventratingsvc.web.dto.RatingResponse;
import exam.eventratingsvc.web.dto.RatingUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Transactional
    public RatingResponse createRating(RatingRequest request) {
        if (ratingRepository.existsByEventIdAndUserId(request.getEventId(), request.getUserId())) {
            throw new IllegalArgumentException("Потребителят вече е гласувал за това събитие");
        }

        LocalDateTime now = LocalDateTime.now();

        Rating rating = Rating.builder()
                .eventId(request.getEventId())
                .userId(request.getUserId())
                .score(request.getScore())
                .comment(request.getComment())
                .createdOn(now)
                .updatedOn(now)
                .build();

        try {
            Rating saved = ratingRepository.save(rating);
            return toResponse(saved);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Потребителят вече е гласувал за това събитие");
        }
    }

    @Transactional
    public RatingResponse updateRating(UUID ratingId, RatingUpdateRequest request) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found"));

        if (request.getScore() != null) {
            rating.setScore(request.getScore());
        }
        if (request.getComment() != null) {
            rating.setComment(request.getComment());
        }
        rating.setUpdatedOn(LocalDateTime.now());

        Rating saved = ratingRepository.save(rating);
        return toResponse(saved);
    }

    @Transactional
    public void deleteRating(UUID ratingId) {
        if (!ratingRepository.existsById(ratingId)) {
            throw new IllegalArgumentException("Rating not found");
        }
        ratingRepository.deleteById(ratingId);
    }

    @Transactional(readOnly = true)
    public EventRatingSummaryResponse getRatingsForEvent(UUID eventId) {
        List<Rating> ratings = ratingRepository.findAllByEventId(eventId);

        double avg = ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);

        List<RatingResponse> responses = ratings.stream()
                .map(this::toResponse)
                .toList();

        return EventRatingSummaryResponse.builder()
                .eventId(eventId)
                .averageScore(ratings.isEmpty() ? null : avg)
                .totalRatings((long) ratings.size())
                .ratings(responses)
                .build();
    }

    @Transactional(readOnly = true)
    public boolean hasUserRated(UUID eventId, UUID userId) {
        return ratingRepository.existsByEventIdAndUserId(eventId, userId);
    }

    private RatingResponse toResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .eventId(rating.getEventId())
                .userId(rating.getUserId())
                .score(rating.getScore())
                .comment(rating.getComment())
                .createdOn(rating.getCreatedOn())
                .updatedOn(rating.getUpdatedOn())
                .build();
    }
}


