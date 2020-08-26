package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private AtomicInteger health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean pause = false;

    private boolean stop = false;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(health);
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {
        while ( health.get() > 0 && !stop) {
            synchronized (this) {
                while (pause) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            Immortal im;
            synchronized (immortalsPopulation) {
                int myIndex = immortalsPopulation.indexOf(this);
                int nextFighterIndex = r.nextInt(immortalsPopulation.size());
                if (myIndex == nextFighterIndex) {
                    nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                }
                im = immortalsPopulation.get(nextFighterIndex);
                this.fight(im);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }if (health.get() == 0){
            immortalsPopulation.remove(this);
        }
    }

    public void fight(Immortal i2) {
        String report = "";
        synchronized (i2) {
            if (i2.getHealth() > 0 ) {
                if (!(immortalsPopulation.size() == 2 && i2.getHealth() - defaultDamageValue == 0)){
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health.addAndGet(defaultDamageValue);
                    report = "Fight: " + this + " vs " + i2 + "\n";
                }
            }else {
                report = this + " says:" + i2 + " is already dead!\n";
            }
        }
        if (!report.equals("")){
            synchronized (updateCallback){
                updateCallback.processReport(report);
            }
        }
    }

    public void changeHealth(int v) {
        health.getAndSet(v);
    }

    public int getHealth() {
        return health.get();
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public void pause(){
        pause = true;
    }

    public void resumee(){
        pause = false;
        synchronized (this) {
            notifyAll();
        }
    }

    public void stopp(){
        stop = true;
    }

}
