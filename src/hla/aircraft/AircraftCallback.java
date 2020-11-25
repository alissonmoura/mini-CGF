package hla.aircraft;

import hla.destination.HlaDestination;

public interface AircraftCallback {
    void reflect(HlaDestination destination);

    void remove(HlaDestination destination);
}