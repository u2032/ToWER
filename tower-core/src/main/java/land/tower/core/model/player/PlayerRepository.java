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

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.synchronizedObservableList;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javax.inject.Inject;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.service.IService;
import land.tower.core.ext.thread.ApplicationThread;
import land.tower.core.view.player.ObservablePlayer;
import land.tower.data.Player;

/**
 * Created on 06/12/2017
 * @author Cédric Longo
 */
public final class PlayerRepository implements IService {

    @Inject
    public PlayerRepository( final @ApplicationThread ScheduledExecutorService scheduledExecutorService ) {
        _scheduledExecutorService = scheduledExecutorService;
    }

    public void registerPlayer( Player player ) {
        _players.add( new ObservablePlayer( player ) );
        scheduleStorageUpdate( );
    }

    public void removePlayer( Player player ) {
        _players.removeIf( p -> p.getNumero( ) == player.getNumero( ) );
        scheduleStorageUpdate( );
    }

    public ObservableList<ObservablePlayer> getPlayersList( ) {
        return _players;
    }

    @Override
    public void start( ) {
        if ( !Files.exists( PLAYER_STORAGE ) ) {
            return;
        }
        try {
            try ( final FileReader fileReader = new FileReader( PLAYER_STORAGE.toFile( ) ) ) {
                final List<Player> list = new GsonBuilder( ).create( )
                                                            .fromJson( fileReader, new TypeToken<List<Player>>( ) {
                                                            }.getType( ) );
                _players.addAll( list.stream( )
                                     .map( ObservablePlayer::new )
                                     .collect( Collectors.toList( ) ) );
            }
        } catch ( IOException e ) {
            _logger.error( "Error loading player storage", e );
        }
    }

    @Override
    public void stop( ) {

    }

    public Optional<ObservablePlayer> getPlayer( final long numero ) {
        return _players.stream( )
                       .filter( p -> p.getNumero( ) == numero )
                       .findAny( );
    }

    private void scheduleStorageUpdate( ) {
        _scheduledExecutorService.schedule( ( ) -> {
            final String json = new GsonBuilder( ).create( )
                                                  .toJson( _players.stream( )
                                                                   .map( ObservablePlayer::getPlayer )
                                                                   .collect( Collectors.toList( ) ) );

            try {
                Files.createDirectories( PLAYER_STORAGE.getParent( ) );
            } catch ( final IOException e ) {
                _logger.error( "Error caught during creating directory: " + PLAYER_STORAGE.toString( ), e );
            }

            try ( final BufferedWriter out = Files.newBufferedWriter( PLAYER_STORAGE_TMP, StandardCharsets.UTF_8 ) ) {
                out.write( json );
            } catch ( final IOException e ) {
                _logger.error( "Error caught during saving storage", e );
            }

            try {
                Files.move( PLAYER_STORAGE_TMP, PLAYER_STORAGE, REPLACE_EXISTING, ATOMIC_MOVE );
            } catch ( final IOException e ) {
                _logger.error( "Error caught during saving storage", e );
            }

        }, 2, TimeUnit.SECONDS );
    }

    private final ObservableList<ObservablePlayer> _players = synchronizedObservableList( observableArrayList( ) );
    private final ScheduledExecutorService _scheduledExecutorService;

    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );

    private static final Path PLAYER_STORAGE = Paths.get( "data", "players.json" );
    private static final Path PLAYER_STORAGE_TMP = Paths.get( "data", "players.json.tmp" );
}
