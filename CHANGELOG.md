# Changelog

Todos los cambios importantes de este proyecto serán documentados en este archivo.

El formato de este archivo está basado en Keep a Changelog.

<<<<<<< HEAD

# [0.20.0] - 2026-11-11

# Agregado
- Se agrego una pantalla de carga al iniciar el juego.
- Se agrego la posibilidad de que el jugador rival deje de visualizar al personaje cuando termina la partida.

# Modificado
- Se reorganizo el procesamiento de mensajes en "HiloCliente".
- Se realizaron metodos para procesar los mensajes.
- Se corrigio una condicion duplicada en "pantallaJuego".
- Se mejoro el cierre y la liberacion de recursos en memoria.
- Se corrigieron problemas de visibilidad de botones.
- Se corrigieron errores en el mapa.
- Se elimino el titulo duplicado de la pantalla de menu.

# [0.19.0] - 2026-11-10

# Agregado
- Se agrego la posibilidad de volver a jugar una partida desde la pantalla final.
- Se agrego un mensaje de error para informar cuando el servidor esta lleno.

# Modificado
- Se completo el manejo de desconexiones de los jugadores.
- Se simplifico la gestion de conexiones del cliente.
- Se centralizo la deteccion de desconexion del servidor.
- Se simplifico y actualizo la clase "PantallaConfig".
- Se corrigio la gestion del puerto del cliente.
- Se elimino el acceso global al "SpriteBatch".
- Se elimino el acceso global a la musica.
- Se eliminaron lineas de codigo innecesarias.

# [0.18.0] - 2026-11-08

# Modificado
- Se mejoro la organizacion del codigo y se eliminaron partes antiguas innecesarias.

# [0.17.0] - 2026-11-03

# Modificado
- Se corrigio un problema que ocurria al regresar desde la pantalla de configuracion.
- Se corrigio la carga del puerto del cliente.

# [0.16.0] - 2026-10-31

# Agregado
- Se agrego una cuenta regresiva antes de iniciar la partida.

# Modificado
- Se centralizaron las constantes del protocolo de red.

# [0.15.0] - 2026-10-29

# Agregado
- Se agregaron las instrucciones del juego.
- Se agrego un protocolo de red para mejorar la robustez de los mensajes enviados.

# Modificado
- Se aplico el patron Factory para simplificar el codigo.
- Se diferencio la logica del jugador local y remoto.
- Se modificaron las animaciones de las entidades para permitir que los jugadores cambien de direccion.

# [0.14.0] - 2026-10-28

# Agregado
- Se agregaron respuestas para manejar la desconexion de un jugador.

# Modificado
- Se corrigieron los efectos de frenado de la lava.

# [0.13.0] - 2026-10-27

# Modificado
- Se corrigio el cierre de recursos al cambiar de pantalla.
- Se corrigio el seguimiento de la camara.
- Se actualizaron el salto y las animaciones del jugador.
- Se corrigio un error en la pantalla final.
- Se evito el envio redundante de paquetes de mensajes de entrada.

# [0.12.0] - 2026-10-24

# Agregado
- Se agrego un mensaje para informar cuando no hay ningun servidor conectado.
- Se agrego un mensaje de error para informar cuando se desconecta el servidor.

# [0.11.0] - 2026-10-23

# Agregado
- Se agrego la posibilidad de seguir jugando luego de terminar una partida.

# [0.10.0] - 2026-10-18

# Modificado
- Se corrigio el cierre del hilo.
- Se agregaron validaciones para mejorar la conexion con el hilo.

# [0.9.0] - 2026-10-16

# Agregado
- Se agregaron los recursos visuales para el segundo jugador.
- Se agrego un nuevo fondo para el menu.
- Se incorporaron dos jugadores a la pantalla de juego.

# Modificado
- Se actualizaron las rutas de las imagenes de los jugadores.
- Se actualizo el constructor de "Jugador".

