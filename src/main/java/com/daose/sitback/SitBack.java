package com.daose.sitback;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class SitBack extends JPanel implements ActionListener, DatabaseReference.CompletionListener {

    private FirebaseDatabase db;
    private DatabaseReference ref;
    private Robot robot;

    private ValueEventListener keyListener, mouseListener;

    public SitBack() {
        super(new BorderLayout());

        setup();

        JButton startButton = new JButton("Start");
        startButton.addActionListener(this);

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(this);

        JPanel testPanel = new JPanel();
        testPanel.add(startButton);
        testPanel.add(stopButton);

        add(testPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void setup() {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("room"); //TODO:: authentication
        try {
            robot = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
            //robot.setAutoDelay(50);
            robot.setAutoWaitForIdle(true);
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(1);
        }

        mouseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                Point point = MouseInfo.getPointerInfo().getLocation();
                Vector2D swipe = ds.getValue(Vector2D.class);
                int x = Math.round(swipe.x);
                int y = Math.round(swipe.y);
                onMouseMove(point.x - x, point.y - y);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        keyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot action : dataSnapshot.getChildren()) {
                    if (action.getKey().equals("button")) {
                        for (DataSnapshot ds : action.getChildren()) {
                            String keyString = ds.getValue().toString();
                            System.out.println("keyString: " + keyString);
                            if (keyString.equals("left")) {
                                onKeyPressed(MouseEvent.BUTTON1_DOWN_MASK);
                            } else if (keyString.equals("right")) {
                                onKeyPressed(MouseEvent.BUTTON3_DOWN_MASK);
                            } else if (keyString.equals("space")) {
                                onKeyPressed(KeyEvent.VK_SPACE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Start":
                clearQueue(ref);
                break;
            case "Stop":
                ref.child("swipe").removeEventListener(mouseListener);
                ref.removeEventListener(keyListener);
                break;
            default:
                break;
        }
    }

    private void clearQueue(DatabaseReference dr) {
        System.out.println("clearQueue");
        dr.setValue(null, this);
    }

    @Override
    public void onComplete(DatabaseError error, DatabaseReference ref) {
        ref.child("swipe").setValue(new Vector2D(0, 0));
        ref.child("swipe").addValueEventListener(mouseListener);
        ref.addValueEventListener(keyListener);
//        ref.child("swipe").addValueEventListener(mouseListener);
//        ref.child("button").addValueEventListener(keyListener);
    }

    public void onKeyPressed(int keyCode) {
        if (keyCode == MouseEvent.BUTTON1_DOWN_MASK || keyCode == MouseEvent.BUTTON3_DOWN_MASK) {
            robot.mousePress(keyCode);
            robot.delay(50);
            robot.mouseRelease(keyCode);
        } else {
            robot.keyPress(keyCode);
            robot.delay(50);
            robot.keyRelease(keyCode);
        }
        ref.child("button").setValue(null);
    }

    public void onMouseMove(int x, int y) {
        robot.mouseMove(x, y);
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Sit Back");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JComponent newContentPane = new SitBack();
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String args[]) {
        System.out.println("Start");
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream(FirebaseConstants.PATH_TO_JSON))
                    .setDatabaseUrl(FirebaseConstants.DATABASE_URL)
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}