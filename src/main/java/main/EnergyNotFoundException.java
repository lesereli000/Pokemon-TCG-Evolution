package main;

public class EnergyNotFoundException extends RuntimeException {
    public EnergyNotFoundException(String message) {
        super(message);
    }
}
