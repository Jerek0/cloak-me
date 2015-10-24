package com.jeremy_minie.helloagaincrm.util.timers;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Debounce {

    private static final String TAG = "Debounce";
    private static final boolean DEBUG = true;
    private Timer timer = null;
    private long lastHit = 0;
    private long debounceDelay = 0;
    private long checkDelay = 0;

    public abstract void execute();

    public Debounce(long debounceDelay, long checkDelay) {
        this.debounceDelay = debounceDelay;
        this.checkDelay = checkDelay;
    }

    public void hit(){
        if(DEBUG) Log.d(TAG, "HIT !");

        lastHit = System.currentTimeMillis();
        if(this.timer != null){
            this.timer.cancel();
            this.timer = null;
        }
        this.timer = new Timer("Debounce", true);
        this.timer.schedule(new DebounceTask(this), 0, checkDelay);
    }

    private void checkExecute(){
        if((System.currentTimeMillis() - lastHit) > debounceDelay){
            if(DEBUG) Log.d(TAG, "Now !");
            this.timer.cancel();
            this.timer = null;
            execute();
        } else {
            if(DEBUG) Log.d(TAG, "Not yet");
        }
    }

    private class DebounceTask extends TimerTask {

        private Debounce debounceInstance = null;

        public DebounceTask(Debounce debounceInstance) {
            this.debounceInstance = debounceInstance;
        }

        @Override
        public void run() {
            debounceInstance.checkExecute();
        }
    }

}