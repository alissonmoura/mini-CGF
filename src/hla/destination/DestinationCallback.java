package hla.destination;

import hla.aircraft.HlaAircraft;

public interface DestinationCallback {
    void reflect(HlaAircraft aircraft);

    void remove(HlaAircraft aircraft);
}