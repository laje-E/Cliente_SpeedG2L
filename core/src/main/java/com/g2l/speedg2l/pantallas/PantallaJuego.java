package com.g2l.speedg2l.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.g2l.speedg2l.cliente.HiloCliente;
import com.g2l.speedg2l.cliente.PosicionFinal;
import com.g2l.speedg2l.cliente.ProtocolosRed;
import com.g2l.speedg2l.componentes.Boton;
import com.g2l.speedg2l.componentes.PanelBotones;
import com.g2l.speedg2l.componentes.Texto;
import com.g2l.speedg2l.componentes.interfaz.Hud;
import com.g2l.speedg2l.entidades.*;
import com.g2l.speedg2l.mundo.Camara;
import com.g2l.speedg2l.mundo.Mapa;
import com.g2l.speedg2l.sonidos.Musica;
import com.g2l.speedg2l.utilidades.Config;
import com.g2l.speedg2l.utilidades.Entradas;
import com.g2l.speedg2l.utilidades.Recursos;
import com.g2l.speedg2l.utilidades.Render;

public class PantallaJuego extends Pantalla {

    private Stage stage;

    private Jugador jugador;
    private Jugador jugador2;
    private Jugador jugadorLocal;
    private Jugador jugadorContrario;
    private boolean jugadoresAsignados = false;

    private SpriteBatch b;
    private Musica musicaJuego;
    private Hud hud;
    private Mapa mapa;
    private Camara camara;
    private Entradas entradas;

    private boolean pausado = false;
    private ShapeRenderer pantallaPausa = new ShapeRenderer();
    private Texto textoPausa = new Texto(Recursos.FUENTE_MENU, 60, Color.RED);

    private Texto textoEspera = new Texto(Recursos.FUENTE_MENU, 60, Color.WHITE);

    private HiloCliente hiloCliente;

    private PosicionFinal[] posicionesFinales = new PosicionFinal[2];

    private PanelBotones panelBotones;

    private long ultimoMensajeEnviado = 0;
    private Texto textoDesconexion = new Texto(Recursos.FUENTE_MENU, 48, Color.RED);

    private long inicioIntentoConexion = 0;
    private final long TIEMPO_ESPERA_SERVIDOR = 5000;
    private final long INTERVALO_REINTENTO = 1000;
    private boolean servidorNoEncontrado = false;
    private Texto textoServidorNoEncontrado = new Texto(Recursos.FUENTE_MENU, 48, Color.RED);

    private boolean conservarHilo = false;

    private Texto textojugadorDesconectado = new Texto(Recursos.FUENTE_MENU, 48, Color.GREEN);

    protected PantallaJuego(Game juego) {
        super(juego);
    }

    @Override
    public void show() {
        b = Render.batch;

        crearYaplicarMusica();

        camara = new Camara(configViewport);
        mapa = new Mapa(Recursos.NIVEL_1);
        entradas = new Entradas();

        jugador = Jugadores.JUGADOR_1.crearJugador();
        jugador2 = Jugadores.JUGADOR_2.crearJugador();

        hud = new Hud();

        textoPausa.setTexto("PAUSADO");
        textoEspera.setTexto("Esperando jugador");
        textoEspera.centrar();
        textoEspera.setY(80);

        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        stage = new Stage(
            new FitViewport(
                Config.getAnchoJuego(),
                Config.getAltoJuego()
            )
        );

        crearPanelBotones(skin);
        panelBotones.centrar();
        agregarAlStage();

        Gdx.input.setInputProcessor(stage);

        textoDesconexion.setTexto("SERVIDOR DESCONECTADO");
        textoDesconexion.centrar();
        textoDesconexion.setY(80);

        textoServidorNoEncontrado.setTexto(
            "NO HAY NINGUN SERVIDOR CONECTADO"
        );
        textoServidorNoEncontrado.centrar();
        textoServidorNoEncontrado.setY(80);

        textojugadorDesconectado.setTexto("SE DESCONECTO SU RIVAL, USTED ES EL GANADOR");
        textojugadorDesconectado.centrar();
        textojugadorDesconectado.setY(80);

        hiloCliente = new HiloCliente();
        hiloCliente.start();

        inicioIntentoConexion = System.currentTimeMillis();
        ultimoMensajeEnviado = inicioIntentoConexion;
        hiloCliente.enviarMensaje(ProtocolosRed.CONEXION);
    }

