package com.g2l.speedg2l.pantallas;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.g2l.speedg2l.cliente.HiloCliente;
import com.g2l.speedg2l.cliente.PosicionFinal;
import com.g2l.speedg2l.componentes.Boton;
import com.g2l.speedg2l.componentes.Imagen;
import com.g2l.speedg2l.componentes.PanelBotones;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.g2l.speedg2l.componentes.Imagen;
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

import java.util.ArrayList;


public class PantallaJuego extends Pantalla {

    private Stage stage;

    private Jugador jugador;
    private Jugador jugador2;
    private SpriteBatch b;
    private Musica musicaJuego;
    private Hud hud;

    private Pincho pincho;
    private Imagen imgPincho;

    private ArrayList<Entidad> listaDeEntidades;
    private ArrayList<Obstaculo> listaDeObstaculos;

    private Mapa mapa;
    private Camara camara;
    private Entradas entradas;

    private boolean pausado = false;
    private ShapeRenderer pantallaPausa = new ShapeRenderer();
    private Texto textoPausa = new Texto(Recursos.FUENTE_MENU, 60, Color.RED);

    private Texto textoEspera = new Texto(Recursos.FUENTE_MENU, 60, Color.WHITE);

    private Meta meta;

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

        jugador = new Jugador(70.0f, 70.0f, 0.0f, 33.0f, Recursos.JUGADOR_1);
        jugador2 = new Jugador(70.0f, 70.0f, 25.0f, 33.0f, Recursos.JUGADOR_2);

        hud = new Hud();

        meta = new Meta(50.0f, 1000.0f, 8000.0f, 100.0f);

        listaDeEntidades = new ArrayList<>();
        listaDeObstaculos = new ArrayList<>();

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

//        cargarColisionesDesdeMapa();

        textoDesconexion.setTexto("SERVIDOR DESCONECTADO");
        textoDesconexion.centrar();
        textoDesconexion.setY(80);

        textoServidorNoEncontrado.setTexto(
            "NO HAY NINGUN SERVIDOR CONECTADO"
        );
        textoServidorNoEncontrado.centrar();
        textoServidorNoEncontrado.setY(80);


        hiloCliente = new HiloCliente();
        hiloCliente.start();

        inicioIntentoConexion = System.currentTimeMillis();
        ultimoMensajeEnviado = inicioIntentoConexion;
        hiloCliente.enviarMensaje("Conexion");


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
                hiloCliente.enviarMensaje("Conexion");
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

            hiloCliente.enviarMensaje("Hola");
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



        if (ahora - ultimoMensajeEnviado >= 1000) {
            hiloCliente.enviarMensaje("Hola");
            ultimoMensajeEnviado = ahora;
        }

        posicionesFinales = hiloCliente.getPosicionesFinales();

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

        else if(!hiloCliente.isEmpieza()){

            panelBotones.getContenedor().setVisible(!hiloCliente.isEmpieza());

            Render.limpiarPantalla();

            stage.getViewport().apply();
            b.setProjectionMatrix(stage.getCamera().combined);

            b.begin();

            textoEspera.dibujar();

            b.end();

            stage.act(delta);
            stage.draw();

        }


        else if (hiloCliente.isEmpieza()){

            delta = Gdx.graphics.getDeltaTime();

            Render.limpiarPantalla();

            panelBotones.getContenedor().setVisible(pausado);

            if(hiloCliente.getNumeroJugador() == 1) {
                camara.seguirJugador(jugador);
            }
            else if (hiloCliente.getNumeroJugador() == 2){
                camara.seguirJugador(jugador2);
            }
            mapa.dibujar(camara.getCamara());

            b.setProjectionMatrix(camara.getCamara().combined);
            b.begin();

            if (entradas.escape()) {
                pausado = !pausado;
            }
//
//            if (!pausado) {
                if (!musicaJuego.estaReproduciendo()) {
                    musicaJuego.reproducir();
                }

                if (hiloCliente.getNumeroJugador() == 1) {
                    // Las entradas pertenecen al Jugador 1.
                    jugador.moverJugador(
                        entradas,
                        Jugadores.JUGADOR_1,
                        hiloCliente
                    );
                    jugador.animar(delta);

                } else if (hiloCliente.getNumeroJugador() == 2) {
                    // Las entradas pertenecen al Jugador 2.
                    jugador2.moverJugador(
                        entradas,
                        Jugadores.JUGADOR_2,
                        hiloCliente
                    );
                    jugador2.animar(delta);
                }

                asignarPosicionJugador(jugador, hiloCliente.getPosicionJugadores(Jugadores.JUGADOR_1));
                asignarPosicionJugador(jugador2, hiloCliente.getPosicionJugadores(Jugadores.JUGADOR_2));
                hud.actualizar();
//                if (hiloCliente.isEsteClienteTermino()) {
//                    cambiarPantalla(new PantallaFin(juego, posicionesFinales, hiloCliente));
//                    musicaJuego.cerrar();
//                }
                if (hiloCliente.isEsteClienteTermino()) {
                    conservarHilo = true;
                    cambiarPantalla(new PantallaFin(juego, posicionesFinales, hiloCliente));
                    musicaJuego.cerrar();
                }
//            }

//       if(pausado) {
////           textoPausa.setPosition(
////               (Config.getAnchoJuego() / 2) - (textoPausa.getAncho() / 2),
////               (Config.getAltoJuego() / 2) + (textoPausa.getAlto() / 2)
////           );
//           textoPausa.centrarArriba(150);
//           textoPausa.dibujar();
//       }
//
//            musicaJuego.pausar();
//
//
            jugador.dibujar();
            jugador2.dibujar();

            b.end();

            b.setProjectionMatrix(stage.getCamera().combined);
//
            if (pausado) {

                b.begin();

                textoPausa.centrarArriba(200);
                textoPausa.dibujar();

                b.end();

                dibujarPausa();
            }
//
            b.begin();

            hud.dibujar();

            b.end();

            stage.act(delta);
            stage.draw();
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

//    private void cargarColisionesDesdeMapa() {
//        if (mapa != null && mapa.getMapa() != null) {
//            TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getMapa().getLayers().get("Capa de patrones 1");
//            if (capa != null) {
//                int tileAncho = (int) capa.getTileWidth();
//                int tileAlto = (int) capa.getTileHeight();
//
//                for (int x = 0; x < capa.getWidth(); x++) {
//                    for (int y = 0; y < capa.getHeight(); y++) {
//                        TiledMapTileLayer.Cell cell = capa.getCell(x, y);
//                        if (cell != null && cell.getTile() != null) {
//                            MapProperties propiedades = cell.getTile().getProperties();
//
//                            if (propiedades.containsKey("solido") && propiedades.get("solido", Boolean.class)) {
//                                Plataforma bloque = new Plataforma(tileAncho, tileAlto, x * tileAncho, y * tileAlto);
//                                listaDeEntidades.add(bloque);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

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
                        cambiarPantalla(new PantallaMenu(juego));
                        hiloCliente.enviarMensaje("SeSalioDelJuego-" + hiloCliente.getNumeroJugador());
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
        hiloCliente.finalizarHilo();
//        stage.dispose();
//        b.dispose();
//        musicaJuego.cerrar();
//        Render.musicaJuego = null;
//        jugador.cerrar();
    }

    public void asignarPosicionJugador(Jugador jugadorElegido, float[] posiciones) {
        if (posiciones != null) {
            jugadorElegido.asignarPosicionJugador(posiciones[0], posiciones[1]);
        }
    }
}
