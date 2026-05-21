package service;

import dto.GeocodingResponseDto;
import dto.GeocodingResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeocodingService {

    public GeocodingResultDto getCoordenadas(String cidade) {
        try {
            String nomeCodificado = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
            String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + nomeCodificado + "&count=1&language=pt&format=json";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            GeocodingResponseDto geocodingResponse = mapper.readValue(response.body(), GeocodingResponseDto.class);

            if (geocodingResponse.getResults() == null || geocodingResponse.getResults().isEmpty()) {
                return null;
            }

            return geocodingResponse.getResults().get(0);
        } catch (Exception e) {
            System.out.println("Erro ao buscar coordenadas: " + e.getMessage());
            return null;
        }
    }
}
