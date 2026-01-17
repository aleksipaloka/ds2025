package gr.hua.dit.ds2025.core.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class EmailProviderClient {

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${email.provider.baseUrl}")
    private String baseUrl;

    @Value("${email.provider.apiKey}")
    private String apiKey;

    @Value("${email.provider.fromEmail}")
    private String fromEmail;

    @Value("${email.provider.enabled:false}")
    private boolean enabled;

    /**
     * External secured POST call (Authorization: Bearer <apiKey>)
     */
    public void sendEmail(final String to, final String subject, final String html) {
        try {
            if (!enabled) {
                System.out.println("[EMAIL][MOCK] to=" + to + " subject=" + subject);
                return;
            }

            // SendGrid-like JSON
            Map<String, Object> body = Map.of(
                    "personalizations", List.of(Map.of("to", List.of(Map.of("email", to)))),
                    "from", Map.of("email", fromEmail),
                    "subject", subject,
                    "content", List.of(Map.of("type", "text/html", "value", html))
            );

            String json = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v3/mail/send"))
                    .header("Content-Type", "application/json")
                    // ✅ Secured external API call with token:
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                throw new RuntimeException("Email provider error: " + resp.statusCode() + " body=" + resp.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