# [0.8.0] - 2026-10-12

# Agregado
- Se agrego la clase "Lava" como nuevo obstaculo.
- Se agregaron atributos y el metodo "aplicarEfectoLava".
- Se agrego un boton para volver al menu desde la pantalla de espera.
- Se agrego la posibilidad de volver al menu desde la pantalla de pausa.
- Se agrego la funcionalidad para mostrar los resultados finales de la partida.

# Modificado
- Se actualizo el tileset para incorporar el atributo de la lava.
- Se modifico la carga de colisiones para reconocer correctamente la lava.
- Se mejoro el funcionamiento de la desconexion de los jugadores.
- Se corrigio la salida al menu para evitar problemas con el servidor.
- Se avanzo en la visualizacion de los resultados finales.

# [0.7.0] - 2026-10-11

# Agregado
- Se agrego una pantalla final completamente funcional.

# Modificado
- Se realizaron ajustes en la pantalla final para mostrar los resultados de la partida.

# [0.6.0] - 2026-10-07

# Agregado
- Se agrego la funcionalidad para que ambos jugadores puedan moverse.
- Se agrego un mensaje de espera para los usuarios.
- Se comenzaron a desarrollar las bases del hilo del cliente.

# Modificado
- Se avanzo en la asociacion entre lo que muestra el servidor y la pantalla del cliente.
- Se realizaron ajustes para mejorar el funcionamiento del cliente.
- Se agrego el cierre automatico del hilo.

## [0.19.0] - 2026-09-04

## Agregado

- Se agregaron getters para tamaño de pantalla actual
- Se agrego y aplico el archivo de efecto de sonido para la meta
- Se realizo la clase para los efectos de sonido
- Se hizo el merge final entre ramas para la segunda pre-entrega

## Modificado

- Se arreglo la centralizacion de la pausa

## [0.18.0] - 2026-09-03

## Agregado

- Se agrego la pantalla completa y ventana opcional
- Se agregaron los controles para la musica

## Modificado

- Se agrego el listener de entrada en "pantallaFin"
- Se agrego el modo ventana por default

## [0.17.5] - 2026-09-02

## Modificado

- Se arreglo un problema de rendimiento
- Se arreglo el bug donde el jugador volaba

## [0.17.0] - 2026-09-01

## Agregado

- Se realizo un nuevo metodo para cargar las colisiones y poner en uso los atributos booleanos hechos anteriormente

## Modificado

- Se arreglo el contenido del tileset ya que tenia errores
- Se arreglo el bug de muchas posibles instancias de "pantallaJuego"

## [0.16.0] - 2026-08-31

## Agregado

- Se agrego la pantalla final
- Se agrego un getter en "Mapa"

## [0.15.0] - 2026-08-29

## Agregado

- Se agrego la clase para las animaciones de entidades
- Se agrego la animacion para el jugador
- Se agrego la imagen de los pichos
- Se agrego el spritesheet del jugador corriendo

## Modificado

- Se aumento el alcance de la interfaz "Cerrable"

## [0.14.0] - 2026-08-26

## Agregado

- Se agrego la HUD y mejoras en la camara del nivel
- Se agrego la camara que sigue al jugador
- Se agregaron nuevas rutas
- Se añado la interfaz "Cerrable"
- Se agregaron atributos booleanos esenciales para las colisiones en el Tiled

## Modificado

- Se arreglo un bug, donde el jugador no podia moverse para la izquierda
- Se actualizo el mapa del nivel

## [0.13.0] - 2026-08-25

## Agregado

- Se agrego la clase "Musica"
- Se agrego la clase "Hud"
- Se implementaron distintas resoluciones funcionales

## [0.12.0] - 2026-08-24

## Agregado

- Se agrego el viewport para controlar la resolucion
- Se agrego el panel de botones
- Se creo la clase padre "Pantalla" como modelo de las demas
- Se agrego la pantalla completa al iniciar el juego
- Se termino el nivel basico

