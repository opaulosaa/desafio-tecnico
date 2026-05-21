package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClimaDto {

    private double latitude;
    private double longitude;

    @JsonProperty("current")
    private ClimaAtualDto atual;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ClimaAtualDto getAtual() {
        return atual;
    }

    public void setAtual(ClimaAtualDto atual) {
        this.atual = atual;
    }
}
