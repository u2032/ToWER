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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import land.tower.data.Match;

/**
 * Created on 30/12/2017
 * @author Cédric Longo
 */
public final class ObservableMatch {

    public ObservableMatch( final Match match ) {
        _match = match;

        _position.set( match.getPosition( ) );
        _position.addListener( ( observable, oldValue, newValue ) -> match.setPosition( newValue.intValue( ) ) );
        _position.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _scoreLeft.set( _match.getScoreLeft( ) );
        _scoreLeft.addListener( ( observable, oldValue, newValue ) -> match.setScoreLeft( newValue.intValue( ) ) );
        _scoreLeft.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _scoreDraw.set( _match.getScoreDraw( ) );
        _scoreDraw.addListener( ( observable, oldValue, newValue ) -> match.setScoreDraw( newValue.intValue( ) ) );
        _scoreDraw.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _scoreRight.set( _match.getScoreRight( ) );
        _scoreRight.addListener( ( observable, oldValue, newValue ) -> match.setScoreRight( newValue.intValue( ) ) );
        _scoreRight.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _leftTeamId.set( _match.getLeftTeamId( ) );
        _leftTeamId.addListener( ( observable, oldValue, newValue ) -> match.setLeftTeamId( newValue.intValue( ) ) );
        _leftTeamId.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _rightTeamId.set( _match.getRightTeamId( ) );
        _rightTeamId.addListener( ( observable, oldValue, newValue ) -> match.setRightTeamId( newValue.intValue( ) ) );
        _rightTeamId.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _hasScore.bind( Bindings.createBooleanBinding( ( ) -> {
            return _scoreLeft.get( ) != 0 || _scoreDraw.get( ) != 0 || _scoreRight.get( ) != 0;
        }, _scoreLeft, _scoreRight, _scoreDraw ) );
    }

    public Match getMatch( ) {
        return _match;
    }

    public int getPosition( ) {
        return _position.get( );
    }

    public SimpleIntegerProperty positionProperty( ) {
        return _position;
    }

    public int getLeftTeamId( ) {
        return _leftTeamId.get( );
    }

    public SimpleIntegerProperty leftTeamIdProperty( ) {
        return _leftTeamId;
    }

    public int getRightTeamId( ) {
        return _rightTeamId.get( );
    }

    public SimpleIntegerProperty rightTeamIdProperty( ) {
        return _rightTeamId;
    }

    public int getScoreLeft( ) {
        return _scoreLeft.get( );
    }

    public SimpleIntegerProperty scoreLeftProperty( ) {
        return _scoreLeft;
    }

    public int getScoreDraw( ) {
        return _scoreDraw.get( );
    }

    public SimpleIntegerProperty scoreDrawProperty( ) {
        return _scoreDraw;
    }

    public int getScoreRight( ) {
        return _scoreRight.get( );
    }

    public SimpleIntegerProperty scoreRightProperty( ) {
        return _scoreRight;
    }

    public boolean isDirty( ) {
        return _dirty.get( );
    }

    public SimpleBooleanProperty dirtyProperty( ) {
        return _dirty;
    }

    public boolean hasScore( ) {
        return _hasScore.get( );
    }

    public SimpleBooleanProperty hasScoreProperty( ) {
        return _hasScore;
    }

    public void setPosition( final int position ) {
        _position.set( position );
    }

    public boolean hasWon( final int teamId ) {
        final boolean isLeft = ( _leftTeamId.get( ) == teamId );
        final boolean isRight = ( _rightTeamId.get( ) == teamId );
        if ( isLeft ) {
            return _scoreLeft.get( ) > _scoreRight.get( );
        }
        if ( isRight ) {
            return _scoreRight.get( ) > _scoreLeft.get( );
        }
        throw new IllegalArgumentException( "The team " + teamId + " has not played in this match" );

    }

    public boolean hasWon( final ObservableTeam team ) {
        return hasWon( team.getId( ) );
    }

    public boolean hasLost( final ObservableTeam team ) {
        final boolean isLeft = ( _leftTeamId.get( ) == team.getId( ) );
        final boolean isRight = ( _rightTeamId.get( ) == team.getId( ) );
        if ( isLeft ) {
            return _scoreLeft.get( ) < _scoreRight.get( );
        }
        if ( isRight ) {
            return _scoreRight.get( ) < _scoreLeft.get( );
        }
        throw new IllegalArgumentException( "The team " + team.getId( ) + " has not played in this match" );
    }

    public boolean isDraw( ) {
        return _scoreLeft.get( ) == _scoreRight.get( );
    }

    public int getOpponentId( final ObservableTeam team ) {
        final boolean isLeft = ( _leftTeamId.get( ) == team.getId( ) );
        final boolean isRight = ( _rightTeamId.get( ) == team.getId( ) );
        if ( isLeft ) {
            return _rightTeamId.get( );
        }
        if ( isRight ) {
            return _leftTeamId.get( );
        }
        throw new IllegalArgumentException( "The team " + team.getId( ) + " has not played in this match" );
    }

    public int getScore( final ObservableTeam team ) {
        final boolean isLeft = ( _leftTeamId.get( ) == team.getId( ) );
        final boolean isRight = ( _rightTeamId.get( ) == team.getId( ) );
        if ( isLeft ) {
            return getScoreLeft( );
        }
        if ( isRight ) {
            return getScoreRight( );
        }
        throw new IllegalArgumentException( "The team " + team.getId( ) + " has not played in this match" );
    }

    public void markAsClean( ) {
        _dirty.set( false );
    }

    private final Match _match;

    private final SimpleIntegerProperty _position = new SimpleIntegerProperty( );
    private final SimpleIntegerProperty _leftTeamId = new SimpleIntegerProperty( );
    private final SimpleIntegerProperty _rightTeamId = new SimpleIntegerProperty( );
    private final SimpleIntegerProperty _scoreLeft = new SimpleIntegerProperty( );
    private final SimpleIntegerProperty _scoreDraw = new SimpleIntegerProperty( );
    private final SimpleIntegerProperty _scoreRight = new SimpleIntegerProperty( );

    private final SimpleBooleanProperty _hasScore = new SimpleBooleanProperty( );

    private final SimpleBooleanProperty _dirty = new SimpleBooleanProperty( );
}
