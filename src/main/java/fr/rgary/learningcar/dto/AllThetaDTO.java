package fr.rgary.learningcar.dto;

import java.util.List;

public class AllThetaDTO {

    public List<ThetaDTO> thetas;

    private AllThetaDTO() {
    }

    public AllThetaDTO(List<ThetaDTO> thetas) {
        this.thetas = thetas;
    }

    public List<ThetaDTO> getThetas() {
        return thetas;
    }
}
