package view;



import java.awt.Canvas;

import java.awt.Component;

import java.awt.Dimension;

import java.awt.Graphics;

import java.awt.Image;

import java.io.File;

import java.util.HashMap;

import java.util.Map;



import javax.imageio.ImageIO;



public class Viewer extends Canvas {

    /**

     *

     */

    private static final long serialVersionUID = 1L;

    private Image bufferImage = null;

    private Graphics bufferGraphics;

    private Image mapImage;

    private int imageWidth = -1;

    private int imageHeight = -1;

    private Dimension preferredSize;

    private Frame frame = new Frame(this, "Viewer");



    private Map<Object, Component> components = new HashMap<>();



    public void register(Object key, Component component) {

        synchronized (components) {

            components.put(key, component);

        }

    }



    public Viewer() {

        try {

            mapImage = ImageIO.read(new File("peter-karta2.png"));

            while (imageWidth < 0 || imageHeight < 0) {

                imageWidth = mapImage.getWidth(null);

                imageHeight = mapImage.getHeight(null);

                Thread.sleep(5);

            }

            preferredSize = new Dimension(imageWidth, imageHeight);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }



    @Override

    public void update(Graphics g) {

        // create the bufferImage buffer and associated Graphics

        if (bufferImage == null) {

            bufferImage = createImage(imageWidth, imageHeight);

            bufferGraphics = bufferImage.getGraphics();

        }

        // clear the exposed area

        bufferGraphics.clearRect(0, 0, imageWidth, imageHeight);

        // do normal redraw

        paint(bufferGraphics);

        // transfer bufferImage to window

        g.drawImage(bufferImage, 0, 0, this);

    }



    @Override

    public void paint(Graphics graphics) {

        graphics.drawImage(mapImage, 0, 0, imageWidth, imageHeight, null);

        synchronized (components) {

            for (Component c : components.values()) {

                c.paint(graphics);

            }

        }

    }



    @Override

    public void show() {

        frame.show();

    }



    @Override

    public Dimension getSize() {

        return preferredSize;

    }



    public void unregister(Object key) {

        synchronized (components) {

            components.remove(key);

        }

    }



    public boolean isRegistered(Object key) {

        boolean registered = false;

        synchronized (components) {

            registered = (components.get(key) != null);

        }

        return registered;

    }

}