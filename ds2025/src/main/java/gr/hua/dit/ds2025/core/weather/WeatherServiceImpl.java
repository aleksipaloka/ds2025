package gr.hua.dit.ds2025.core.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.hua.dit.ds2025.core.weather.WeatherService;
import gr.hua.dit.ds2025.core.weather.GeocodingResponse;
import gr.hua.dit.ds2025.core.weather.OpenMeteoResponse;
import gr.hua.dit.ds2025.core.weather.WeatherInfo;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, Coordinates> geoCache = new ConcurrentHashMap<>();

    @Override
    public WeatherInfo getCurrentWeatherByPlace(String place) {
        try {
            if (place == null || place.isBlank()) {
                throw new IllegalArgumentException("Place must not be blank");
            }

            final String normalized = normalize(place);

            System.out.println("[WEATHER] Input place: '" + place + "' | normalized: '" + normalized + "'");

            Coordinates c = geoCache.computeIfAbsent(normalized, key -> {
                try {
                    return geocodeWithFallback(place);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            OpenMeteoResponse w = getCurrentWeather(c.lat(), c.lon());
            int code = w.current_weather().weathercode();

            System.out.println("[WEATHER] Coords: lat=" + c.lat() + ", lon=" + c.lon()
                    + " | temp=" + w.current_weather().temperature()
                    + " | wind=" + w.current_weather().windspeed()
                    + " | code=" + code);

            return new WeatherInfo(
                    w.current_weather().temperature(),
                    w.current_weather().windspeed(),
                    code,
                    describeWeather(code)
            );

        } catch (RuntimeException re) {
            System.out.println("[WEATHER] ERROR (runtime): " + re.getMessage());
            throw re;
        } catch (Exception e) {
            System.out.println("[WEATHER] ERROR: " + e.getMessage());
            throw new RuntimeException("Weather lookup failed for place=" + place, e);
        }
    }

    private Coordinates geocodeWithFallback(String place) throws Exception {
        String cleanedOriginal = cleanPlace(place);

        System.out.println("[WEATHER] Geocoding attempt #1: '" + cleanedOriginal + "'");

        Coordinates c1 = geocode(cleanedOriginal);
        if (c1 != null) return c1;

        String fallback = cleanedOriginal + ", Greece";

        System.out.println("[WEATHER] Geocoding attempt #2 (fallback): '" + fallback + "'");

        Coordinates c2 = geocode(fallback);
        if (c2 != null) return c2;

        throw new RuntimeException("No geocoding results for: " + place);
    }

    private Coordinates geocode(String placeQuery) throws Exception {
        String q = URLEncoder.encode(placeQuery, StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search"
                + "?name=" + q
                + "&count=1"
                + "&language=el"
                + "&format=json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Geocoding failed: " + response.statusCode());
        }

        GeocodingResponse geo = mapper.readValue(response.body(), GeocodingResponse.class);
        if (geo == null || geo.results() == null || geo.results().isEmpty()) {
            System.out.println("[WEATHER] Geocoding returned 0 results for: '" + placeQuery + "'");
            return null;
        }

        GeocodingResponse.Result r = geo.results().get(0);
        if (r.latitude() == null || r.longitude() == null) {
            System.out.println("[WEATHER] Geocoding returned null coords for: '" + placeQuery + "'");
            return null;
        }

        System.out.println("[WEATHER] Geocoding OK: '" + placeQuery + "' -> "
                + r.latitude() + "," + r.longitude() + " (" + r.name() + ", " + r.country() + ")");

        return new Coordinates(r.latitude(), r.longitude());
    }

    private OpenMeteoResponse getCurrentWeather(double lat, double lon) throws Exception {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + lat
                + "&longitude=" + lon
                + "&current_weather=true";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Weather API failed: " + response.statusCode());
        }

        return mapper.readValue(response.body(), OpenMeteoResponse.class);
    }

    private String cleanPlace(String place) {
        if (place == null) return "";
        // keep first chunk before '-'
        String first = place.split("-")[0];
        return first.trim();
    }

    private String normalize(String place) {
        return cleanPlace(place).toLowerCase();
    }

    private String describeWeather(int code) {
        return switch (code) {
            case 0 -> "Clear";
            case 1, 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45, 48 -> "Fog";
            case 51, 53, 55 -> "Drizzle";
            case 61, 63, 65 -> "Rain";
            case 71, 73, 75 -> "Snow";
            case 95 -> "Thunderstorm";
            default -> "Unknown";
        };
    }

    private record Coordinates(double lat, double lon) {}
}