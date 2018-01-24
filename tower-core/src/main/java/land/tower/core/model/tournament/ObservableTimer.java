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

import java.time.Duration;
import java.time.ZonedDateTime;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import land.tower.data.TimerInfo;

/**
 * Created on 23/01/2018
 * @author Cédric Longo
 */
public final class ObservableTimer {

    public ObservableTimer( final TimerInfo timer ) {
        _timer = timer;

        _initialDuration.set( timer.getInitialDuration( ) );
        _initialDuration.addListener( ( observable, oldValue, newValue ) ->
                                          _timer.setInitialDuration( newValue.intValue( ) ) );
        _initialDuration.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _finalDuration.set( timer.getFinalDuration( ) );
        _finalDuration.addListener( ( observable, oldValue, newValue ) -> _timer.setFinalDuration( newValue ) );
        _finalDuration.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _endTime.set( timer.getEndTime( ) );
        _endTime.addListener( ( observable, oldValue, newValue ) -> _timer.setEndTime( newValue ) );
        _endTime.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        update( );
    }

    public boolean isDirty( ) {
        return _dirty.get( );
    }

    public SimpleBooleanProperty dirtyProperty( ) {
        return _dirty;
    }

    public int getInitialDuration( ) {
        return _initialDuration.get( );
    }

    public SimpleIntegerProperty initialDurationProperty( ) {
        return _initialDuration;
    }

    public Integer getFinalDuration( ) {
        return _finalDuration.get( );
    }

    public SimpleObjectProperty<Integer> finalDurationProperty( ) {
        return _finalDuration;
    }

    public ZonedDateTime getEndTime( ) {
        return _endTime.get( );
    }

    public SimpleObjectProperty<ZonedDateTime> endTimeProperty( ) {
        return _endTime;
    }

    public void markAsClean( ) {
        _dirty.set( false );
    }

    public SimpleStringProperty textProperty( ) {
        return _text;
    }

    public void update( ) {
        if ( getEndTime( ) == null ) {
            // Clock has never started
            _text.setValue( String.format( "%02d:00", getInitialDuration( ) ) );
            _overtime.set( false );
            return;
        }

        if ( getFinalDuration( ) != null ) {
            // Clock has finished, we display the final result
            _overtime.set( getFinalDuration( ) < 0 );
            _text.set( String.format( "%s%02d'", _overtime.get( ) ? "+" : "-", Math.abs( getFinalDuration( ) ) ) );
            return;
        }

        // Clock is on going
        final ZonedDateTime now = ZonedDateTime.now( );
        final Duration duration = Duration.between( now, getEndTime( ) );
        final long minutes = Math.abs( duration.toMinutes( ) );
        final long seconds = Math.abs( duration.getSeconds( ) ) % 60;
        _text.set( String.format( "%s%02d:%02d", duration.isNegative( ) ? "+" : "", minutes, seconds ) );
        _overtime.set( duration.isNegative( ) );
    }

    public void reset( ) {
        _endTime.set( null );
        _finalDuration.set( null );
        _overtime.set( false );
        update( );
    }

    public void end( ) {
        if ( getEndTime( ) == null ) {
            endTimeProperty( ).set( ZonedDateTime.now( ) );
        }
        final Duration duration = Duration.between( ZonedDateTime.now( ), getEndTime( ) );
        _finalDuration.set( (int) duration.toMinutes( ) );
        _overtime.set( duration.isNegative( ) );
        update( );
    }

    public void start( ) {
        _endTime.set( ZonedDateTime.now( ).plusMinutes( _initialDuration.get( ) ) );
    }

    public String getText( ) {
        return _text.get( );
    }

    public boolean isOvertime( ) {
        return _overtime.get( );
    }

    public SimpleBooleanProperty overtimeProperty( ) {
        return _overtime;
    }

    private final TimerInfo _timer;

    private final SimpleBooleanProperty _dirty = new SimpleBooleanProperty( );

    private final SimpleIntegerProperty _initialDuration = new SimpleIntegerProperty( );
    private final SimpleObjectProperty<Integer> _finalDuration = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<ZonedDateTime> _endTime = new SimpleObjectProperty<>( );

    private final SimpleStringProperty _text = new SimpleStringProperty( );
    private final SimpleBooleanProperty _overtime = new SimpleBooleanProperty( );
}
