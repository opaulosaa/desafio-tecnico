package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import dto.EnderecoDto;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                System.out.println("Erro ao buscar endereço: resposta inesperada da API (status " + response.statusCode() + ").");
                return null;
            }

            if (response.body().contains("\"erro\"")) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), EnderecoDto.class);
        } catch (Exception e) {
            System.out.println("Erro ao buscar endereço: " + e.getMessage());
            return null;
        }
    }

}
