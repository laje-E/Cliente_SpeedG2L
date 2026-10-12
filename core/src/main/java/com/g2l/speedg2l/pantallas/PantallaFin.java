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
import com.g2l.speedg2l.utilidades.Render;

public class PantallaFin extends Pantalla {

    private Stage stage;
    private Boton btnSalir;
    private PanelBotones panelBotones;
    private SpriteBatch b;
    private EfectoSonido efectoMeta;
    private Texto cronometro;
    private float tiempo;

    private PosicionFinal[] posiciones;
    private Texto textoResultados;
    private HiloCliente hiloCliente;

    public PantallaFin(Game juego, PosicionFinal[] posiciones, HiloCliente hiloCliente) {
        super(juego);
        this.posiciones = posiciones;
        this.hiloCliente = hiloCliente;
    }

    @Override
    public void show() {

        posiciones = hiloCliente.getPosicionesFinales();

        textoResultados = new Texto(
            Recursos.FUENTE_MENU,
            35,
            Color.GREEN
        );

        b = Render.batch;

        stage = new Stage(configViewport.getViewport());

        efectoMeta = new EfectoSonido(Recursos.EFECTO_META);
        if (Config.isSonidoSilenciado()) {
            efectoMeta.setVolumen(0.0f);
        } else {
            efectoMeta.setVolumen(Config.getVolumenMaster());
            efectoMeta.reproducir();
        }

//
//        cronometro = new Texto(Recursos.FUENTE_MENU, 60, Color.MAROON);
//
//        int minutos = (int) tiempo / 60;
//        int segundos = (int) tiempo % 60;
//        int centesimas = (int) (tiempo * 100) % 100;
//
//        cronometro.setTexto("Tiempo final:\n" + "min: " + minutos + " seg: " + segundos + "," + centesimas);
//
//        cronometro.centrarArriba(150);

        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

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
//                texto.append(i + 1)
//                    .append("° puesto: En carrera\n");
            } else {
                texto += (resultado.getNumeroPosicion()) + "° puesto: Jugador " + (resultado.getIdJugador()) + " - " + (String.format("%.2f s", resultado.getTiempo())) + "\n";
//                texto.append(resultado.getNumeroPosicion())
//                    .append("° puesto: Jugador ")
//                    .append(resultado.getIdJugador())
//                    .append(" - ")
//                    .append(String.format("%.2f s", resultado.getTiempo()))
//                    .append("\n");
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
    public void render(float delta) {
        Render.limpiarPantalla();

        actualizarResultados(posiciones);

        configViewport.aplicarViewport(b);
        b.begin();

//        cronometro.dibujar();
        textoResultados.dibujar();

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
        stage.dispose();
        efectoMeta.cerrar();
    }

    private void agregarAlStage() {
        stage.addActor(panelBotones.getContenedor());
    }

}
