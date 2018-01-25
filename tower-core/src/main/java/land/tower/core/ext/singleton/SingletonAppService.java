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

import javax.inject.Inject;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.service.IService;
import land.tower.core.ext.service.ServicePriority;

/**
 * Created on 21/01/2018
 * @author Cédric Longo
 */
final class SingletonAppService implements IService {

    @Inject
    SingletonAppService( final SingletonAppClient client,
                         final SingletonAppServer server ) {
        _client = client;
        _server = server;
    }

    @Override
    public void start( ) {
        if ( _client.sendMessage( ) ) {
            _logger.info( "One another instance is launched, closing." );
            System.exit( 0 );
        } else {
            _thread = new Thread( _server, "singleton-app-thread" );
            _thread.setDaemon( true );
            _thread.start( );
        }
    }

    @Override
    public void stop( ) {
        _server.stop( );
        if ( _thread != null ) {
            _thread.interrupt( );
        }
    }

    @Override
    public ServicePriority getPriority( ) {
        return ServicePriority.VERY_HIGH;
    }

    private final SingletonAppClient _client;
    private final SingletonAppServer _server;

    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
    private Thread _thread;
}
