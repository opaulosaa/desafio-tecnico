package service;

import dto.CambioDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CambioService {

    private final HttpClient client = HttpClient.newHttpClient();

    public Map<String, CambioDto> getCotacoes(List<String> moedas) {
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
                System.out.println("Erro ao buscar cotações: resposta inesperada da API (status " + response.statusCode() + ").");
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), new TypeReference<Map<String, CambioDto>>() {});
        } catch (Exception e) {
            System.out.println("Erro ao buscar cotações: " + e.getMessage());
            return null;
        }
    }
}
