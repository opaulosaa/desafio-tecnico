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
import exception.ApiException;
import exception.ApiStatusException;
import exception.ApiTimeoutException;

public class ApiService {

    private final HttpClient client = HttpClient.newHttpClient();

    public EnderecoDto getEndereco(String cep) throws ApiException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://viacep.com.br/ws/" + cep + "/json/"))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiStatusException(
                        "A API de endereços retornou status " + response.statusCode() + ".",
                        response.statusCode());
            }

            // Resposta válida da API indicando CEP inexistente — não é erro de sistema
            if (response.body().contains("\"erro\"")) {
                return null;
            }

            return new ObjectMapper().readValue(response.body(), EnderecoDto.class);

        } catch (HttpTimeoutException e) {
            throw new ApiTimeoutException("A API de endereços não respondeu dentro do tempo limite.", e);
        } catch (JsonProcessingException e) {
            throw new ApiException("Não foi possível interpretar a resposta da API de endereços.", e);
        } catch (IOException e) {
            throw new ApiException("Erro de comunicação com a API de endereços: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("A requisição ao serviço de endereços foi interrompida.", e);
        }
    }
}
