package gr.hua.dit.ds2025.web.ui.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewForm(
        @NotNull @Min(1) @Max(5) Integer rating,
        @Size(max = 500) String comments,
        @NotNull Long revieweeId,
        Long tripId
) {}
