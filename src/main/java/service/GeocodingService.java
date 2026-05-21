package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.GeocodingResponseDto;
import dto.GeocodingResultDto;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class GeocodingService {

    private final HttpClient client = HttpClient.newHttpClient();

    public GeocodingResultDto getCoordenadas(String cidade) {
        try {
            String nomeCodificado = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
            String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + nomeCodificado
                    + "&count=1&language=pt&format=json";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Erro ao buscar coordenadas: resposta inesperada da API (status " + response.statusCode() + ").");
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            GeocodingResponseDto geocodingResponse = mapper.readValue(response.body(), GeocodingResponseDto.class);
            return geocodingResponse.getResults() != null && !geocodingResponse.getResults().isEmpty()
                    ? geocodingResponse.getResults().get(0)
                    : null;
        } catch (Exception e) {
            System.out.println("Erro ao buscar coordenadas: " + e.getMessage());
            return null;
        }
    }
}
