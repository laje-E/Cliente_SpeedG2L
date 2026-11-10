
package com.g2l.speedg2l.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.g2l.speedg2l.componentes.Imagen;
import com.g2l.speedg2l.animaciones.Fade;
import com.g2l.speedg2l.utilidades.Recursos;
import com.g2l.speedg2l.utilidades.Render;

public class PantallaCarga extends Pantalla {

    private Imagen imagenFondo;
    private Fade fade;

    public PantallaCarga(Game juego, SpriteBatch b) {
        super(juego, b);
    }

    @Override
    public void show() {
        imagenFondo = new Imagen(Recursos.FONDO_CARGA, batch);
        imagenFondo.setSize(405, 512);
        imagenFondo.centrar();
        imagenFondo.setTransparencia(0);

        fade = new Fade(5.0f, imagenFondo);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        fade.iniciar();

        configViewport.aplicarViewport(batch);

        batch.begin();
        imagenFondo.dibujar();
        batch.end();

        if (fade.isTerminado()) {
            cambiarPantalla(new PantallaMenu(juego, batch));
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (imagenFondo != null) {
            imagenFondo.cerrar();
        }
    }
}
