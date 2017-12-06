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

package land.tower.core.ext.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import javax.inject.Inject;
import land.tower.core.ext.logger.Loggers;

/**
 * Created on 06/12/2017
 * @author Cédric Longo
 */
public final class ServiceManager {

    @Inject
    public ServiceManager( final Set<IService> services ) {
        _services = services;
    }

    public void startAll( ) {
        _services.forEach( service -> {
            _logger.info( "Starting service: " + service.getName( ) );
            service.getName( );
        } );
    }

    public void stopAll( ) {
        _services.forEach( service -> {
            try {
                _logger.info( "Stopping service: " + service.getName( ) );
                service.stop( );
            } catch ( final Exception e ) {
                _logger.error( "Error during stoping service " + service.getName( ) );
            }
        } );
    }

    private final Set<IService> _services;
    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
