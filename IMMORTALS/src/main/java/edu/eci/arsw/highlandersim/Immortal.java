package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean pause = false;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {
        while (health > 0) {
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
            //avoid self-fight
            synchronized (immortalsPopulation) {
                int nextFighterIndex = r.nextInt(immortalsPopulation.size());
                if (immortalsPopulation.get(nextFighterIndex).equals(this)) {
                    nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                }

                im = immortalsPopulation.get(nextFighterIndex);
            }
            this.fight(im);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (immortalsPopulation) {
            immortalsPopulation.remove(this);
        }
    }

    public void fight(Immortal i2) {
        synchronized (i2) {
            if (i2.getHealth() > 0 ) {
                synchronized (immortalsPopulation){
                    if (!(immortalsPopulation.size() == 2 && i2.getHealth() - defaultDamageValue == 0)){
                        i2.changeHealth(i2.getHealth() - defaultDamageValue);
                        this.health += defaultDamageValue;
                        updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                    }
                }
            }else {
                updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
            }
        }
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
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

}
