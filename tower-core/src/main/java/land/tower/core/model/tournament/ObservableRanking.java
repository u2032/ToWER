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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import land.tower.data.Ranking;

/**
 * Created on 01/01/2018
 * @author Cédric Longo
 */
public final class ObservableRanking {

    public ObservableRanking( final Ranking ranking ) {
        _ranking = ranking;

        _rank.set( ranking.getRank( ) );
        _rank.addListener( ( observable, oldValue, newValue ) -> _ranking.setRank( newValue ) );
        _rank.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _points.set( ranking.getPoints( ) );
        _points.addListener( ( observable, oldValue, newValue ) -> _ranking.setPoints( newValue ) );
        _points.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _d1.set( ranking.getD1( ) );
        _d1.addListener( ( observable, oldValue, newValue ) -> _ranking.setD1( newValue ) );
        _d1.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _d2.set( ranking.getD2( ) );
        _d2.addListener( ( observable, oldValue, newValue ) -> _ranking.setD2( newValue ) );
        _d2.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _d3.set( ranking.getD3( ) );
        _d3.addListener( ( observable, oldValue, newValue ) -> _ranking.setD3( newValue ) );
        _d3.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _d4.set( ranking.getD4( ) );
        _d4.addListener( ( observable, oldValue, newValue ) -> _ranking.setD4( newValue ) );
        _d4.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );
    }

    public Ranking getRanking( ) {
        return _ranking;
    }

    public Integer getRank( ) {
        return _rank.get( );
    }

    public SimpleObjectProperty<Integer> rankProperty( ) {
        return _rank;
    }

    public Integer getPoints( ) {
        return _points.get( );
    }

    public SimpleObjectProperty<Integer> pointsProperty( ) {
        return _points;
    }

    public Integer getD1( ) {
        return _d1.get( );
    }

    public SimpleObjectProperty<Integer> d1Property( ) {
        return _d1;
    }

    public Integer getD2( ) {
        return _d2.get( );
    }

    public SimpleObjectProperty<Integer> d2Property( ) {
        return _d2;
    }

    public Integer getD3( ) {
        return _d3.get( );
    }

    public SimpleObjectProperty<Integer> d3Property( ) {
        return _d3;
    }

    public Integer getD4( ) {
        return _d4.get( );
    }

    public SimpleObjectProperty<Integer> d4Property( ) {
        return _d4;
    }

    public void setRank( final int rank ) {
        this._rank.set( rank );
    }

    public void setPoints( final int points ) {
        this._points.set( points );
    }

    public void setD1( final int d1 ) {
        this._d1.set( d1 );
    }

    public void setD2( final int d2 ) {
        this._d2.set( d2 );
    }

    public void setD3( final int d3 ) {
        this._d3.set( d3 );
    }

    public void setD4( final int d4 ) {
        this._d4.set( d4 );
    }

    public void setDirty( final boolean dirty ) {
        this._dirty.set( dirty );
    }

    public boolean isDirty( ) {
        return _dirty.get( );
    }

    public SimpleBooleanProperty dirtyProperty( ) {
        return _dirty;
    }

    private final Ranking _ranking;

    private final SimpleObjectProperty<Integer> _rank = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _points = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _d1 = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _d2 = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _d3 = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _d4 = new SimpleObjectProperty<>( );

    private final SimpleBooleanProperty _dirty = new SimpleBooleanProperty( );
}
