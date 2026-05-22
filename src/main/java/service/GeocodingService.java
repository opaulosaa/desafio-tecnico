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
                System.out.println("Erro ao buscar coordenadas: a API retornou status " + response.statusCode() + ".");
                return null;
            }

            GeocodingResponseDto geocodingResponse =
                    new ObjectMapper().readValue(response.body(), GeocodingResponseDto.class);

            // Lista vazia é resposta válida — cidade não encontrada no banco da API
            return geocodingResponse.getResults() != null && !geocodingResponse.getResults().isEmpty()
                    ? geocodingResponse.getResults().get(0)
                    : null;

        } catch (HttpTimeoutException e) {
            System.out.println("Erro ao buscar coordenadas: tempo limite excedido.");
            return null;
        } catch (JsonProcessingException e) {
            System.out.println("Erro ao buscar coordenadas: não foi possível interpretar a resposta da API.");
            return null;
        } catch (IOException e) {
            System.out.println("Erro ao buscar coordenadas: falha de comunicação — " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Erro ao buscar coordenadas: a requisição foi interrompida.");
            return null;
        }
    }
}
