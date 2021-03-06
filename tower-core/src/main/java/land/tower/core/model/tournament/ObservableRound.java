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

import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import land.tower.data.Round;

/**
 * Created on 30/12/2017
 * @author Cédric Longo
 */
public final class ObservableRound {

    public ObservableRound( final Round round ) {
        _round = round;

        _numero.set( round.getNumero( ) );
        _numero.addListener( ( observable, oldValue, newValue ) -> _round.setNumero( newValue.intValue( ) ) );
        _numero.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _final.set( round.isFinal( ) );
        _final.addListener( ( observable, oldValue, newValue ) -> _round.setFinal( newValue ) );
        _final.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _timer = new ObservableTimer( round.getTimer( ) );
        _timer.dirtyProperty( )
              .addListener( ( observable, oldValue, newValue ) -> _dirty.set( isDirty( ) || newValue ) );

        final ChangeListener<Boolean> matchListener = ( observable, oldValue, newValue ) -> {
            _dirty.set( isDirty( ) || newValue );
            updateEndedStatus( );
        };

        round.getMatches( )
             .forEach( match -> {
                 final ObservableMatch oMatch = new ObservableMatch( match );
                 oMatch.dirtyProperty( ).addListener( matchListener );
                 _matches.add( oMatch );
             } );

        _matches.addListener( (ListChangeListener<ObservableMatch>) c -> {
            _round.getMatches( ).clear( );
            _round.getMatches( ).addAll( _matches.stream( )
                                                 .map( ObservableMatch::getMatch )
                                                 .collect( Collectors.toList( ) ) );
            _matches.forEach( match -> {
                match.dirtyProperty( ).removeListener( matchListener );
                match.dirtyProperty( ).addListener( matchListener );
            } );
        } );
        _matches.addListener( (ListChangeListener<ObservableMatch>) c -> {
            _dirty.set( true );
            updateEndedStatus( );
        } );

        updateEndedStatus( );

        _ended.addListener( ( observable, oldValue, newValue ) -> {
            if ( !oldValue && newValue ) {
                getTimer( ).end( );
            }
        } );
    }

    private void updateEndedStatus( ) {
        _ended.set( _matches.stream( ).allMatch( ObservableMatch::hasScore ) );
    }

    public Round getRound( ) {
        return _round;
    }

    public int getNumero( ) {
        return _numero.get( );
    }

    public SimpleIntegerProperty numeroProperty( ) {
        return _numero;
    }

    public ObservableTimer getTimer( ) {
        return _timer;
    }

    public ObservableList<ObservableMatch> getMatches( ) {
        return _matches;
    }

    public boolean isDirty( ) {
        return _dirty.get( );
    }

    public SimpleBooleanProperty dirtyProperty( ) {
        return _dirty;
    }

    public boolean isEnded( ) {
        updateEndedStatus( );
        return _ended.get( );
    }

    public SimpleBooleanProperty endedProperty( ) {
        return _ended;
    }

    public Optional<ObservableMatch> getMatchFor( final ObservableTeam team ) {
        return _matches.stream( )
                       .filter( m -> m.getLeftTeamId( ) == team.getId( ) || m.getRightTeamId( ) == team.getId( ) )
                       .findAny( );
    }

    public void markAsClean( ) {
        _matches.forEach( ObservableMatch::markAsClean );
        _timer.markAsClean( );
        _dirty.set( false );
    }

    public boolean isFinal( ) {
        return _final.get( );
    }

    public SimpleBooleanProperty finalProperty( ) {
        return _final;
    }

    public void setFinal( final boolean aFinal ) {
        _final.set( aFinal );
    }

    private final Round _round;

    private final SimpleIntegerProperty _numero = new SimpleIntegerProperty( );
    private final ObservableList<ObservableMatch> _matches = FXCollections.observableArrayList( );
    private final ObservableTimer _timer;

    private final SimpleBooleanProperty _ended = new SimpleBooleanProperty( );
    private final SimpleBooleanProperty _final = new SimpleBooleanProperty( );
    private final SimpleBooleanProperty _dirty = new SimpleBooleanProperty( );
}
