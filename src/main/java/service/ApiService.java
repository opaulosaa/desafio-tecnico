package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import dto.EnderecoDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiService {

    public EnderecoDto getEndereco(String cep) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://viacep.com.br/ws/" + cep + "/json/"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.body().contains("\"erro\"")) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), EnderecoDto.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
