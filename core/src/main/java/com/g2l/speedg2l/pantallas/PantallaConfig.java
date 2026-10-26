
package com.g2l.speedg2l.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.g2l.speedg2l.componentes.Boton;
import com.g2l.speedg2l.utilidades.Config;
import com.g2l.speedg2l.utilidades.ModoPantalla;
import com.g2l.speedg2l.utilidades.Render;
import com.g2l.speedg2l.utilidades.Resolucion;

public class PantallaConfig extends Pantalla {

    private Stage stage;
    private Skin skin;

    private Label textoVolumen;
    private Label textoSilencio;

    private Resolucion[] resoluciones;

    public PantallaConfig(Game juego) {
        super(juego);
    }

    @Override
    public void show() {
        stage = new Stage(configViewport.getViewport());
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        calcularResoluciones();
        crearInterfaz();

        configurarInput(stage);
    }

    private void calcularResoluciones() {
        int anchoMax = Config.getAnchoMonitor();
        int altoMax = Config.getAltoMonitor();

        Resolucion[] disponibles = Resolucion.values();

        resoluciones = java.util.Arrays.stream(disponibles)
            .filter(r -> r.getAncho() <= anchoMax
                && r.getAlto() <= altoMax)
            .toArray(Resolucion[]::new);
    }

    private void crearInterfaz() {
        Table tabla = crearTabla();

        Label titulo = crearTitulo();

        SelectBox<Resolucion> selectorResoluciones =
            crearSelectorResoluciones();

        SelectBox<String> selectorModoPantalla =
            crearSelectorModoPantalla();

        configurarListenersPantalla(
            selectorResoluciones,
            selectorModoPantalla
        );

        crearTextosVolumen();

        Boton btnSubirVolumen = crearBotonSubirVolumen();
        Boton btnBajarVolumen = crearBotonBajarVolumen();
        Boton btnSilenciar = crearBotonSilenciar();
        Boton btnVolver = crearBotonVolver();

        agregarControlesTabla(
            tabla,
            titulo,
            selectorResoluciones,
            selectorModoPantalla,
            btnSubirVolumen,
            btnBajarVolumen,
            btnSilenciar,
            btnVolver
        );

        stage.addActor(tabla);
    }

    private Table crearTabla() {
        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        return tabla;
    }

    private Label crearTitulo() {
        Label titulo = new Label("CONFIGURACION", skin);
        titulo.setFontScale(1.5f);
        return titulo;
    }

    private SelectBox<Resolucion> crearSelectorResoluciones() {
        SelectBox<Resolucion> selector = new SelectBox<>(skin);

        selector.setItems(resoluciones);

        if (resoluciones.length > 0) {
            seleccionarResolucionActual(selector);
        }

        return selector;
    }

    private void seleccionarResolucionActual(
        SelectBox<Resolucion> selector
    ) {
        for (Resolucion resolucion : resoluciones) {
            if (resolucion.getAncho()
                == Config.getAnchoPantallaActual()
                && resolucion.getAlto()
                == Config.getAltoPantallaActual()) {

                selector.setSelected(resolucion);
                return;
            }
        }

        if (resoluciones.length > 0) {
            selector.setSelected(resoluciones[0]);
        }
    }

    private SelectBox<String> crearSelectorModoPantalla() {
        SelectBox<String> selector = new SelectBox<>(skin);

        selector.setItems(
            ModoPantalla.VENTANA.getNombre(),
            ModoPantalla.PANTALLA_COMPLETA.getNombre()
        );

        // Detectar el modo actual.
        String modoActual = Gdx.graphics.isFullscreen()
            ? ModoPantalla.PANTALLA_COMPLETA.getNombre()
            : ModoPantalla.VENTANA.getNombre();

        selector.setSelected(modoActual);

        return selector;
    }

