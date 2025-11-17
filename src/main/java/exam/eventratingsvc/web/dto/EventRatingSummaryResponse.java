package exam.eventratingsvc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class EventRatingSummaryResponse {

    private UUID eventId;
    private Double averageScore;
    private Long totalRatings;
    private List<RatingResponse> ratings;
}


