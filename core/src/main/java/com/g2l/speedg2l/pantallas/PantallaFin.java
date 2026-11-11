package com.g2l.speedg2l.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.g2l.speedg2l.cliente.HiloCliente;
import com.g2l.speedg2l.componentes.Boton;
import com.g2l.speedg2l.componentes.PanelBotones;
import com.g2l.speedg2l.componentes.Texto;
import com.g2l.speedg2l.cliente.PosicionFinal;
import com.g2l.speedg2l.sonidos.EfectoSonido;
import com.g2l.speedg2l.utilidades.Config;
import com.g2l.speedg2l.utilidades.Recursos;

public class PantallaFin extends Pantalla {

    private Stage stage;
    private Skin skin;
    private Boton btnSalir;
    private PanelBotones panelBotones;
    private EfectoSonido efectoMeta;
    private Texto cronometro;
    private float tiempo;

    private PosicionFinal[] posiciones;
    private Texto textoResultados;
    private HiloCliente hiloCliente;

    public PantallaFin(Game juego, PosicionFinal[] posiciones, HiloCliente hiloCliente, SpriteBatch b) {
        super(juego, b);
        this.posiciones = posiciones;
        this.hiloCliente = hiloCliente;
    }

    @Override
    public void show() {

        posiciones = hiloCliente.getPosicionesFinales();

        textoResultados = new Texto(
            Recursos.FUENTE_MENU,
            35,
            Color.GREEN,
            batch
        );

        stage = new Stage(configViewport.getViewport());

        efectoMeta = new EfectoSonido(Recursos.EFECTO_META);
        if (Config.isSonidoSilenciado()) {
            efectoMeta.setVolumen(0.0f);
        } else {
            efectoMeta.setVolumen(Config.getVolumenMaster());
            efectoMeta.reproducir();
        }

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        crearPanelBotones(skin);
        agregarAlStage();
        configurarInput(stage);
    }

    private void actualizarResultados(PosicionFinal[] nuevosResultados) {
        this.posiciones = nuevosResultados;

        String texto = "RESULTADOS\n\n";

        for (int i = 0; i < posiciones.length; i++) {
            PosicionFinal resultado = posiciones[i];

            if (resultado == null) {
                texto += (i+1) + "° puesto: En carrera\n";
            } else {
                texto += (resultado.getNumeroPosicion()) + "° puesto: Jugador " + (resultado.getIdJugador()) + " - " + (String.format("%.2f s", resultado.getTiempo())) + "\n";
            }
        }

        textoResultados.setTexto(texto);
        textoResultados.centrar();
    }

    private void crearPanelBotones(Skin skin) {
        panelBotones = new PanelBotones();

        final int PADDING = 10;
        final int ANCHO_BOTONES = 200;
        final int ALTURA_BOTONES = 60;

        panelBotones.agregarBoton(
            new Boton(
                "Menu",
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
    public void render(float delta) {
        limpiarPantalla();

        posiciones = hiloCliente.getPosicionesFinales();

        actualizarResultados(posiciones);

        configViewport.aplicarViewport(batch);
        batch.begin();
        textoResultados.dibujar();

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
        if (hiloCliente != null) {
            hiloCliente.finalizarHilo();
        }
    }


    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }

        if (efectoMeta != null) {
            efectoMeta.cerrar();
        }

        if (textoResultados != null) {
            textoResultados.cerrar();
        }

        if (skin != null) {
            skin.dispose();
        }
    }

    private void agregarAlStage() {
        stage.addActor(panelBotones.getContenedor());
    }

}
