package view;



import hla.aircraft.HlaAircraft;



import java.awt.Color;

import java.awt.Component;

import java.awt.Graphics;



public class AircraftView extends Component {



    /**

     *

     */

    private static final long serialVersionUID = 1L;



    public AircraftView(HlaAircraft model, Color color, int size) {

        this.model = model;

        this.size = size;

        this.color = color;

    }



    public AircraftView(HlaAircraft model) {

        this(model, Color.blue, 5);

    }



    public AircraftView(HlaAircraft model, Color color) {

        this(model, color, 5);

    }



    private HlaAircraft model;

    private double size;

    private Color color;



    private int[] xc = new int[4];

    private int[] yc = new int[4];

    private int[] shapeX = new int[] { 5, 0, -1, 0 };

    private int[] shapeY = new int[] { 0, 2, 0, -2 };



    public void paint(Graphics graphics) {

        rotate(xc, yc);

        graphics.setColor(color);

        graphics.fillPolygon(xc, yc, xc.length);

    }



    private void rotate(int[] x, int[] y) {

        double angle = model.getOrientation();

        double c = Math.cos(angle);

        double s = Math.sin(angle);

        for (int i = 0; i < x.length; i++) {

            x[i] = (int) Math.round((shapeX[i] * c - shapeY[i] * s) * size + model.getX());

            y[i] = (int) Math.round((shapeX[i] * s + shapeY[i] * c) * size + model.getY());

        }

    }



    public Component setColor(Color color) {

        this.color = color;

        return this;

    }

}