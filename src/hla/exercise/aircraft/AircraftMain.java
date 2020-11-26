package hla.exercise.aircraft;

import hla.aircraft.AircraftCallback;
import hla.aircraft.AircraftChangedListener;
import hla.aircraft.AircraftSimulation;
import hla.aircraft.HlaAircraft;
import hla.destination.HlaDestination;
import hla.rti1516e.exceptions.RTIexception;
import util.RandomCoordinate;
import view.AircraftView;
import view.DestinationView;
import view.Frame;
import view.Viewer;

public class AircraftMain {
    public static void main(String[] argv) {
        AircraftMain main = new AircraftMain();
        main.run();
    }

    private void run() {
        try {
            HlaAircraft aircraft = new HlaAircraft(RandomCoordinate.getX(), RandomCoordinate.getY());
            Viewer canvas = new Viewer();
            canvas.register(aircraft, new AircraftView(aircraft));
            Frame viewer = new Frame(canvas, "Aircraft");
            AircraftSimulation simulation = new AircraftSimulation(aircraft);
            AircraftFederate federate = new AircraftFederate(new AircraftCallback() {

                @Override
                public void reflect(HlaDestination destination) {
                    if (!canvas.isRegistered(destination)) {
                        canvas.register(destination, new DestinationView(destination));
                    }
                    simulation.reflect(destination);
                }

                @Override
                public void remove(HlaDestination destination) {
                    simulation.reflect(destination);
                }
            });
            federate.init();
            federate.register(aircraft);

            simulation.addChangedListener(new AircraftChangedListener() {
                @Override
                public void aircraftChanged(HlaAircraft aircraft) {
                    try {
                        federate.update(aircraft);
                    } catch (RTIexception e) {
                        e.printStackTrace();
                    }
                }
            });

            viewer.show();

            while (true) {
                simulation.tick();
                viewer.repaint();
                Thread.sleep(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}