import service.ApiService;
import service.GeocodingService;
import service.ClimaService;
import service.CambioService;
import dto.EnderecoDto;
import dto.GeocodingResultDto;
import dto.ClimaDto;
import dto.CambioDto;
import exception.ApiException;
import exception.ApiStatusException;
import exception.ApiTimeoutException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ApiService apiService = new ApiService();
        GeocodingService geocodingService = new GeocodingService();
        ClimaService climaService = new ClimaService();
        CambioService cambioService = new CambioService();

        // --- Bloco CEP + Clima ---
        while (true) {
            System.out.print("Digite o CEP: ");
            String input = scanner.nextLine().trim();

            if (!input.matches("[0-9\\-]+")) {
                System.out.println("Erro: o CEP deve conter apenas números. Tente novamente.\n");
                continue;
            }

            String cep = input.replaceAll("-", "");

            if (cep.length() != 8) {
                System.out.println("Erro: o CEP deve ter exatamente 8 dígitos (ex: 01001000 ou 01001-000). Tente novamente.\n");
                continue;
            }

            EnderecoDto endereco;
            try {
                endereco = apiService.getEndereco(cep);
            } catch (ApiTimeoutException e) {
                System.out.println("Tempo limite excedido ao buscar o endereço. Verifique sua conexão e tente novamente.\n");
                continue;
            } catch (ApiStatusException e) {
                System.out.println("Serviço de CEP indisponível (status " + e.getStatusCode() + "). Tente novamente mais tarde.\n");
                continue;
            } catch (ApiException e) {
                System.out.println("Erro ao buscar endereço: " + e.getMessage() + "\n");
                continue;
            }

            if (endereco == null) {
                System.out.println("CEP não encontrado. Verifique os números digitados e tente novamente.\n");
                continue;
            }

            System.out.println("\n--- Endereço ---");
            System.out.println("CEP       : " + endereco.getCep());
            System.out.println("Logradouro: " + endereco.getLogradouro());
            System.out.println("Bairro    : " + endereco.getBairro());
            System.out.println("Cidade    : " + endereco.getLocalidade());
            System.out.println("UF        : " + endereco.getUf());

            // Clima é não-fatal — falha aqui exibe aviso mas não impede mostrar o endereço
            try {
                GeocodingResultDto coordenadas = geocodingService.getCoordenadas(endereco.getLocalidade());

                if (coordenadas != null) {
                    ClimaDto clima = climaService.getClima(coordenadas.getLatitude(), coordenadas.getLongitude());

                    if (clima != null && clima.getAtual() != null) {
                        System.out.println("\n--- Clima em " + endereco.getLocalidade() + " ---");
                        System.out.println("Temperatura : " + clima.getAtual().getTemperatura() + "°C");
                        System.out.println("Vento       : " + clima.getAtual().getVelocidadeVento() + " km/h");
                        System.out.println("Condição    : " + interpretarCodigoClima(clima.getAtual().getCodigoClima()));
                    }
                }
            } catch (ApiTimeoutException e) {
                System.out.println("\nAviso: dados de clima indisponíveis — tempo limite excedido.");
            } catch (ApiStatusException e) {
                System.out.println("\nAviso: dados de clima indisponíveis — serviço retornou status " + e.getStatusCode() + ".");
            } catch (ApiException e) {
                System.out.println("\nAviso: dados de clima indisponíveis — " + e.getMessage());
            }

            break;
        }

        // --- Bloco Câmbio ---
        while (true) {
            System.out.print("\nDigite as moedas para consultar câmbio separadas por vírgula (ex: USD,EUR,GBP) ou Enter para pular: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                break;
            }

            List<String> moedas = Arrays.stream(input.split(","))
                    .map(String::trim)
                    .filter(m -> !m.isEmpty())
                    .collect(Collectors.toList());

            List<String> invalidas = moedas.stream()
                    .filter(m -> !m.matches("[A-Za-z]{2,4}"))
                    .collect(Collectors.toList());

            if (!invalidas.isEmpty()) {
                System.out.println("Código(s) inválido(s): " + invalidas + ". Use siglas como USD, EUR, GBP. Tente novamente.\n");
                continue;
            }

            Map<String, CambioDto> cotacoes;
            try {
                cotacoes = cambioService.getCotacoes(moedas);
            } catch (ApiTimeoutException e) {
                System.out.println("Tempo limite excedido ao buscar cotações. Tente novamente.\n");
                continue;
            } catch (ApiStatusException e) {
                System.out.println("Serviço de câmbio indisponível (status " + e.getStatusCode() + "). Verifique as siglas e tente novamente.\n");
                continue;
            } catch (ApiException e) {
                System.out.println("Erro ao buscar cotações: " + e.getMessage() + "\n");
                continue;
            }

            if (cotacoes.isEmpty()) {
                System.out.println("Nenhuma cotação retornada. Verifique as siglas e tente novamente.\n");
                continue;
            }

            System.out.println("\n--- Câmbio (em BRL) ---");
            for (CambioDto cotacao : cotacoes.values()) {
                String variacao = Double.parseDouble(cotacao.getPctChange()) >= 0
                        ? "+" + cotacao.getPctChange()
                        : cotacao.getPctChange();
                System.out.printf("%-4s  Compra: R$ %-8s  Venda: R$ %-8s  Variação: %s%%%n",
                        cotacao.getCode(), cotacao.getBid(), cotacao.getAsk(), variacao);
            }

            break;
        }

        scanner.close();
    }

    private static String interpretarCodigoClima(int codigo) {
        return switch (codigo) {
            case 0 -> "Céu limpo";
            case 1, 2, 3 -> "Parcialmente nublado";
            case 45, 48 -> "Névoa";
            case 51, 53, 55 -> "Garoa";
            case 61, 63, 65 -> "Chuva";
            case 71, 73, 75 -> "Neve";
            case 80, 81, 82 -> "Pancadas de chuva";
            case 95 -> "Tempestade";
            case 96, 99 -> "Tempestade com granizo";
            default -> "Condição não identificada (código " + codigo + ")";
        };
    }
}
