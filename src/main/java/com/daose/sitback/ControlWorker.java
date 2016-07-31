package com.daose.sitback;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.swing.SwingWorker;

/**
 * Created by STUDENT on 2016-07-30.
 */
public class ControlWorker extends SwingWorker<Void, Void> implements ValueEventListener {

    private Robot robot;
    private DatabaseReference ref;

    public ControlWorker(DatabaseReference ref) {
        try {
            robot = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
            this.ref = ref;
        } catch (AWTException e){
            e.printStackTrace();
        }
    }

    public Void doInBackground() {
        ref.addValueEventListener(this);
        return null;
    }

    @Override
    public void done(){
        System.out.println("done");
    }

    @Override
    public void onDataChange(DataSnapshot ds) {
        System.out.println("onDataChange");
        /*
        robot.keyPress(KeyEvent.VK_SPACE);
        robot.delay(50);
        robot.keyRelease(KeyEvent.VK_SPACE);
        */
    }

    @Override
    public void onCancelled(DatabaseError error){
        System.err.println("The read failed: " + error.getCode());
    }
}
