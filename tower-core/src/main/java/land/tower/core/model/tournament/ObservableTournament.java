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

import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

        _dirty.setValue( false );
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
        // TODO has round => STARTED
        getHeader( ).setStatus( current );
    }

    private final Tournament _tournament;
    private final ObservableTournamentHeader _header;
    private final ObservableList<ObservableTeam> _teams = FXCollections.observableArrayList( );

    private final SimpleBooleanProperty _dirty = new SimpleBooleanProperty( );
}
