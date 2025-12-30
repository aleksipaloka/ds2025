package gr.hua.dit.ds2025.core.service.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateReviewRequest(
        @NotNull @Positive Long reviewerId,
        @NotNull @Positive Long revieweeId,
        @NotNull @Positive @Max(5) @Min(1) int rating,
        String comments
) {
}
