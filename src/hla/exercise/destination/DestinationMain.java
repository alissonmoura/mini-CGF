package hla.exercise.destination;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import hla.aircraft.HlaAircraft;
import hla.destination.DestinationCallback;
import hla.destination.DestinationChangedListener;
import hla.destination.DestinationSimulation;
import hla.destination.HlaDestination;
import hla.rti1516e.exceptions.RTIexception;
import util.RandomCoordinate;
import view.AircraftView;
import view.DestinationView;
import view.Frame;
import view.Viewer;

public class DestinationMain {
    public static void main(String[] argv) {
        DestinationMain main = new DestinationMain();
        main.run();
    }

    private void run() {
        try {
            HlaDestination destination = new HlaDestination(RandomCoordinate.getX(), RandomCoordinate.getY());

            Viewer canvas = new Viewer();
            Frame viewer = new Frame(canvas, "Destination");
            DestinationSimulation simulation = new DestinationSimulation(destination);
            canvas.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    simulation.update(e.getX(), e.getY());
                }
            });

            canvas.register(destination, new DestinationView(destination));

            DestinationFederate federate = new DestinationFederate(new DestinationCallback() {

                @Override
                public void reflect(HlaAircraft aircraft) {
                    if (!canvas.isRegistered(aircraft)) {
                        canvas.register(aircraft, new AircraftView(aircraft));
                    }
                    simulation.reflect(aircraft);
                }

                @Override
                public void remove(HlaAircraft aircraft) {
                    canvas.unregister(aircraft);
                }

            });
            federate.init();
            federate.register(destination);

            simulation.addChangeListener(new DestinationChangedListener() {
                @Override
                public void destinationChanged(HlaDestination destination) {
                    try {
                        federate.update(destination);
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
