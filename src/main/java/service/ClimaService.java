package service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ClimaDto;
import exception.ApiException;
import exception.ApiStatusException;
import exception.ApiTimeoutException;

public class ClimaService {

    private final HttpClient client = HttpClient.newHttpClient();

    public ClimaDto getClima(double latitude, double longitude) throws ApiException {
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
                throw new ApiStatusException(
                        "A API de clima retornou status " + response.statusCode() + ".",
                        response.statusCode());
            }

            return new ObjectMapper().readValue(response.body(), ClimaDto.class);

        } catch (HttpTimeoutException e) {
            throw new ApiTimeoutException("A API de clima não respondeu dentro do tempo limite.", e);
        } catch (JsonProcessingException e) {
            throw new ApiException("Não foi possível interpretar a resposta da API de clima.", e);
        } catch (IOException e) {
            throw new ApiException("Erro de comunicação com a API de clima: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("A requisição ao serviço de clima foi interrompida.", e);
        }
    }
}
