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

package land.tower.core.ext.singleton;

import com.google.common.eventbus.EventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.logger.Loggers;

/**
 * Created on 21/01/2018
 * @author Cédric Longo
 */
final class SingletonAppServer implements Runnable {

    @Inject
    SingletonAppServer( final EventBus eventBus, final Configuration configuration ) {
        _eventBus = eventBus;
        _configuration = configuration;
    }

    @Override
    public void run( ) {
        final int port = Integer.parseInt( _configuration.get( "singleton.app.port" ) );
        _logger.info( "Listening for communication on port {}", port );

        try ( final ServerSocket socket = _serverSocket = new ServerSocket( port, 10,
                                                                            InetAddress.getLoopbackAddress( ) ) ) {
            while ( true ) {
                try ( Socket soc = socket.accept( );
                      BufferedReader plec = new BufferedReader( new InputStreamReader( soc.getInputStream( ) ) ) ) {

                    final String message = plec.readLine( );
                    _logger.info( "Received message from another instance: {}", message );

                    if ( SingletonAppClient.HELLO_MESSAGE.equals( message ) ) {
                        _eventBus.post( new SingletonAppEvent( ) );
                    }

                } catch ( IOException e1 ) {
                    if ( !_stopping ) {
                        _logger.info( "Communication failed in an unexpected way", e1 );
                    }
                }
            }

        } catch ( IOException e ) {
            if ( !_stopping ) {
                _logger.info( "Sevrer binding for communication failed in an unexpected way", e );
            }
        }
    }

    public void stop( ) {
        _stopping = true;
        if ( _serverSocket != null ) {
            try {
                _serverSocket.close( );
            } catch ( IOException ignored ) {
            }
        }
    }

    private ServerSocket _serverSocket;
    private boolean _stopping;

    private final EventBus _eventBus;
    private final Configuration _configuration;
    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
