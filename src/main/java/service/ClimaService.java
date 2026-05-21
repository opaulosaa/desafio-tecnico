package service;

import dto.ClimaDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClimaService {

    public ClimaDto getClima(double latitude, double longitude) {
        try {
            String url = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + latitude
                    + "&longitude=" + longitude
                    + "&current=temperature_2m,weathercode,windspeed_10m"
                    + "&timezone=America%2FSao_Paulo";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), ClimaDto.class);
        } catch (Exception e) {
            System.out.println("Erro ao buscar clima: " + e.getMessage());
            return null;
        }
    }
}
