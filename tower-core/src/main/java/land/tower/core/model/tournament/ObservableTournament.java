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

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import land.tower.data.Match;
import land.tower.data.Teams;
import land.tower.data.Tournament;
import land.tower.data.TournamentStatus;

/**
 * Created on 16/12/2017
 * @author Cédric Longo
 */
public final class ObservableTournament {

    public ObservableTournament( final Tournament tournament ) {
        _tournament = tournament;
        _header = new ObservableTournamentHeader( tournament.getHeader( ) );
        _header.dirtyProperty( )
               .addListener( ( observable, oldValue, newValue ) -> _dirty.set( isDirty( ) || newValue ) );
        _header.dateProperty( ).addListener( ( observable, oldValue, newValue ) -> updateStatus( ) );

        tournament.getTeams( )
                  .forEach( team -> {
                      final ObservableTeam oTeam = new ObservableTeam( team );
                      oTeam.dirtyProperty( )
                           .addListener( ( observable, oldValue, newValue ) -> _dirty.set( isDirty( ) || newValue ) );
                      _teams.add( oTeam );
                  } );
        _teams.addListener( (ListChangeListener<ObservableTeam>) c -> {
            _tournament.getTeams( ).clear( );
            _tournament.getTeams( ).addAll( _teams.stream( )
                                                  .map( ObservableTeam::getTeam )
                                                  .collect( Collectors.toList( ) ) );
        } );
        _teams.addListener( (ListChangeListener<ObservableTeam>) c -> _dirty.set( true ) );
        _teams.addListener( (ListChangeListener<ObservableTeam>) c -> updateStatus( ) );

        tournament.getRounds( )
                  .forEach( round -> {
                      final ObservableRound oRound = new ObservableRound( round );
                      oRound.dirtyProperty( )
                            .addListener( ( observable, oldValue, newValue ) -> _dirty.set( isDirty( ) || newValue ) );
                      _rounds.add( oRound );
                  } );
        _rounds.addListener( (ListChangeListener<ObservableRound>) c -> {
            _tournament.getRounds( ).clear( );
            _tournament.getRounds( ).addAll( _rounds.stream( )
                                                    .map( ObservableRound::getRound )
                                                    .collect( Collectors.toList( ) ) );
        } );
        _rounds.addListener( (ListChangeListener<ObservableRound>) c -> _dirty.set( true ) );
        _rounds.addListener( (ListChangeListener<ObservableRound>) c -> updateStatus( ) );
        _rounds.addListener( (ListChangeListener<ObservableRound>) c -> updateLastRound( ) );

        updateLastRound( );

        _dirty.setValue( false );
    }

    private void updateLastRound( ) {
        final Optional<ObservableRound> lastRound =
            getRounds( ).stream( )
                        .sorted( Comparator.comparing( ObservableRound::getNumero, Comparator.reverseOrder( ) ) )
                        .findFirst( );

        lastRound.ifPresent( _currentRound::set );
    }

    public Tournament getTournament( ) {
        return _tournament;
    }

    public ObservableTournamentHeader getHeader( ) {
        return _header;
    }

    public ObservableList<ObservableTeam> getTeams( ) {
        return _teams;
    }

    public void registerTeam( final ObservableTeam team ) {
        _teams.add( team );
        _rounds.forEach( round -> {
            final int maxPosition = round.getMatches( ).stream( )
                                         .mapToInt( ObservableMatch::getPosition )
                                         .max( )
                                         .orElse( 0 );

            final Match match = new Match( );
            match.setPosition( maxPosition + 1 );
            match.setLeftTeamId( team.getId( ) );
            match.setRightTeamId( Teams.BYE_TEAM.getId( ) );
            match.setScoreLeft( 0 );
            match.setScoreDraw( 0 );
            match.setScoreRight( _tournament.getHeader( ).getWinningGameCount( ) );
            round.getMatches( ).add( new ObservableMatch( match ) );
        } );
    }

    public ObservableList<ObservableRound> getRounds( ) {
        return _rounds;
    }

    public void registerRound( final ObservableRound round ) {
        _rounds.add( round );
    }

    public boolean isDirty( ) {
        return _dirty.get( );
    }

    public SimpleBooleanProperty dirtyProperty( ) {
        return _dirty;
    }

    public void updateStatus( ) {
        TournamentStatus current = getHeader( ).getStatus( );
        if ( current == TournamentStatus.CLOSED ) {
            return;
        }

        current = TournamentStatus.PLANNED;

        if ( !_teams.isEmpty( ) ) {
            current = TournamentStatus.ENROLMENT;
        }

        if ( !_rounds.isEmpty( ) ) {
            current = TournamentStatus.STARTED;
        }

        getHeader( ).setStatus( current );
    }

    public ObservableTeam getTeam( final int teamId ) {
        if ( teamId == Teams.BYE_TEAM.getId( ) ) {
            return ObservableTeam.BYE_TEAM;
        }
        return _teams.stream( ).filter( t -> t.getId( ) == teamId ).findAny( )
                     .orElseThrow( IllegalStateException::new );
    }

    public ObservableRound getCurrentRound( ) {
        return _currentRound.get( );
    }

    public SimpleObjectProperty<ObservableRound> currentRoundProperty( ) {
        return _currentRound;
    }

    public void markAsClean( ) {
        getHeader( ).markAsClean( );
        getRounds( ).forEach( ObservableRound::markAsClean );
        getTeams( ).forEach( ObservableTeam::markAsClean );
        _dirty.set( false );
    }

    public void markDirty( ) {
        _dirty.set( true );
    }

    private final Tournament _tournament;
    private final ObservableTournamentHeader _header;
    private final ObservableList<ObservableTeam> _teams = FXCollections.observableArrayList( );
    private final ObservableList<ObservableRound> _rounds = FXCollections.observableArrayList( );

    private final SimpleObjectProperty<ObservableRound> _currentRound = new SimpleObjectProperty<>( );

    private final SimpleBooleanProperty _dirty = new SimpleBooleanProperty( );
}
