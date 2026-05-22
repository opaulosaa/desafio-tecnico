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
                System.out.println("Erro ao buscar clima: a API retornou status " + response.statusCode() + ".");
                return null;
            }

            return new ObjectMapper().readValue(response.body(), ClimaDto.class);

        } catch (HttpTimeoutException e) {
            System.out.println("Erro ao buscar clima: tempo limite excedido.");
            return null;
        } catch (JsonProcessingException e) {
            System.out.println("Erro ao buscar clima: não foi possível interpretar a resposta da API.");
            return null;
        } catch (IOException e) {
            System.out.println("Erro ao buscar clima: falha de comunicação — " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Erro ao buscar clima: a requisição foi interrompida.");
            return null;
        }
    }
}
