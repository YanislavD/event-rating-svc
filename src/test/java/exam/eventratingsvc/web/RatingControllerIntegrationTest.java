package exam.eventratingsvc.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import exam.eventratingsvc.model.Rating;
import exam.eventratingsvc.repository.RatingRepository;
import exam.eventratingsvc.web.dto.RatingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RatingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID eventId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void createRating_WithValidRequest_ShouldReturnCreated() throws Exception {
        RatingRequest request = new RatingRequest();
        request.setEventId(eventId);
        request.setUserId(userId);
        request.setScore(5);

        mockMvc.perform(post("/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value(eventId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.score").value(5));
    }

    @Test
    void getRatingsForEvent_WhenRatingsExist_ShouldReturnSummary() throws Exception {
        Rating rating1 = Rating.builder()
                .eventId(eventId)
                .userId(userId)
                .score(5)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        UUID userId2 = UUID.randomUUID();
        Rating rating2 = Rating.builder()
                .eventId(eventId)
                .userId(userId2)
                .score(3)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        ratingRepository.save(rating1);
        ratingRepository.save(rating2);

        mockMvc.perform(get("/ratings/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(eventId.toString()))
                .andExpect(jsonPath("$.totalRatings").value(2))
                .andExpect(jsonPath("$.averageScore").value(4.0));
    }
}

