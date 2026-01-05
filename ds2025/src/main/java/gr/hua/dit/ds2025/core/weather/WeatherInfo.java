package gr.hua.dit.ds2025.core.weather;

public record WeatherInfo(
        double temperature,
        double windspeed,
        int weathercode,
        String description
) {
}
