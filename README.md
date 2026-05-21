# Desafio Técnico — Consulta de CEP, Clima e Câmbio

Aplicação Java via terminal que, a partir de um CEP brasileiro, retorna informações de endereço e clima da cidade. Oferece também consulta de cotações de moedas em BRL, de forma independente do CEP.

---

## Funcionalidades

- Consulta de endereço a partir de um CEP (logradouro, bairro, cidade e UF)
- Consulta do clima atual da cidade retornada pelo CEP (temperatura, vento e condição do tempo)
- Consulta de cotações de moedas estrangeiras em Real (BRL)
- Validação de entrada com mensagens de erro amigáveis

---

## APIs utilizadas

| API | Finalidade | Autenticação |
|---|---|---|
| [ViaCEP](https://viacep.com.br) | CEP → endereço | Não requer |
| [Open-Meteo Geocoding](https://open-meteo.com/en/docs/geocoding-api) | Cidade → coordenadas geográficas | Não requer |
| [Open-Meteo Forecast](https://open-meteo.com/en/docs) | Coordenadas → clima atual | Não requer |
| [AwesomeAPI](https://docs.awesomeapi.com.br/api-de-moedas) | Sigla da moeda → cotação em BRL | Não requer |

---

## Tecnologias

- Java 24
- Maven
- Jackson Databind (desserialização de JSON)
- `java.net.http.HttpClient` (requisições HTTP nativas do Java 11+)

---

## Estrutura do projeto

```
src/main/java/
├── Main.java                        # Ponto de entrada e orquestração do fluxo
├── dto/
│   ├── EnderecoDto.java             # Dados de endereço (ViaCEP)
│   ├── GeocodingResultDto.java      # Coordenadas geográficas
│   ├── GeocodingResponseDto.java    # Wrapper da resposta de geocodificação
│   ├── ClimaDto.java                # Resposta completa da API de clima
│   ├── ClimaAtualDto.java           # Dados do clima atual
│   └── CambioDto.java              # Dados de cotação de uma moeda
└── service/
    ├── ApiService.java              # Consulta endereço via ViaCEP
    ├── GeocodingService.java        # Converte cidade em coordenadas
    ├── ClimaService.java            # Consulta clima via Open-Meteo
    └── CambioService.java           # Consulta cotações via AwesomeAPI
```

---

## Como executar

**Pré-requisitos:** Java 24 e Maven instalados.

```bash
mvn clean compile exec:java
```

Para evitar problemas de encoding com caracteres especiais:

```bash
mvn clean compile exec:java -Dfile.encoding=UTF-8
```

---

## Fluxo da aplicação

```
Usuário digita o CEP
        │
        ▼
  ApiService ──────────────► ViaCEP
  Retorna endereço            CEP → logradouro, bairro, cidade, UF
        │
        ▼
  GeocodingService ─────────► Open-Meteo Geocoding
  Recebe nome da cidade        Cidade → latitude, longitude
        │
        ▼
  ClimaService ─────────────► Open-Meteo Forecast
  Recebe lat/lon               Coordenadas → temperatura, vento, condição
        │
        ▼
  Usuário digita as moedas (opcional)
        │
        ▼
  CambioService ────────────► AwesomeAPI
  Recebe siglas (USD, EUR…)    Moeda → compra, venda, variação em BRL
```

---

## Exemplo de saída

```
Digite o CEP: 60192-055

--- Endereço ---
Logradouro: Rua Bento Albuquerque
Bairro    : Cocó
Cidade    : Fortaleza
UF        : CE

--- Clima em Fortaleza ---
Temperatura : 30.8°C
Vento       : 14.3 km/h
Condição    : Parcialmente nublado

Digite as moedas para consultar câmbio separadas por vírgula (ex: USD,EUR,GBP) ou Enter para pular: USD,EUR

--- Câmbio (em BRL) ---
USD   Compra: R$ 5.0588    Venda: R$ 5.0618    Variação: +1.02%
EUR   Compra: R$ 5.8685    Venda: R$ 5.8823    Variação: +0.59%
```