    private void configurarListenersPantalla(
        SelectBox<Resolucion> selectorResoluciones,
        SelectBox<String> selectorModoPantalla
    ) {
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Resolucion resolucion =
                    selectorResoluciones.getSelected();

                String modo = selectorModoPantalla.getSelected();

                if (resolucion != null && modo != null) {
                    aplicarConfiguracionPantalla(resolucion, modo);
                }
            }
        };

        selectorResoluciones.addListener(listener);
        selectorModoPantalla.addListener(listener);
    }

    private void aplicarConfiguracionPantalla(
        Resolucion resolucion,
        String modo
    ) {
        if (modo.equals(ModoPantalla.VENTANA.getNombre())) {
            aplicarModoVentana(resolucion);
        } else if (
            modo.equals(ModoPantalla.PANTALLA_COMPLETA.getNombre())
        ) {
            aplicarPantallaCompleta(resolucion);
        }
    }

    private void aplicarModoVentana(Resolucion resolucion) {
        Gdx.graphics.setWindowedMode(
            resolucion.getAncho(),
            resolucion.getAlto()
        );
    }

    private void aplicarPantallaCompleta(Resolucion resolucion) {
        Graphics.DisplayMode[] modos = Gdx.graphics.getDisplayModes();

        for (Graphics.DisplayMode modo : modos) {
            if (modo.width == resolucion.getAncho()
                && modo.height == resolucion.getAlto()) {

                Gdx.graphics.setFullscreenMode(modo);
                return;
            }
        }

        Gdx.app.log(
            "PantallaConfig",
            "No se encontró un modo de pantalla compatible."
        );
    }

    private void crearTextosVolumen() {
        textoVolumen = new Label(
            "Volumen: "
                + Math.round(Config.getVolumenMaster() * 100)
                + "%",
            skin
        );

        textoSilencio = new Label(
            "Sonido: "
                + (Config.isSonidoSilenciado()
                ? "Silenciado"
                : "Activado"),
            skin
        );
    }

    private Boton crearBotonSubirVolumen() {
        return new Boton(
            "Subir volumen",
            skin,
            new ClickListener() {
                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {
                    Config.setVolumenMaster(
                        Config.getVolumenMaster() + 0.1f
                    );

                    actualizarVolumen();
                    actualizarTextoVolumen();
                }
            },
            250,
            60
        );
    }

    private Boton crearBotonBajarVolumen() {
        return new Boton(
            "Bajar volumen",
            skin,
            new ClickListener() {
                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {
                    Config.setVolumenMaster(
                        Config.getVolumenMaster() - 0.1f
                    );

                    actualizarVolumen();
                    actualizarTextoVolumen();
                }
            },
            250,
            60
        );
    }

    private Boton crearBotonSilenciar() {
        return new Boton(
            "Silenciar / activar",
            skin,
            new ClickListener() {
                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {
                    Config.setSonidoSilenciado(
                        !Config.isSonidoSilenciado()
                    );

                    actualizarVolumen();
                    actualizarTextoVolumen();
                    actualizarTextoSilencio();
                }
            },
            250,
            60
        );
    }

    private void actualizarVolumen() {
        if (Render.musicaJuego == null) {
            return;
        }

        if (Config.isSonidoSilenciado()) {
            Render.musicaJuego.setVolumen(0.0f);
        } else {
            Render.musicaJuego.setVolumen(
                Config.getVolumenMaster()
            );
        }
    }

    private Boton crearBotonVolver() {
        return new Boton(
            "Volver",
            skin,
            new ClickListener() {
                @Override
                public void clicked(
                    InputEvent event,
                    float x,
                    float y
                ) {
                    cambiarPantalla(new PantallaMenu(juego));
                }
            },
            250,
            60
        );
    }

    private void agregarControlesTabla(
        Table tabla,
        Label titulo,
        SelectBox<Resolucion> selectorResoluciones,
        SelectBox<String> selectorModoPantalla,
        Boton btnSubirVolumen,
        Boton btnBajarVolumen,
        Boton btnSilenciar,
        Boton btnVolver
    ) {
        Label textoResolucion = new Label("Resolución:", skin);
        Label textoModoPantalla = new Label("Modo de pantalla:", skin);

        tabla.add(titulo).padBottom(30).row();
        tabla.add(textoResolucion).padBottom(10).row();
        tabla.add(selectorResoluciones).width(250).padBottom(25).row();
        tabla.add(textoModoPantalla).padBottom(10).row();
        tabla.add(selectorModoPantalla).width(250).padBottom(25).row();
        tabla.add(textoVolumen).padBottom(10).row();
        tabla.add(btnSubirVolumen).pad(5).row();
        tabla.add(btnBajarVolumen).pad(5).row();
        tabla.add(textoSilencio).padTop(15).padBottom(10).row();
        tabla.add(btnSilenciar).padBottom(25).row();
        tabla.add(btnVolver).row();
    }

    private void actualizarTextoVolumen() {
        textoVolumen.setText(
            "Volumen: "
                + Math.round(Config.getVolumenMaster() * 100)
                + "%"
        );
    }

    private void actualizarTextoSilencio() {
        textoSilencio.setText(
            "Sonido: "
                + (Config.isSonidoSilenciado()
                ? "Silenciado"
                : "Activado")
        );
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        configViewport.aplicarViewport(Render.batch);

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
        configurarInput(null);
    }

    @Override
    public void dispose() {
        configurarInput(null);

        if (stage != null) {
            stage.dispose();
            stage = null;
        }

        if (skin != null) {
            skin.dispose();
            skin = null;
        }
    }
}
