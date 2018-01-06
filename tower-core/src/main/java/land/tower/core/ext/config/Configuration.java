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

package land.tower.core.ext.config;

import static java.lang.String.format;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import land.tower.core.ext.logger.Loggers;

/**
 * This class doesn't implement Service interface to be available immediately
 * Created on 14/12/2017
 * @author Cédric Longo
 */
public final class Configuration {

    public Configuration( final String file ) {
        try ( final InputStream in = getClass( ).getClassLoader( ).getResourceAsStream( file ) ) {
            if ( in == null ) {
                throw new NullPointerException( "No config file " + file + " found." );
            }

            LoggerFactory.getLogger( Loggers.MAIN ).info( "Loading configuration from: {}", file );
            final Properties properties = new Properties( );
            properties.load( in );
            properties.stringPropertyNames( )
                      .forEach( key -> _config.put( key, properties.getProperty( key ) ) );

        } catch ( final IOException e ) {
            Throwables.propagate( e );
        }
    }

    public Image getSplashscreen( ) {
        final InputStream imStream = getClass( ).getClassLoader( ).getResourceAsStream( get( "splashcreen" ) );
        return new Image( imStream, 600, 400, true, true );
    }

    public Image getHomePlayerButton( ) {
        final InputStream imStream = getClass( ).getClassLoader( ).getResourceAsStream( get( "home.player.button" ) );
        return new Image( imStream, 200, 400, true, true );
    }

    public Image getHomeTournamentButton( ) {
        final InputStream imStream = getClass( ).getClassLoader( )
                                                .getResourceAsStream( get( "home.tournament.button" ) );
        return new Image( imStream, 200, 400, true, true );
    }

    public String getApplicationStyle( ) {
        return getClass( ).getClassLoader( ).getResource( get( "application.style" ) ).toExternalForm( );
    }

    public void setIcons( final Stage stage ) {
        if ( _icons != null ) {
            stage.getIcons( ).setAll( _icons );
            return;
        }
        _icons = IntStream.of( 16, 24, 32, 48, 64, 96, 128, 256, 512 )
                          .mapToObj( size -> {
                              final InputStream imStream = getClass( ).getClassLoader( )
                                                                      .getResourceAsStream(
                                                                          format( "img/icons/icon_%1$sx%1$s.png",
                                                                                  size ) );
                              if ( imStream == null ) {
                                  return Optional.<Image>empty( );
                              }
                              return Optional.of( new Image( imStream, size, size, true, true ) );
                          } )
                          .filter( Optional::isPresent )
                          .map( Optional::get )
                          .collect( Collectors.toList( ) );

        stage.getIcons( ).setAll( _icons );
    }

    public String get( final String key ) {
        return _config.get( key );
    }

    public String getTitle( ) {
        return get( "title" );
    }

    private final Map<String, String> _config = Maps.newHashMap( );
    private List<Image> _icons;
}
