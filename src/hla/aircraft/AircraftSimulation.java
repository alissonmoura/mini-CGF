package hla.aircraft;

import java.util.LinkedList;
import java.util.List;

import model.Simulation;
import hla.destination.HlaDestination;
import util.RandomCoordinate;

public class AircraftSimulation implements Simulation {
    private static final double DEFAULT_TURN_SPEED = 0.01;
    private static final double DEFAULT_SPEED = 0.3;
    private static final double TWO_PI = Math.PI * 2;
    private HlaAircraft aircraft;
    private double turnSpeed;
    private double speed;
    private HlaDestination destination = new HlaDestination(RandomCoordinate.getX(), RandomCoordinate.getY());

    public AircraftSimulation(HlaAircraft aircraft) {
        this(DEFAULT_TURN_SPEED, DEFAULT_SPEED, aircraft);
    }

    public AircraftSimulation(double turnSpeed, double speed, HlaAircraft aircraft) {
        this.turnSpeed = turnSpeed;
        this.speed = speed;
        this.aircraft = aircraft;
    }

    private List<AircraftChangedListener> aircraftChangeListeners = new LinkedList<>();

    public void addChangedListener(AircraftChangedListener listener) {
        aircraftChangeListeners.add(listener);
    }

    public void tick() {
        synchronized (this) {
            double orientation = normalize(calculateNewOrientation(aircraft.getOrientation()));
            double c = Math.cos(orientation);
            double s = Math.sin(orientation);
            double x = aircraft.getX() + c * speed;
            double y = aircraft.getY() + s * speed;
            update(x, y, orientation);
        }
    }

    public void update(double x, double y, double orientation) {
        aircraft.setX(x);
        aircraft.setY(y);
        aircraft.setOrientation(orientation);
        for (AircraftChangedListener listener : aircraftChangeListeners) {
            listener.aircraftChanged(aircraft);
        }
    }

    private double calculateNewOrientation(double orientation) {
        // translate to origin
        double dx = destination.getX() - aircraft.getX();
        double dy = destination.getY() - aircraft.getY();

        // rotate destination vector to negative orientation (align orientation
        // with x-axis)
        double theta = -orientation;
        double xprime = dx * Math.cos(theta) - dy * Math.sin(theta);
        double yprime = dx * Math.sin(theta) + dy * Math.cos(theta);

        // calculate relative destination vector angle
        double desiredOrientation = Math.atan2(yprime, xprime);

        // calculate angle to turn
        double rotation = Math.min(Math.abs(desiredOrientation), turnSpeed);

        // turn whichever way is shortest
        if (yprime >= 0) {
            return orientation + rotation; // turn left
        }
        return orientation - rotation; // turn right
    }

    private double normalize(double d) {
        while (d > TWO_PI) {
            d -= TWO_PI;
        }
        while (d < 0) {
            d += TWO_PI;
        }
        return d;
    }

    public void reflect(HlaDestination destination) {
        synchronized (this) {
            this.destination.setX(destination.getX());
            this.destination.setY(destination.getY());
        }
    }
}