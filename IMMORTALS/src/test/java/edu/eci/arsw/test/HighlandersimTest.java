package edu.eci.arsw.test;

import jdk.nashorn.internal.ir.CatchNode;
import org.junit.Test;
import org.junit.Before;
import edu.eci.arsw.highlandersim.*;
import java.util.List;
import static org.junit.Assert.*;

public class HighlandersimTest {

    private ControlFrame game;

    public HighlandersimTest(){}

    @Test
    public void noDeberiaLanzarExcepcionAlIniciar() {
        try{
            game = new ControlFrame();
            game.start();
            assertTrue(true);
        }catch (Exception e){
            fail("Lanzó excepción");
        }
    }

    @Test
    public void noDeberiaLanzarExcepcionAlPausaryReanudar() {
        try{
            game = new ControlFrame();
            game.start();
            game.pause();
            game.resumee();
            game.pause();
            game.resumee();
            Thread.sleep(500);
            game.pause();
            game.resumee();
        }catch (Exception e){
            fail("Lanzó excepción");
        }
    }

    @Test
    public void deberiaTenerElMismoNumeroDePuntosDeVidaTotalSiempre() {
        try {
            game = new ControlFrame();
            game.start();
            game.pause();
            int sum = 0;
            for(Immortal im: game.getImmortals()){
                sum += im.getHealth();
            }
            assertEquals(game.getNumOfImmortals() * game.getDefaultImmortalHealth(), sum);
            game.resumee();
            Thread.sleep(500);
            game.pause();
            sum = 0;
            for(Immortal im: game.getImmortals()){
                sum += im.getHealth();
            }
            assertEquals(game.getNumOfImmortals() * game.getDefaultImmortalHealth(), sum);
        }catch (Exception e){
            fail("Lanzó excepción");
        }
    }

    @Test
    public void noDeberiaLanzarExcepcionAlPararElJuego() {
        try{
            ControlFrame game = new ControlFrame();
            game.start();
            game.stopp();
            Thread.sleep(500);
            game.start();
            game.stopp();
            assertTrue(true);
        }catch (Exception e){
            fail("Lanzo excepción");
        }
    }

    @Test
    public  void deberiaCrearNuevosJugadoresAlPararEIniciarNuevamenteElJuego(){
        try{
            ControlFrame game = new ControlFrame();
            game.start();
            game.stopp();
            Immortal im = game.getImmortals().get(0);
            game.start();
            game.stopp();
            assertTrue(!game.getImmortals().contains(im));
        }catch (Exception e){
            fail("Lanzo excepción");
        }
    }

    @Test
    public void deberiaEliminarLosJugadoresMuertosDeLaLista() {
        try {
            ControlFrame game = new ControlFrame();
            game.start();
            game.pause();
            int im = game.getImmortals().size();
            game.resumee();
            Thread.sleep(1000);
            assertTrue(game.getImmortals().size() < im);
        }catch(Exception e){
            fail("Lanzó excepción");
        }
    }
}
