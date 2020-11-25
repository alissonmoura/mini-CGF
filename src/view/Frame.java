package view;



import java.awt.BorderLayout;

import java.awt.Canvas;



import javax.swing.JFrame;

import javax.swing.SwingUtilities;



public class Frame {



    private JFrame frame = new JFrame();

    private Canvas canvas;



    public Frame(Canvas canvas, String title) {

        this.canvas = canvas;

        frame.setTitle(title);

    }



    public void show() {

        SwingUtilities.invokeLater(new Runnable() {

            @Override

            public void run() {

                frame.getContentPane().setLayout(new BorderLayout());

                frame.getContentPane().add(canvas, BorderLayout.CENTER);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.pack();

                frame.setLocationRelativeTo(null);

                frame.setVisible(true);

            }

        });

    }



    public void repaint() {

        frame.repaint();

        canvas.repaint();

    }

}

