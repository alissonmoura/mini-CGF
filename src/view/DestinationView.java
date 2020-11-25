package view;



import hla.destination.HlaDestination;



import java.awt.Color;

import java.awt.Component;

import java.awt.Graphics;



public class DestinationView extends Component {



    /**

     *

     */

    private static final long serialVersionUID = -3099329993584061016L;



    private HlaDestination destination;



    public DestinationView(HlaDestination destination) {

        this.destination = destination;

    }



    public void paint(Graphics g) {

        g.setColor(Color.black);

        g.fillOval(destination.getX() - 2, destination.getY() - 2, 5, 5);

    }

}