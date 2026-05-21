package service;

import dto.ClimaDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ClimaService {

    private final HttpClient client = HttpClient.newHttpClient();

    public ClimaDto getClima(double latitude, double longitude) {
        try {
            String url = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + latitude
                    + "&longitude=" + longitude
                    + "&current=temperature_2m,weathercode,windspeed_10m"
                    + "&timezone=America%2FSao_Paulo";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Erro ao buscar clima: resposta inesperada da API (status " + response.statusCode() + ").");
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), ClimaDto.class);
        } catch (Exception e) {
            System.out.println("Erro ao buscar clima: " + e.getMessage());
            return null;
        }
    }
}
