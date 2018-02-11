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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.logger.Loggers;

/**
 * Created on 21/01/2018
 * @author Cédric Longo
 */
class SingletonAppClient {

    @Inject
    SingletonAppClient( final Configuration configuration ) {
        _configuration = configuration;
    }

    public boolean sendMessage( ) {
        final int port = Integer.parseInt( _configuration.get( "singleton.app.port" ) );
        _logger.info( "App is starting, trying to communicate with launched instance on port {}", port );
        try ( Socket socket = new Socket( InetAddress.getLoopbackAddress( ), port );
              PrintWriter pred = new PrintWriter(
                  new BufferedWriter( new OutputStreamWriter( socket.getOutputStream( ) ) ), true ) ) {

            pred.println( HELLO_MESSAGE );

        } catch ( final ConnectException e ) {
            _logger.info( "Communication failed: No instance launched. Starting..." );
            return false;

        } catch ( IOException e ) {
            _logger.info( "Communication failed in an unexpected way. Starting anyway.", e );
            return false;
        }

        return true;
    }

    private final Configuration _configuration;

    public static final String HELLO_MESSAGE = "HELLO_TOWER";
    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
