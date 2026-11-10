package com.g2l.speedg2l.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.g2l.speedg2l.componentes.Boton;
import com.g2l.speedg2l.componentes.PanelBotones;
import com.g2l.speedg2l.componentes.Texto;
import com.g2l.speedg2l.utilidades.Recursos;

public class PantallaInstrucciones extends Pantalla{
    private Texto titulo;
    private Texto texto;
    private PanelBotones panelBotones;
    private Stage stage;

    protected PantallaInstrucciones(Game juego, SpriteBatch b) {
        super(juego, b);
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
                        cambiarPantalla(new PantallaMenu(juego, batch));
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
        titulo = new Texto(Recursos.FUENTE_MENU, 60, Color.WHITE, batch);
        titulo.setTexto("Instrucciones");
        titulo.centrarArriba(50);

        texto = new Texto(Recursos.FUENTE_MENU, 25, Color.WHITE, batch);
        texto.setTexto("""
            OBJETIVO
            ¡Competí contra otro jugador en una carrera de plataformas!\s
            Superá los obstáculos y llegá primero a la meta para ganar la partida.

            CONTROLES
            A: Moverse hacia la izquierda.
            D: Moverse hacia la derecha.
            W: Saltar para superar obstáculos y plataformas.

            ¡CUIDADO CON LA LAVA!
            Evitá pisar la lava. Si entrás en contacto con ella, tu personaje sufrirá una ralentización.

            CONSEJO
            Aprovechá los saltos, controlá tus movimientos y tratá de mantener la ventaja durante toda la carrera.""");
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
        limpiarPantalla();
        configViewport.aplicarViewport(batch);
        batch.begin();

        titulo.dibujar();

        texto.dibujar();

        batch.end();
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
        texto.cerrar();
    }
}
