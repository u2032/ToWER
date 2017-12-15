/*
 *  Copyright 2017 Cedric Longo.
 *  This file is part of ToWER program <https://github.com/u2032/ToWER>
 *
 *  ToWER is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  ToWER is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 *  of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with ToWER.
 *  If not, see <http://www.gnu.org/licenses/>
 */

package land.tower.core;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.config.ConfigurationModule;
import land.tower.core.ext.effect.Effects;
import land.tower.core.ext.event.EventModule;
import land.tower.core.ext.i18n.I18nModule;
import land.tower.core.ext.inject.ModuleResolver;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.service.ServiceManager;
import land.tower.core.ext.service.ServiceModule;
import land.tower.core.ext.thread.ThreadingModule;
import land.tower.core.model.player.PlayerModule;
import land.tower.core.view.home.HomepageViewModule;
import land.tower.core.view.main.ApplicationScene;
import land.tower.core.view.main.MainViewModule;
import land.tower.core.view.player.PlayerViewModule;

/**
 * Created on 09/11/2017
 * @author Cédric Longo
 */
public final class Application extends javafx.application.Application {

    public static void main( final String[] args ) {
        Application.launch( Application.class, args );
    }

    @Override
    public void start( final Stage primaryStage ) throws Exception {
        try {
            final Injector injector = Guice.createInjector( modules( ).getModules( ) );
            final Configuration configuration = injector.getInstance( Configuration.class );

            /* Display a splashscreen */
            final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor( );
            final Pane splashScreen = displaySplashScreen( primaryStage, configuration );
            executor.scheduleWithFixedDelay( ( ) -> Platform.runLater( ( ) -> {
                if ( !_ready.get( ) ) {
                    return;
                }
                displayApplicationScene( injector.getInstance( ApplicationScene.class ),
                                         injector.getInstance( ServiceManager.class ),
                                         configuration );

                splashScreen.setEffect( null );

                final FadeTransition fadeSplash = new FadeTransition( Duration.seconds( 1 ), splashScreen );
                fadeSplash.setFromValue( 1.0 );
                fadeSplash.setToValue( 0.0 );
                fadeSplash.setOnFinished( actionEvent -> {
                    primaryStage.close( );
                } );
                fadeSplash.play( );

                executor.shutdown( );
            } ), 4, 2, TimeUnit.SECONDS );

            /* Start loading app */
            CompletableFuture.runAsync( ( ) -> {
                try {
                    loadFont( "fonts/NotoSans-Regular.ttf" );
                    loadFont( "fonts/NotoSans-Italic.ttf" );
                    loadFont( "fonts/NotoSans-Bold.ttf" );
                    loadFont( "fonts/NotoSans-BoldItalic.ttf" );
                    loadFont( "fonts/fa-regular-400.ttf" );
                    loadFont( "fonts/fa-solid-900.ttf" );

                    final ServiceManager serviceManager = injector.getInstance( ServiceManager.class );
                    serviceManager.startAll( );

                    _ready.set( true );

                } catch ( final Exception e ) {
                    _logger.error( "Exception caught during startup", e );
                    System.exit( 1 );
                }
            } );

        } catch ( final Exception e ) {
            _logger.error( "Exception caught during startup", e );
            System.exit( 1 );
        }
    }

    private void displayApplicationScene( final ApplicationScene scene,
                                          final ServiceManager serviceManager,
                                          final Configuration configuration ) {
        final Stage stage = new Stage( );
        scene.getStylesheets( ).add( configuration.getApplicationStyle( ) );

        stage.setMaximized( true );
        stage.setTitle( "♜ ToWER" );
        stage.setScene( scene );
        stage.setOnCloseRequest( value -> serviceManager.stopAll( ) );

        stage.show( );
    }

    private Pane displaySplashScreen( final Stage stage, final Configuration configuration ) {
        stage.setTitle( "♜ ToWER" );
        stage.setResizable( false );
        stage.initStyle( StageStyle.TRANSPARENT );
        stage.setAlwaysOnTop( true );

        final BorderPane pane = new BorderPane( );
        pane.setPadding( new Insets( 10, 10, 10, 10 ) );
        pane.setCenter( new ImageView( configuration.getSplashscreen( ) ) );
        pane.setEffect( Effects.dropShadow( ) );

        stage.setScene( new Scene( pane, Color.TRANSPARENT ) );
        stage.show( );

        return pane;
    }

    private void loadFont( final String name ) {
        final InputStream fontStream = getClass( ).getClassLoader( )
                                                  .getResourceAsStream( name );
        Font.loadFont( checkNotNull( fontStream, "Font %s not found", name ), -1 );
    }

    private static ModuleResolver modules( ) {
        return ModuleResolver.withModules( new ConfigurationModule( "config.properties" ),
                                           new MainViewModule( ),
                                           new HomepageViewModule( ),
                                           new ThreadingModule( ),
                                           new ServiceModule( ),
                                           new EventModule( ),
                                           new I18nModule( ),
                                           new PlayerModule( ),
                                           new PlayerViewModule( ) );
    }

    private final AtomicBoolean _ready = new AtomicBoolean( );
    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
