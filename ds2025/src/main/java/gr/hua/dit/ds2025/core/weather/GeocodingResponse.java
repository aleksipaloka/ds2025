package gr.hua.dit.ds2025.core.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeocodingResponse(
        List<Result> results
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            String name,
            String country,
            Double latitude,
            Double longitude) {

    }
}
