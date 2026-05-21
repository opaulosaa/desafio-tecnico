import service.ApiService;
import service.GeocodingService;
import service.ClimaService;
import dto.EnderecoDto;
import dto.GeocodingResultDto;
import dto.ClimaDto;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ApiService apiService = new ApiService();
        GeocodingService geocodingService = new GeocodingService();
        ClimaService climaService = new ClimaService();

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

            EnderecoDto endereco = apiService.getEndereco(cep);

            if (endereco == null) {
                System.out.println("CEP não encontrado. Verifique os números digitados e tente novamente.\n");
                continue;
            }

            System.out.println("\n--- Endereço ---");
            System.out.println("Logradouro: " + endereco.getLogradouro());
            System.out.println("Bairro    : " + endereco.getBairro());
            System.out.println("Cidade    : " + endereco.getLocalidade());
            System.out.println("UF        : " + endereco.getUf());

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
