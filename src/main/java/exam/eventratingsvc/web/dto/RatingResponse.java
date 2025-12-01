package exam.eventratingsvc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class RatingResponse {

    private UUID id;
    private UUID eventId;
    private UUID userId;
    private Integer score;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}


