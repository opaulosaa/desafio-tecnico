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
import dto.EnderecoDto;

public class ApiService {

    private final HttpClient client = HttpClient.newHttpClient();

    public EnderecoDto getEndereco(String cep) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://viacep.com.br/ws/" + cep + "/json/"))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Erro ao buscar endereço: a API retornou status " + response.statusCode() + ".");
                return null;
            }

            // Resposta válida da API indicando CEP inexistente — não é erro de sistema
            if (response.body().contains("\"erro\"")) {
                return null;
            }

            return new ObjectMapper().readValue(response.body(), EnderecoDto.class);

        } catch (HttpTimeoutException e) {
            System.out.println("Erro ao buscar endereço: tempo limite excedido. Verifique sua conexão.");
            return null;
        } catch (JsonProcessingException e) {
            System.out.println("Erro ao buscar endereço: não foi possível interpretar a resposta da API.");
            return null;
        } catch (IOException e) {
            System.out.println("Erro ao buscar endereço: falha de comunicação — " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Erro ao buscar endereço: a requisição foi interrompida.");
            return null;
        }
    }
}
