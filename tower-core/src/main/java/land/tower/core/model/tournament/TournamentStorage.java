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

package land.tower.core.model.tournament;

import com.google.common.base.Throwables;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.thread.ApplicationThread;
import land.tower.data.Tournament;

/**
 * Created on 16/12/2017
 * @author Cédric Longo
 */
final class TournamentStorage implements ITournamentStorage {

    @Inject
    TournamentStorage( final @ApplicationThread ScheduledExecutorService scheduledExecutorService ) {
        _scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public List<Tournament> loadTournaments( ) {
        final ArrayList<Tournament> result = new ArrayList<>( );
        if ( !Files.exists( TOURNAMENT_STORAGE ) ) {
            return result;
        }

        try {
            Files.walk( TOURNAMENT_STORAGE, 1 )
                 .filter( p -> p.getFileName( ).toString( ).endsWith( ".twr" ) )
                 .filter( p -> p.toFile( ).isFile( ) )
                 .forEach( p -> {
                     try ( final FileReader fileReader = new FileReader( p.toFile( ) ) ) {
                         final Tournament t = new GsonBuilder( ).create( ).fromJson( fileReader, Tournament.class );
                         result.add( t );
                     } catch ( final Exception e ) {
                         _logger.error( "Failure loading tournament from file: " + p.toAbsolutePath( ), e );
                     }
                 } );
        } catch ( final IOException e ) {
            _logger.error( "Error caught during loading tournament from storage", e );
            throw Throwables.propagate( e );
        }

        return result;
    }

    @Override
    public void saveTournament( final Tournament tournament ) {
        _scheduledExecutorService.schedule( ( ) -> {
            if ( !Files.exists( TOURNAMENT_STORAGE ) ) {
                try {
                    Files.createDirectories( TOURNAMENT_STORAGE );
                } catch ( final IOException e ) {
                    _logger.error( "Can't create directories: " + TOURNAMENT_STORAGE.toAbsolutePath( ), e );
                }
            }

            final String json = new GsonBuilder( ).create( ).toJson( tournament );
            final Path fileTmp = TOURNAMENT_STORAGE.resolve( tournament.getId( ).toString( ) + ".twr.tmp" );
            try ( final BufferedWriter out = Files.newBufferedWriter( fileTmp, StandardCharsets.UTF_8 ) ) {
                out.write( json );
            } catch ( final IOException e ) {
                _logger.error( "Error caught during saving tournament" + tournament.getId( ) + " in storage", e );
            }

            try {
                final Path file = TOURNAMENT_STORAGE.resolve( tournament.getId( ).toString( ) + ".twr" );
                Files.move( fileTmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE );
                _logger.info( "Tournament saved into: {}", file );
                
            } catch ( IOException e ) {
                _logger.error( "Error during saving tournament: " + tournament.getId( ), e );
            }

        }, 2, TimeUnit.SECONDS );
    }

    @Override
    public void deleteTournament( final Tournament tournament ) {
        _scheduledExecutorService.schedule( ( ) -> {
            if ( !Files.exists( TOURNAMENT_STORAGE ) ) {
                return;
            }

            try {
                final Path file = TOURNAMENT_STORAGE.resolve( tournament.getId( ).toString( ) + ".twr" );
                Files.deleteIfExists( file );
            } catch ( IOException e ) {
                _logger.error( "Error during deleting tournament: " + tournament.getId( ), e );
            }
        }, 2, TimeUnit.SECONDS );
    }

    private final ScheduledExecutorService _scheduledExecutorService;

    private static final Path TOURNAMENT_STORAGE = Paths.get( "data", "tournaments" );

    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
