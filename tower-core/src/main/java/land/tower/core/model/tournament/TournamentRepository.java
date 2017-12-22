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

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.synchronizedObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.collections.ObservableList;
import javax.inject.Inject;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.service.IService;
import land.tower.core.ext.thread.ApplicationThread;
import land.tower.data.Address;
import land.tower.data.PairingMode;
import land.tower.data.Tournament;
import land.tower.data.TournamentHeader;
import land.tower.data.TournamentStatus;

/**
 * Created on 16/12/2017
 * @author Cédric Longo
 */
public final class TournamentRepository implements IService {

    @Inject
    public TournamentRepository( final ITournamentStorage storage,
                                 final @ApplicationThread ScheduledExecutorService scheduler ) {
        _storage = storage;
        _scheduler = scheduler;
    }

    @Override
    public void start( ) {
        _tournaments.clear( );
        _storage.loadTournaments( )
                .stream( )
                .map( ObservableTournament::new )
                .forEach( _tournaments::add );
        _logger.info( "{} tournaments loaded from storage", _tournaments.size( ) );

        _scheduler.scheduleWithFixedDelay( this::saveDirtyTournaments, 30, 30, TimeUnit.SECONDS );
    }

    private synchronized void saveDirtyTournaments( ) {
        _tournaments.stream( )
                    .filter( ObservableTournament::isDirty )
                    .forEach( t -> _storage.saveTournament( t.getTournament( ) ) );
    }

    @Override
    public void stop( ) {
        saveDirtyTournaments( );
    }

    public ObservableTournament create( ) {
        final Tournament tournament = new Tournament( );
        tournament.setId( UUID.randomUUID( ) );
        tournament.setKey( UUID.randomUUID( ) );

        final TournamentHeader header = new TournamentHeader( );
        header.setTitle( "" );
        header.setDate( LocalDateTime.now( ).truncatedTo( ChronoUnit.MINUTES ) );
        header.setStatus( TournamentStatus.NOT_CONFIGURED );
        header.setPairingMode( PairingMode.SWISS );
        header.setkValue( 1 );
        header.setTeamSize( 1 );
        header.setMatchDuration( 30 );
        tournament.setHeader( header );

        final Address address = new Address( );
        address.setName( "" );
        address.setLine1( "" );
        address.setLine2( "" );
        address.setPostalCode( "" );
        address.setCity( "" );
        address.setCountry( "" );
        header.setAddress( address );

        final ObservableTournament observableTournament = new ObservableTournament( tournament );
        _tournaments.add( observableTournament );
        _storage.saveTournament( tournament );
        return observableTournament;
    }

    public ObservableList<ObservableTournament> getTournamentList( ) {
        return _tournaments;
    }

    public void remove( final Tournament tournament ) {
        _tournaments.removeIf( t -> tournament.getId( ).equals( t.getTournament( ).getId( ) ) );
        _storage.deleteTournament( tournament );
    }

    private final ObservableList<ObservableTournament> _tournaments =
        synchronizedObservableList( observableArrayList( ) );

    private final ITournamentStorage _storage;
    private final ScheduledExecutorService _scheduler;

    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
