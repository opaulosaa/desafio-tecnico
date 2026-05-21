import service.ApiService;

import java.util.Scanner;

import dto.EnderecoDto;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ApiService apiService = new ApiService();

        while (true) {
            System.out.println("Digite o CEP: ");
            String input = scanner.nextLine().trim();

            if (!input.matches("[0-9\\-]+")) {
                System.out.println("Erro: o CEP deve conter apenas números. Tente novamente.\n");
                continue;
            }

            String cep = input.replaceAll("-", "");

            if (cep.length() != 8) {
                System.out.println(
                        "Erro: o CEP deve ter exatamente 8 digitos (ex: 010001-000 ou 01001000). Tente novamente.\n");
                continue;
            }

            EnderecoDto endereco = apiService.getEndereco(cep);

            if (endereco == null) {
                System.out.println("CEP não encontrado. Verifique os números digitados e tente novamente.\n");
                continue;
            }

            System.out.println("\nLogradouro: " + endereco.getLogradouro());
            System.out.println("Bairro    : " + endereco.getBairro());
            System.out.println("Cidade    : " + endereco.getLocalidade());
            System.out.println("UF        : " + endereco.getUf());
            break;

        }
        scanner.close();
    }
}
