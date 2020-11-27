package view;



import java.awt.Point;

import java.awt.event.MouseAdapter;

import java.awt.event.MouseEvent;



import hla.aircraft.AircraftChangedListener;

import hla.aircraft.AircraftSimulation;

import hla.aircraft.HlaAircraft;

import hla.destination.DestinationChangedListener;

import hla.destination.DestinationSimulation;

import hla.destination.HlaDestination;

import util.RandomCoordinate;



public class Main {

    public class DestinationListener implements DestinationChangedListener {



        @Override

        public void destinationChanged(HlaDestination destination) {

            aircraftSimulation.reflect(destination);

        }

    }



    public class AircraftListener implements AircraftChangedListener {



        @Override

        public void aircraftChanged(HlaAircraft aircraft) {

            destinationSimulation.reflect(aircraft);

        }

    }



    public static void main(String[] argv) {

        Main main = new Main();

        main.run();

    }



    private AircraftSimulation aircraftSimulation;

    private HlaAircraft aircraft;

    private HlaDestination destination;

    private DestinationSimulation destinationSimulation;

    private Viewer viewer;



    public Main() {

        viewer = new Viewer();



        aircraft = new HlaAircraft(100, 100);

        aircraftSimulation = new AircraftSimulation(aircraft);



        destination = new HlaDestination(200, 200);

        destinationSimulation = new DestinationSimulation(destination);



        aircraftSimulation.addChangedListener(new AircraftListener());

        destinationSimulation.addChangeListener(new DestinationListener());

        destinationSimulation.update(RandomCoordinate.getX(), RandomCoordinate.getY());



        viewer.register(aircraft, new AircraftView(aircraft));

        viewer.register(destination, new DestinationView(destination));

        viewer.addMouseListener(new MouseAdapter() {



            @Override

            public void mousePressed(MouseEvent e) {

                Point p = e.getPoint();

                destinationSimulation.update(p.x, p.y);

            }

        });

    }



    public void run() {

        viewer.show();

        while (true) {

            aircraftSimulation.tick();

            destinationSimulation.tick();

            viewer.repaint();

            try {

                Thread.sleep(5);

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

    }

}

