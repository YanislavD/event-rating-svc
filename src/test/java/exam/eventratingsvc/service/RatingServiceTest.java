package exam.eventratingsvc.service;

import exam.eventratingsvc.model.Rating;
import exam.eventratingsvc.repository.RatingRepository;
import exam.eventratingsvc.web.dto.EventRatingSummaryResponse;
import exam.eventratingsvc.web.dto.RatingRequest;
import exam.eventratingsvc.web.dto.RatingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    private UUID eventId;
    private UUID userId;
    private RatingRequest ratingRequest;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        ratingRequest = new RatingRequest();
        ratingRequest.setEventId(eventId);
        ratingRequest.setUserId(userId);
        ratingRequest.setScore(5);
    }

    @Test
    void createRating_WhenRatingDoesNotExist_ShouldCreateRating() {
        when(ratingRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);
        
        Rating savedRating = Rating.builder()
                .id(UUID.randomUUID())
                .eventId(eventId)
                .userId(userId)
                .score(5)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        
        when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);

        RatingResponse response = ratingService.createRating(ratingRequest);

        assertNotNull(response);
        assertEquals(eventId, response.getEventId());
        assertEquals(userId, response.getUserId());
        assertEquals(5, response.getScore());
        verify(ratingRepository).existsByEventIdAndUserId(eventId, userId);
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void getRatingsForEvent_WhenRatingsExist_ShouldReturnSummary() {
        Rating rating1 = Rating.builder()
                .id(UUID.randomUUID())
                .eventId(eventId)
                .userId(userId)
                .score(5)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        UUID userId2 = UUID.randomUUID();
        Rating rating2 = Rating.builder()
                .id(UUID.randomUUID())
                .eventId(eventId)
                .userId(userId2)
                .score(3)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        List<Rating> ratings = List.of(rating1, rating2);
        when(ratingRepository.findAllByEventId(eventId)).thenReturn(ratings);

        EventRatingSummaryResponse response = ratingService.getRatingsForEvent(eventId);

        assertNotNull(response);
        assertEquals(eventId, response.getEventId());
        assertEquals(2L, response.getTotalRatings());
        assertEquals(4.0, response.getAverageScore());
        verify(ratingRepository).findAllByEventId(eventId);
    }
}

