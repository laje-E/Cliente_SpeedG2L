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

    private Musica musicaJuego;
    private Hud hud;
    private Mapa mapa;
    private Camara camara;
    private Entradas entradas;

    private boolean pausado = false;
    private ShapeRenderer pantallaPausa = new ShapeRenderer();
    private Texto textoPausa = new Texto(Recursos.FUENTE_MENU, 60, Color.RED, batch);

    private Texto textoEspera = new Texto(Recursos.FUENTE_MENU, 60, Color.WHITE, batch);

    private HiloCliente hiloCliente;

    private PanelBotones panelBotones;

    private long ultimoMensajeEnviado = 0;
    private Texto textoDesconexion = new Texto(Recursos.FUENTE_MENU, 48, Color.RED, batch);

    private long inicioIntentoConexion = 0;
    private boolean servidorNoEncontrado = false;
    private Texto textoServidorNoEncontrado = new Texto(Recursos.FUENTE_MENU, 48, Color.RED, batch);

    private boolean conservarHilo = false;

    private Texto textojugadorDesconectado = new Texto(Recursos.FUENTE_MENU, 48, Color.GREEN, batch);

    private Texto textoCuentaRegresiva = new Texto(Recursos.FUENTE_MENU, 200, Color.YELLOW, batch);
    private int ultimaCuentaMostrada = -1;

    private Texto textoLimiteClientes = new Texto(Recursos.FUENTE_MENU, 48, Color.RED, batch);

    protected PantallaJuego(Game juego, SpriteBatch b) {
        super(juego, b);
    }

    @Override
    public void show() {
        crearYaplicarMusica();

        camara = new Camara(configViewport);
        mapa = new Mapa(Recursos.NIVEL_1);
        entradas = new Entradas();

        jugador = Jugadores.JUGADOR_1.crearJugador(batch);
        jugador2 = Jugadores.JUGADOR_2.crearJugador(batch);

        hud = new Hud(batch);

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

        textoLimiteClientes.setTexto("SERVIDOR COMPLETO");
        textoLimiteClientes.centrar();
        textoLimiteClientes.setY(80);

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

        musicaJuego = new Musica(Recursos.MUSICA_JUEGO);
        musicaJuego.repetir(true);

        if (Config.isSonidoSilenciado()) {
            musicaJuego.setVolumen(0.0f);
        } else {
            musicaJuego.setVolumen(Config.getVolumenMaster());
        }

        musicaJuego.reproducir();
    }

    @Override
    public void render(float delta) {

        if (!hiloCliente.isHayEspacio()) {

            Render.limpiarPantalla();

            panelBotones.getContenedor().setVisible(true);

            stage.getViewport().apply();
            batch.setProjectionMatrix(stage.getCamera().combined);

            batch.begin();
            textoLimiteClientes.dibujar();
            batch.end();

            stage.act(delta);
            stage.draw();

            return;
        }

        long ahora = System.currentTimeMillis();
        // 1. Todavía no recibimos una respuesta del servidor.
        if (!hiloCliente.isServidorConfirmado()
            && !servidorNoEncontrado) {

            // Reintentar la conexión cada segundo.
            long INTERVALO_REINTENTO = 1000;
            if (ahora - ultimoMensajeEnviado >= INTERVALO_REINTENTO) {
                hiloCliente.enviarMensaje(ProtocolosRed.CONEXION);
                ultimoMensajeEnviado = ahora;
            }

            // Comprobar si pasaron los 5 segundos.
            long TIEMPO_ESPERA_SERVIDOR = 5000;
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
            batch.setProjectionMatrix(stage.getCamera().combined);

            batch.begin();
            textoServidorNoEncontrado.dibujar();
            batch.end();

            stage.act(delta);
            stage.draw();

            return;
        }

        // 4. Comportamiento de desconexión.
        if (hiloCliente.isServidorDesconectado()) {
            Render.limpiarPantalla();

            panelBotones.getContenedor().setVisible(true);

            stage.getViewport().apply();
            batch.setProjectionMatrix(stage.getCamera().combined);

            batch.begin();
            textoDesconexion.dibujar();
            batch.end();

            stage.act(delta);
            stage.draw();

            return;
        }

        if(hiloCliente.isSeDesconectoUnJugador()){
            Render.limpiarPantalla();

            panelBotones.getContenedor().setVisible(true);

            stage.getViewport().apply();
            batch.setProjectionMatrix(stage.getCamera().combined);

            batch.begin();
            textojugadorDesconectado.dibujar();
            batch.end();

            stage.act(delta);
            stage.draw();

            return;
        }

        PosicionFinal[] posicionesFinales = hiloCliente.getPosicionesFinales();

        if(!hiloCliente.isEmpieza()){

            int cuentaRegresiva = hiloCliente.getCuentaRegresiva();

            panelBotones.getContenedor().setVisible(!hiloCliente.isEmpieza() && cuentaRegresiva <= 0);

            Render.limpiarPantalla();

            stage.getViewport().apply();
            batch.setProjectionMatrix(stage.getCamera().combined);

            batch.begin();

            if (cuentaRegresiva > 0) {
                if (cuentaRegresiva != ultimaCuentaMostrada) {
                    textoCuentaRegresiva.setTexto(String.valueOf(cuentaRegresiva));
                    textoCuentaRegresiva.centrar();
                    ultimaCuentaMostrada = cuentaRegresiva;
                }
                textoCuentaRegresiva.dibujar();
            } else {
                textoEspera.dibujar();
            }

            batch.end();

            stage.act(delta);
            stage.draw();

        } else {
            if (!jugadoresAsignados) {
                asignarJugadores( );
            } else {
                Render.limpiarPantalla();

                panelBotones.getContenedor().setVisible(pausado);

                asignarPosicionJugador(jugadorLocal, hiloCliente.getPosicionJugadores(jugadorLocal.getNumeroJugador()));
                asignarPosicionJugador(jugadorContrario, hiloCliente.getPosicionJugadores(jugadorContrario.getNumeroJugador()));

                camara.seguirJugador(jugadorLocal);

                mapa.dibujar(camara.getCamara());

                batch.setProjectionMatrix(camara.getCamara().combined);

                batch.begin();

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
                        cambiarPantalla(new PantallaFin(juego, posicionesFinales, hiloCliente, batch));
                    }

                    jugadorLocal.dibujar();
                    jugadorContrario.dibujar();

                batch.end();

                batch.setProjectionMatrix(stage.getCamera().combined);

                if (pausado) {
                    batch.begin();
                        textoPausa.centrarArriba(200);
                        textoPausa.dibujar();
                    batch.end();
                    dibujarPausa();
                }

                batch.begin();
                    hud.dibujar();
                batch.end();

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
        if (musicaJuego != null) {
            musicaJuego.cerrar();
        }
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
