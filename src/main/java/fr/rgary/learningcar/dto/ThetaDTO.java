package fr.rgary.learningcar.dto;

public class ThetaDTO {

    public double[] data;
    public int numRows;
    public int numCols;

    private ThetaDTO() {
    }

    public ThetaDTO(double[] data, int numRows, int numCols) {
        this.data = data;
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public double[] getData() {
        return data;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }
}
