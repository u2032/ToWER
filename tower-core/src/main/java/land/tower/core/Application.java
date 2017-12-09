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

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.io.InputStream;
import java.util.List;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import land.tower.core.ext.event.EventModule;
import land.tower.core.ext.i18n.I18nModule;
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
        loadFont( "fonts/NotoSans-Regular.ttf" );
        loadFont( "fonts/NotoSans-Italic.ttf" );
        loadFont( "fonts/NotoSans-Bold.ttf" );
        loadFont( "fonts/NotoSans-BoldItalic.ttf" );

        final Injector injector = Guice.createInjector( modules( ) );

        final ServiceManager serviceManager = injector.getInstance( ServiceManager.class );
        serviceManager.startAll( );
        primaryStage.setOnCloseRequest( value -> serviceManager.stopAll( ) );

        final ApplicationScene scene = injector.getInstance( ApplicationScene.class );
        scene.getStylesheets( )
             .add( getClass( ).getClassLoader( ).getResource( "styles/application.css" ).toExternalForm( ) );

        primaryStage.setMaximized( true );
        primaryStage.setTitle( "♜ ToWER" );
        primaryStage.setScene( scene );
        primaryStage.show( );
    }

    private void loadFont( final String name ) {
        final InputStream fontStream = getClass( ).getClassLoader( )
                                                  .getResourceAsStream( name );
        Font.loadFont( checkNotNull( fontStream, "Font %s not found", name ), -1 );
    }

    private static List<Module> modules( ) {
        return ImmutableList.of( new MainViewModule( ),
                                 new HomepageViewModule( ),
                                 new ThreadingModule( ),
                                 new ServiceModule( ),
                                 new EventModule( ),
                                 new I18nModule( ),
                                 new PlayerModule( ),
                                 new PlayerViewModule( ) );
    }
}
