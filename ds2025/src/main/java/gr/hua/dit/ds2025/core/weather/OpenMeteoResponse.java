package gr.hua.dit.ds2025.core.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(CurrentWeather current_weather) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentWeather(
            double temperature,
            double windspeed,
            int weathercode
    ) {}
}