## Modificado

- Se realizo un cambio general de la app para su resolucion
- Se mejoro la responsabilidad de los botones
- Se actualizo el tileset para el mapa del nivel

## [0.11.0] - 2026-08-23

## Agregado

- Se agrego el mapa a la pantalla de juego
- Se realizo la clase padre para animaciones
- Se realizo la clase hija de animacion "Fade"

## Modificado

- Se delegaron las responsabilidades a Fade para animacion
- Se modifico la forma de verificar la colision

## [0.10.0] - 2026-08-16

## Agregado

- Se creo el mapa jugable
- Se realizo el tileset hecho en photopea
- Se hicieron avances en el mapa (piso, techo, obstaculos)

## [0.9.0] - 2026-07-30

## Agregado

- Se agrego la clase "Obstaculo"
- Se implementaron los obstaculos, sin los efectos

## Modificado

- Se hicieron cambios en la clase "Pincho"

## [0.8.0] - 2026-07-26

## Modificado

- Se arreglaron problemas en las colisiones

## [0.7.5] - 2026-07-25

## Modificado

- Se mejoraron las colisiones

## [0.7.0] - 2026-07-24

## Agregado

- Se agrego la imagen de la pantalla de carga
- Se agregaron funciones para centrar texto
- Se agregaron la imagen, texto y cambio de resolucion de prueba
- Se agrego la transparencia
- Se agrego la pantalla de carga
- Se agrego la version inicial de pantalla de juego
- Se agrego la plataforma

## [0.6.0] - 2026-07-23

## Agregado

- Se agrego la utilidad para la configuracion inicial
- Se aplico la configuracion general del programa
- Se implementaron los botones funcionales del menu
- Se creo la clase "Texto" para mayor abstraccion
- Se agrego el fondo y fuente de pruebas


## [0.5.0] - 2026-07-22

## Agregado

- Se creo la clase "Render" para dibujar
- Se incorporo la clase "Entradas" para los inputs
- Se agrego la clase "Recursos" para almacenar rutas
- Se creo el componente "Imagen" para un mejor manejo
- Se implemento "Entradas" en la clase "Jugador"
- Se realizo la version inicial del menu del juego
- Se realizo un nuevo movimiento para el jugador y se implementaron las clases "Render" y "PantallaMenu"
- Se realizo la clase "Boton"
- Se agrego la skin para los botones
- Se agrego la fisica del salto del jugador
- Se agrego la funcion del salto para el jugador, haciendo un merge
- Se agrego la logica de aceleracion para el personaje
- Se agrego una plataforma
- Se agrego la version inicial del sistema de colisiones en la clase "Jugador"
- Se actualizo la version del sistema de colisiones

=======
>>>>>>> repositorio_original_SpeedG2L/nacho

## [0.4.0] - 2026-07-21

### Agregado

- Se incorporo la clase "SpeedG2L" al programa
<<<<<<< HEAD
- Se incorporo la clase del juego "SpeedG2L" al programa
- Se creo e incorporo la funcionalidad inicial de movimiento a la clase "Jugador"

## Modificado
- Se reemplazo la clase "Main" por la clase "SpeedG2L"

=======
>>>>>>> repositorio_original_SpeedG2L/nacho

## [0.3.0] - 2026-07-15

### Agregado

- Incorporación de imágenes y referencias visuales en la Wiki.
- Se incorporo una tabla en la Wiki, ubicada en la sección "Organización del trabajo"
- Actualización del README con instrucciones de ejecución.

## [0.2.0] - 2026-07-14

### Agregado

- Publicación de la propuesta formal en la Wiki.

## [0.1.0] - 2026-07-13

### Agregado

- Inicialización del proyecto mediante LibGDX LiftOff.
- Creación del repositorio Git.
- Incorporación de README.md.
- Incorporación de CHANGELOG.md.
- Configuración inicial del archivo .gitignore.
