package exam.eventratingsvc.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RatingUpdateRequest {

    @Min(1)
    @Max(5)
    private Integer score;

    @Size(max = 500)
    private String comment;
}


