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

package land.tower.core.model.player;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.google.common.base.Throwables;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.thread.ApplicationThread;
import land.tower.data.Player;

/**
 * Created on 16/12/2017
 * @author Cédric Longo
 */
final class PlayerStorage implements IPlayerStorage {

    @Inject
    PlayerStorage( final Configuration configuration,
                   @ApplicationThread final ScheduledExecutorService scheduledExecutorService ) {
        _configuration = configuration;
        _scheduledExecutorService = scheduledExecutorService;

        playerStorage = configuration.dataDirectory( ).resolve( "players.json" );
        playerStorageTmp = configuration.dataDirectory( ).resolve( "players.json._COPYING_" );
    }

    @Override
    public List<Player> loadPlayers( ) {
        if ( !Files.exists( playerStorage ) ) {
            return new ArrayList<>( );
        }
        try {
            try ( final BufferedReader fileReader = Files.newBufferedReader( playerStorage, UTF_8 ) ) {
                return new GsonBuilder( ).create( )
                                         .fromJson( fileReader, new TypeToken<List<Player>>( ) {
                                         }.getType( ) );
            }
        } catch ( final IOException e ) {
            _logger.error( "Error loading player storage", e );
            throw Throwables.propagate( e );
        }
    }

    @Override
    public void savePlayers( final List<Player> players ) {
        _scheduledExecutorService.schedule( ( ) -> doSave( players ), 2, TimeUnit.SECONDS );
    }

    private synchronized void doSave( final List<Player> players ) {
        final String json = new GsonBuilder( ).create( ).toJson( players );

        try {
            Files.createDirectories( playerStorage.getParent( ) );
        } catch ( final IOException e ) {
            _logger.error( "Error caught during creating directory: " + playerStorage.toString( ), e );
        }

        try ( final BufferedWriter out = Files.newBufferedWriter( playerStorageTmp, UTF_8 ) ) {
            out.write( json );
        } catch ( final IOException e ) {
            _logger.error( "Error caught during saving storage", e );
        }

        try {
            Files.move( playerStorageTmp, playerStorage, REPLACE_EXISTING, ATOMIC_MOVE );
        } catch ( final IOException e ) {
            _logger.error( "Error caught during saving storage", e );
        }

        _logger.info( "Player list saved: {} players in storage", players.size( ) );
    }

    private final Configuration _configuration;

    private final ScheduledExecutorService _scheduledExecutorService;

    private final Path playerStorage;
    private final Path playerStorageTmp;

    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
