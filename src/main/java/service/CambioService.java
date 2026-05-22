package service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CambioDto;
import exception.ApiException;
import exception.ApiStatusException;
import exception.ApiTimeoutException;

public class CambioService {

    private final HttpClient client = HttpClient.newHttpClient();

    public Map<String, CambioDto> getCotacoes(List<String> moedas) throws ApiException {
        try {
            String pares = moedas.stream()
                    .map(m -> m.toUpperCase() + "-BRL")
                    .collect(Collectors.joining(","));

            String url = "https://economia.awesomeapi.com.br/json/last/" + pares;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiStatusException(
                        "A API de câmbio retornou status " + response.statusCode() + ".",
                        response.statusCode());
            }

            return new ObjectMapper().readValue(response.body(), new TypeReference<Map<String, CambioDto>>() {});

        } catch (HttpTimeoutException e) {
            throw new ApiTimeoutException("A API de câmbio não respondeu dentro do tempo limite.", e);
        } catch (JsonProcessingException e) {
            throw new ApiException("Não foi possível interpretar a resposta da API de câmbio.", e);
        } catch (IOException e) {
            throw new ApiException("Erro de comunicação com a API de câmbio: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("A requisição ao serviço de câmbio foi interrompida.", e);
        }
    }
}
