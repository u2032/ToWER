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

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.collections.ObservableList;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.preference.Preferences;
import land.tower.core.ext.service.IService;
import land.tower.core.ext.thread.ApplicationThread;
import land.tower.data.Address;
import land.tower.data.PairingMode;
import land.tower.data.Tournament;
import land.tower.data.TournamentHeader;
import land.tower.data.TournamentScoringMode;
import land.tower.data.TournamentStatus;
import land.tower.data.TournamentType;

/**
 * Created on 16/12/2017
 * @author Cédric Longo
 */
public final class TournamentRepository implements IService {

    @Inject
    public TournamentRepository( final ITournamentStorage storage,
                                 final @ApplicationThread ScheduledExecutorService scheduler,
                                 final Preferences preferences, final Configuration config ) {
        _storage = storage;
        _scheduler = scheduler;
        _preferences = preferences;
        _config = config;
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
                    .forEach( t -> {
                        t.markAsClean( );
                        _storage.saveTournament( t );
                    } );
    }

    @Override
    public void stop( ) {
        saveDirtyTournaments( );
    }

    public ObservableTournament create( ) {
        final Optional<TournamentHeader> pref = _preferences.getFromJson( "tournament.info",
                                                                          TournamentHeader.class );

        final Tournament tournament = new Tournament( );
        tournament.setId( UUID.randomUUID( ) );
        tournament.setKey( UUID.randomUUID( ) );
        tournament.getFlags( ).put( "app.name", _config.get( "app.name" ) );
        tournament.getFlags( ).put( "app.version", _config.get( "version" ) );
        tournament.getFlags( ).put( "app.bundle", _config.bundleType( ) );

        final TournamentHeader header = new TournamentHeader( );
        header.setTitle( "" );
        header.setDate( ZonedDateTime.now( ).truncatedTo( ChronoUnit.HOURS ) );
        header.setStatus( TournamentStatus.NOT_CONFIGURED );
        header.setGame( pref.map( TournamentHeader::getGame ).orElse( "" ) );
        header.setTournamentType( pref.map( TournamentHeader::getTournamentType ).orElse( TournamentType.LOCAL ) );
        header.setScoringMode( pref.map( TournamentHeader::getScoringMode ).orElse( TournamentScoringMode.BY_WINS ) );
        header.setPairingMode( pref.map( TournamentHeader::getPairingMode ).orElse( PairingMode.SWISS ) );
        header.setScoreMax( pref.map( TournamentHeader::getScoreMax ).orElse( 1 ) );
        header.setScoreMaxBis( pref.map( TournamentHeader::getScoreMaxBis ).orElse( 1 ) );
        header.setDoubleScore( pref.map( TournamentHeader::isDoubleScore ).orElse( false ) );
        header.setTeamSize( pref.map( TournamentHeader::getTeamSize ).orElse( 1 ) );
        header.setMatchDuration( pref.map( TournamentHeader::getMatchDuration ).orElse( 30 ) );
        tournament.setHeader( header );

        final Address address = new Address( );
        address.setName( pref.map( t -> t.getAddress( ).getName( ) ).orElse( "" ) );
        address.setLine1( pref.map( t -> t.getAddress( ).getLine1( ) ).orElse( "" ) );
        address.setLine2( pref.map( t -> t.getAddress( ).getLine2( ) ).orElse( "" ) );
        address.setPostalCode( pref.map( t -> t.getAddress( ).getPostalCode( ) ).orElse( "" ) );
        address.setCity( pref.map( t -> t.getAddress( ).getCity( ) ).orElse( "" ) );
        address.setCountry( pref.map( t -> t.getAddress( ).getCountry( ) ).orElse( "" ) );
        header.setAddress( address );

        final ObservableTournament observableTournament = new ObservableTournament( tournament );
        _tournaments.add( observableTournament );
        _storage.saveTournament( observableTournament );
        return observableTournament;
    }

    public ObservableList<ObservableTournament> getTournamentList( ) {
        return _tournaments;
    }

    public void remove( final Tournament tournament ) {
        _tournaments.removeIf( t -> tournament.getId( ).equals( t.getTournament( ).getId( ) ) );
        _storage.deleteTournament( tournament );
    }

    public Optional<ObservableTournament> getTournament( final UUID id ) {
        return _tournaments.stream( )
                           .filter( t -> t.getTournament( ).getId( ).equals( id ) )
                           .findAny( );
    }

    public void delete( final UUID id ) {
        _tournaments.removeIf( t -> t.getTournament( ).getId( ).equals( id ) );
    }

    public void add( final ObservableTournament otournament ) {
        otournament.markDirty( );
        _tournaments.add( otournament );
    }

    private final ObservableList<ObservableTournament> _tournaments =
        synchronizedObservableList( observableArrayList( ) );

    private final ITournamentStorage _storage;
    private final ScheduledExecutorService _scheduler;
    private final Preferences _preferences;
    private final Configuration _config;

    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
