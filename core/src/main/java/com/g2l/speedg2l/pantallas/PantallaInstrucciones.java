package com.g2l.speedg2l.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.g2l.speedg2l.componentes.Boton;
import com.g2l.speedg2l.componentes.Imagen;
import com.g2l.speedg2l.componentes.PanelBotones;
import com.g2l.speedg2l.componentes.Texto;
import com.g2l.speedg2l.utilidades.Config;
import com.g2l.speedg2l.utilidades.Recursos;
import com.g2l.speedg2l.utilidades.Render;

import java.awt.*;

import static com.badlogic.gdx.Gdx.app;

public class PantallaInstrucciones extends Pantalla{
    private Texto titulo;
    private Texto texto;
    private SpriteBatch b;
    private PanelBotones panelBotones;
    private Stage stage;

    protected PantallaInstrucciones(Game juego) {
        super(juego);
    }


    private void crearPanelBotones(Skin skin) {
        panelBotones = new PanelBotones();

        final int PADDING = 20;
        final int ANCHO_BOTONES = 200;
        final int ALTURA_BOTONES = 60;

        panelBotones.agregarBoton(
            new Boton(
                "MENU",
                skin,
                new ClickListener() {
                    @Override
                    public void clicked(
                        com.badlogic.gdx.scenes.scene2d.InputEvent event,
                        float x,
                        float y) {
                        cambiarPantalla(new PantallaMenu(juego));
                    }
                },
                ANCHO_BOTONES,
                ALTURA_BOTONES
            ),
            PADDING
        );

    }

    @Override
    public void show() {
        b = Render.batch;
        titulo = new Texto(Recursos.FUENTE_MENU, 60, Color.WHITE);
        titulo.setTexto("Instrucciones");
        titulo.centrarArriba(50);

        texto = new Texto(Recursos.FUENTE_MENU, 25, Color.WHITE);
        texto.setTexto("OBJETIVO" +
            "\n" +
            "¡Competí contra otro jugador en una carrera de plataformas! \nSuperá los obstáculos y llegá primero a la meta para ganar la partida.\n" +
            "\n" +
            "CONTROLES" +
            "\n" +
            "A: Moverse hacia la izquierda." +
            "\n" +
            "D: Moverse hacia la derecha." +
            "\n" +
            "W: Saltar para superar obstáculos y plataformas.\n" +
            "\n" +
            "¡CUIDADO CON LA LAVA!" +
            "\n" +
            "Evitá pisar la lava. Si entrás en contacto con ella, tu personaje sufrirá una ralentización.\n" +
            "\n" +
            "CONSEJO" +
            "\n" +
            "Aprovechá los saltos, controlá tus movimientos y tratá de mantener la ventaja durante toda la carrera.");
        texto.centrarArriba(150);
        texto.setX(20);


        stage = new Stage(configViewport.getViewport());

        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        crearPanelBotones(skin);
        panelBotones.getContenedor().bottom();

        agregarAlStage();

        configurarInput(stage);
    }

    private void agregarAlStage() {
        stage.addActor(panelBotones.getContenedor());
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();
        configViewport.aplicarViewport(b);
        b.begin();

        titulo.dibujar();

        texto.dibujar();

        b.end();
        stage.act(delta);
        stage.draw();
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
//        texto.cerrar();
//        b.dispose();
    }
}
