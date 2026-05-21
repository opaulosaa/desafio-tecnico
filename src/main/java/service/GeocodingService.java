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

public class GeocodingService {
    public GeocodingService() {
    }

    public GeocodingResultDto getCoordenadas(String cidade) {
        try {
            String nomeCodificado = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
            String url = "https://geocoding-api.open-meteo.com/v1/search?name=" + nomeCodificado
                    + "&count=1&language=pt&format=json";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            GeocodingResponseDto geocodingResponse = (GeocodingResponseDto) mapper.readValue((String) response.body(),
                    GeocodingResponseDto.class);
            return geocodingResponse.getResults() != null && !geocodingResponse.getResults().isEmpty()
                    ? (GeocodingResultDto) geocodingResponse.getResults().get(0)
                    : null;
        } catch (Exception e) {
            System.out.println("Erro ao buscar coordenadas: " + e.getMessage());
            return null;
        }
    }
}
