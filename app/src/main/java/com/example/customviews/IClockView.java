package com.example.customviews;

/**
 * Created by fanlat on 13/08/17.
 */

public interface IClockView {
    /**
     * Inicia la cuenta regresiva
     */
    void start();

    /**
     * Regresa el reloj a 24 segundos
     */
    void reset();

    /**
     * Detiene el reloj
     */
    void stop();
}
