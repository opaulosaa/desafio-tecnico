package service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.GeocodingResponseDto;
import dto.GeocodingResultDto;
import exception.ApiException;
import exception.ApiStatusException;
import exception.ApiTimeoutException;

public class GeocodingService {

    private final HttpClient client = HttpClient.newHttpClient();

    public GeocodingResultDto getCoordenadas(String cidade) throws ApiException {
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
                throw new ApiStatusException(
                        "A API de geocodificação retornou status " + response.statusCode() + ".",
                        response.statusCode());
            }

            GeocodingResponseDto geocodingResponse =
                    new ObjectMapper().readValue(response.body(), GeocodingResponseDto.class);

            // Lista vazia é resposta válida — cidade não encontrada no banco da API
            return geocodingResponse.getResults() != null && !geocodingResponse.getResults().isEmpty()
                    ? geocodingResponse.getResults().get(0)
                    : null;

        } catch (HttpTimeoutException e) {
            throw new ApiTimeoutException("A API de geocodificação não respondeu dentro do tempo limite.", e);
        } catch (JsonProcessingException e) {
            throw new ApiException("Não foi possível interpretar a resposta da API de geocodificação.", e);
        } catch (IOException e) {
            throw new ApiException("Erro de comunicação com a API de geocodificação: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("A requisição ao serviço de geocodificação foi interrompida.", e);
        }
    }
}
