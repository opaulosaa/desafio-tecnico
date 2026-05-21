package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClimaAtualDto {

    @JsonProperty("temperature_2m")
    private double temperatura;

    @JsonProperty("windspeed_10m")
    private double velocidadeVento;

    @JsonProperty("weathercode")
    private int codigoClima;

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getVelocidadeVento() {
        return velocidadeVento;
    }

    public void setVelocidadeVento(double velocidadeVento) {
        this.velocidadeVento = velocidadeVento;
    }

    public int getCodigoClima() {
        return codigoClima;
    }

    public void setCodigoClima(int codigoClima) {
        this.codigoClima = codigoClima;
    }
}