    private void agregarAlStage() {
        stage.addActor(panelBotones.getContenedor());
    }

    private void crearYaplicarMusica() {

        if (Render.musicaJuego == null) {
            Render.musicaJuego = new Musica(Recursos.MUSICA_JUEGO);
            Render.musicaJuego.repetir(true);
        }
        if (Config.isSonidoSilenciado()) {
            Render.musicaJuego.setVolumen(0.0f);
        } else {
            Render.musicaJuego.setVolumen(Config.getVolumenMaster());
        }

        musicaJuego = Render.musicaJuego;

        if (!musicaJuego.estaReproduciendo()) {
            musicaJuego.reproducir();
        }
    }

    @Override
    public void render(float delta) {

        long ahora = System.currentTimeMillis();
        // 1. Todavía no recibimos una respuesta del servidor.
        if (!hiloCliente.isServidorConfirmado()
            && !servidorNoEncontrado) {

            // Reintentar la conexión cada segundo.
            if (ahora - ultimoMensajeEnviado >= INTERVALO_REINTENTO) {
                hiloCliente.enviarMensaje(ProtocolosRed.CONEXION);
                ultimoMensajeEnviado = ahora;
            }

            // Comprobar si pasaron los 5 segundos.
            if (ahora - inicioIntentoConexion >= TIEMPO_ESPERA_SERVIDOR
                && !hiloCliente.isServidorConfirmado()) {

                servidorNoEncontrado = true;

                System.out.println(
                    "No se encontró ningún servidor después de 5 segundos."
                );
            }
        }

        // 2. Ya hay conexión: enviar mensajes de comprobación.
        if (hiloCliente.isServidorConfirmado()
            && !hiloCliente.isServidorDesconectado()
            && ahora - ultimoMensajeEnviado >= 1000) {

            hiloCliente.enviarMensaje(ProtocolosRed.ESTAS_AHI);
            ultimoMensajeEnviado = ahora;
        }

        // 3. Mostrar el mensaje si no se encontró el servidor.
        if (servidorNoEncontrado) {
            Render.limpiarPantalla();

            panelBotones.getContenedor().setVisible(true);

            stage.getViewport().apply();
            b.setProjectionMatrix(stage.getCamera().combined);

            b.begin();
            textoServidorNoEncontrado.dibujar();
            b.end();

            stage.act(delta);
            stage.draw();

            return;
        }

        // 4. Comportamiento de desconexión.
        if (hiloCliente.isServidorDesconectado()) {
            Render.limpiarPantalla();

            panelBotones.getContenedor().setVisible(true);

            stage.getViewport().apply();
            b.setProjectionMatrix(stage.getCamera().combined);

            b.begin();
            textoDesconexion.dibujar();
            b.end();

            stage.act(delta);
            stage.draw();

            return;
        }

        if(hiloCliente.isSeDesconectoUnJugador()){
            Render.limpiarPantalla();

            panelBotones.getContenedor().setVisible(true);

            stage.getViewport().apply();
            b.setProjectionMatrix(stage.getCamera().combined);

            b.begin();
            textojugadorDesconectado.dibujar();
            b.end();

            stage.act(delta);
            stage.draw();

            return;
        }

        posicionesFinales = hiloCliente.getPosicionesFinales();

//        if (hiloCliente.isServidorDesconectado()) {
//            Render.limpiarPantalla();
//
//            panelBotones.getContenedor().setVisible(true);
//
//            stage.getViewport().apply();
//            b.setProjectionMatrix(stage.getCamera().combined);
//
//            b.begin();
//            textoDesconexion.dibujar();
//            b.end();
//
//            stage.act(delta);
//            stage.draw();
//
//            return;
//        }

        if(!hiloCliente.isEmpieza()){

            panelBotones.getContenedor().setVisible(!hiloCliente.isEmpieza());

            Render.limpiarPantalla();

            stage.getViewport().apply();
            b.setProjectionMatrix(stage.getCamera().combined);

            b.begin();

            textoEspera.dibujar();

            b.end();

            stage.act(delta);
            stage.draw();

        } else {
            if (!jugadoresAsignados) {
                asignarJugadores();
            } else {
                Render.limpiarPantalla();

                panelBotones.getContenedor().setVisible(pausado);

                asignarPosicionJugador(jugadorLocal, hiloCliente.getPosicionJugadores(jugadorLocal.getNumeroJugador()));
                asignarPosicionJugador(jugadorContrario, hiloCliente.getPosicionJugadores(jugadorContrario.getNumeroJugador()));

                camara.seguirJugador(jugadorLocal);

                mapa.dibujar(camara.getCamara());

                b.setProjectionMatrix(camara.getCamara().combined);

                b.begin();

                    if (entradas.escape()) {
                        pausado = !pausado;
                    }

                    if (!musicaJuego.estaReproduciendo()) {
                        musicaJuego.reproducir();
                    }

                    jugadorLocal.moverJugador(entradas,hiloCliente);

                    jugadorLocal.animar(delta);
                    jugadorContrario.animar(delta);

                    hud.actualizar();

                    if (hiloCliente.isEsteClienteTermino()) {
                        conservarHilo = true;
                        cambiarPantalla(new PantallaFin(juego, posicionesFinales, hiloCliente));
                        musicaJuego.cerrar();
                    }

                    jugadorLocal.dibujar();
                    jugadorContrario.dibujar();

                b.end();

                b.setProjectionMatrix(stage.getCamera().combined);

                if (pausado) {
                    b.begin();
                        textoPausa.centrarArriba(200);
                        textoPausa.dibujar();
                    b.end();
                    dibujarPausa();
                }

                b.begin();
                    hud.dibujar();
                b.end();

                stage.act(delta);
                stage.draw();
            }
        }

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    private void dibujarPausa() {

        Gdx.gl.glEnable(GL20.GL_BLEND);

        pantallaPausa.setProjectionMatrix(stage.getCamera().combined);

        pantallaPausa.begin(ShapeRenderer.ShapeType.Filled);

        pantallaPausa.setColor(0, 0, 0, 0.7f);

        pantallaPausa.rect(
            0,
            0,
            Config.getAnchoJuego(),
            Config.getAltoJuego()
        );

        pantallaPausa.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void crearPanelBotones(Skin skin) {
        panelBotones = new PanelBotones();

        final int PADDING = 10;
        final int ANCHO_BOTONES = 200;
        final int ALTURA_BOTONES = 60;

        panelBotones.agregarBoton(
            new Boton(
                "Volver al menu",
                skin,
                new ClickListener() {
                    @Override
                    public void clicked(
                        com.badlogic.gdx.scenes.scene2d.InputEvent event,
                        float x,
                        float y) {
                        hiloCliente.enviarMensaje(
                            ProtocolosRed.SE_SALIO_DEL_JUEGO +
                            ProtocolosRed.SEPARADOR +
                            hiloCliente.getNumeroJugador()
                        );
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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        if (hiloCliente != null && !conservarHilo) {
            hiloCliente.finalizarHilo();
        }
    }

    @Override
    public void dispose() {
//        stage.dispose();
//        b.dispose();
//        musicaJuego.cerrar();
//        Render.musicaJuego = null;
//        jugador.cerrar();
    }

    public void asignarJugadores(){
        int numeroJugador = hiloCliente.getNumeroJugador();

        if(numeroJugador == 1){
            jugadorLocal = jugador;
            jugadorContrario = jugador2;

            jugadoresAsignados = true;
        }else if(numeroJugador == 2) {
            jugadorLocal = jugador2;
            jugadorContrario = jugador;

            jugadoresAsignados = true;
        }

        jugadorLocal.asignarLocal();
    }

    public void asignarPosicionJugador(Jugador jugador, float[] posiciones) {
        if (posiciones != null) {
            float x = posiciones[0];
            float y = posiciones[1];

            if(!jugador.isLocal()){
                if(x > jugador.getPosicionX()){
                    jugador.setMoviendose(true);
                    jugador.setMirandoDerecha(true);
                }
                else if(x < jugador.getPosicionX()){
                    jugador.setMoviendose(true);
                    jugador.setMirandoDerecha(false);
                }
                else{
                    jugador.setMoviendose(false);
                }
            }

            jugador.asignarPosicion(x, y);
        }
    }
}
