package hla.destination;

import java.util.LinkedList;
import java.util.List;

import util.RandomCoordinate;
import model.Simulation;
import hla.aircraft.HlaAircraft;

public class DestinationSimulation implements Simulation {
    private HlaDestination destination = new HlaDestination(RandomCoordinate.getX(), RandomCoordinate.getY());;
    private HlaAircraft aircraft = new HlaAircraft(RandomCoordinate.getX(), RandomCoordinate.getY());

    private List<DestinationChangedListener> destinationListeners = new LinkedList<>();

    public DestinationSimulation(HlaDestination destination) {
        this.destination = destination;
    }

    public void addChangeListener(DestinationChangedListener listener) {
        destinationListeners.add(listener);
    }

    public void reflect(HlaAircraft aircraft) {
        this.aircraft.setX(aircraft.getX());
        this.aircraft.setY(aircraft.getY());
        this.aircraft.setOrientation(aircraft.getOrientation());
    }

    public void tick() {
        if (distance(aircraft, destination) < 20) {
            update(RandomCoordinate.getX(), RandomCoordinate.getY());
        }
    }

    public void update(int x, int y) {
        synchronized (destination) {
            destination.setX(x);
            destination.setY(y);
            for (DestinationChangedListener listener : destinationListeners) {
                listener.destinationChanged(destination);
            }
        }
    }

    private double distance(HlaAircraft aircraft, HlaDestination destination) {
        double x = aircraft.getX() - destination.getX();
        double y = aircraft.getY() - destination.getY();
        return Math.sqrt(x * x + y * y);
    }
}