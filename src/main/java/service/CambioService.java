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
                System.out.println("Erro ao buscar cotações: a API retornou status " + response.statusCode() + ".");
                return null;
            }

            return new ObjectMapper().readValue(response.body(), new TypeReference<Map<String, CambioDto>>() {});

        } catch (HttpTimeoutException e) {
            System.out.println("Erro ao buscar cotações: tempo limite excedido.");
            return null;
        } catch (JsonProcessingException e) {
            System.out.println("Erro ao buscar cotações: não foi possível interpretar a resposta da API.");
            return null;
        } catch (IOException e) {
            System.out.println("Erro ao buscar cotações: falha de comunicação — " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Erro ao buscar cotações: a requisição foi interrompida.");
            return null;
        }
    }
}
