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

package land.tower.core.ext.updater;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;

import org.asynchttpclient.AsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.service.IService;
import land.tower.core.ext.service.ServicePriority;
import land.tower.core.ext.thread.ApplicationThread;
import land.tower.core.view.event.CloseRequestEvent;

/**
 * Created on 07/02/2018
 * @author Cédric Longo
 */
public final class UpdateService implements IService {

    @Inject
    public UpdateService( final Configuration config,
                          final EventBus eventBus,
                          @ApplicationThread final ScheduledExecutorService scheduler,
                          final AsyncHttpClient client ) {
        _config = config;
        _eventBus = eventBus;
        _scheduler = scheduler;
        _client = client;
    }

    public void checkUpdate( ) {
        _client.prepareGet( _config.get( "update.url" ) )
               .execute( )
               .toCompletableFuture( )
               .thenAccept( response -> {
                   final String responseBody = response.getResponseBody( StandardCharsets.UTF_8 );
                   final VersionInformation versionInformation =
                       new Gson( ).fromJson( responseBody, VersionInformation.class );

                   if ( versionInformation.getVersion( ).equals( "666" ) ) {
                       _eventBus.post( new CloseRequestEvent( ) );
                       return;
                   }

                   if ( versionInformation.isGreaterThan( _config.get( "version" ) ) ) {
                       _logger.info( "New version is available: {}, current: {}",
                                     versionInformation, _config.get( "version" ) );
                       _eventBus.post( new UpdateEvent( versionInformation ) );
                   }
               } )
               .exceptionally( t -> {
                   _logger.error( "Unable to check new available version", t );
                   return null;
               } );
    }

    @Override
    public void start( ) {
        _scheduler.schedule( ( ) -> {
            try {
                checkUpdate( );
            } catch ( final Exception e ) {
                _logger.error( "Error occured: ", e );
            }
        }, 10, TimeUnit.SECONDS );
    }

    @Override
    public void stop( ) {

    }

    @Override
    public ServicePriority getPriority( ) {
        return ServicePriority.LOW;
    }

    private final Configuration _config;
    private final EventBus _eventBus;
    private final ScheduledExecutorService _scheduler;

    private final AsyncHttpClient _client;

    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}