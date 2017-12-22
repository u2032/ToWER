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

import java.time.ZonedDateTime;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import land.tower.data.PairingMode;
import land.tower.data.TournamentHeader;
import land.tower.data.TournamentStatus;

/**
 * Created on 17/12/2017
 * @author Cédric Longo
 */
public final class ObservableTournamentHeader {

    public ObservableTournamentHeader( final TournamentHeader header ) {
        _header = header;

        _title = new SimpleStringProperty( header.getTitle( ) );
        _title.addListener( ( obs, oldValue, newValue ) -> header.setTitle( newValue ) );
        _title.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _date = new SimpleObjectProperty<>( header.getDate( ) );
        _date.addListener( ( obs, oldV, newV ) -> header.setDate( newV ) );
        _date.addListener( ( obs, oldV, newV ) -> _dirty.set( true ) );

        _status = new SimpleObjectProperty<>( header.getStatus( ) );
        _status.addListener( ( ( observable, oldValue, newValue ) -> header.setStatus( newValue ) ) );
        _status.addListener( ( ( observable, oldValue, newValue ) -> _dirty.set( true ) ) );

        _pairingMode = new SimpleObjectProperty<>( header.getPairingMode( ) );
        _pairingMode.addListener( ( ( observable, oldValue, newValue ) -> header.setPairingMode( newValue ) ) );
        _pairingMode.addListener( ( ( observable, oldValue, newValue ) -> _dirty.set( true ) ) );

        _matchDuration = new SimpleObjectProperty<>( header.getMatchDuration( ) );
        _matchDuration.addListener( ( obs, oldValue, newValue ) ->
                                        header.setMatchDuration( newValue != null ? newValue : 30 ) );
        _matchDuration.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _teamSize = new SimpleObjectProperty<>( header.getTeamSize( ) );
        _teamSize.addListener( ( obs, oldValue, newValue ) -> header.setTeamSize( newValue != null ? newValue : 1 ) );
        _teamSize.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _address = new ObservableAddress( _header.getAddress( ) );

        _dirty.setValue( false );
        _address.dirtyProperty( )
                .addListener( ( observable, oldValue, newValue ) -> _dirty.set( isDirty( ) || newValue ) );

    }

    public String getTitle( ) {
        return _title.get( );
    }

    public SimpleStringProperty titleProperty( ) {
        return _title;
    }

    public ZonedDateTime getDate( ) {
        return _date.get( );
    }

    public SimpleObjectProperty<ZonedDateTime> dateProperty( ) {
        return _date;
    }

    public void setDate( final ZonedDateTime date ) {
        this._date.set( date );
    }

    public TournamentStatus getStatus( ) {
        return _status.get( );
    }

    public SimpleObjectProperty<TournamentStatus> statusProperty( ) {
        return _status;
    }

    public void setStatus( final TournamentStatus status ) {
        this._status.set( status );
    }

    public PairingMode getPairingMode( ) {
        return _pairingMode.get( );
    }

    public SimpleObjectProperty<PairingMode> pairingModeProperty( ) {
        return _pairingMode;
    }

    public boolean isDirty( ) {
        return _dirty.get( );
    }

    public SimpleBooleanProperty dirtyProperty( ) {
        return _dirty;
    }

    public TournamentHeader getHeader( ) {
        return _header;
    }

    public ObservableAddress getAddress( ) {
        return _address;
    }

    public int getMatchDuration( ) {
        return _matchDuration.get( );
    }

    public SimpleObjectProperty<Integer> matchDurationProperty( ) {
        return _matchDuration;
    }

    public int getTeamSize( ) {
        return _teamSize.get( );
    }

    public SimpleObjectProperty<Integer> teamSizeProperty( ) {
        return _teamSize;
    }

    private final SimpleStringProperty _title;
    private final SimpleObjectProperty<ZonedDateTime> _date;
    private final SimpleObjectProperty<TournamentStatus> _status;
    private final SimpleObjectProperty<PairingMode> _pairingMode;
    private final SimpleObjectProperty<Integer> _matchDuration;
    private final SimpleObjectProperty<Integer> _teamSize;
    private final ObservableAddress _address;

    private final SimpleBooleanProperty _dirty = new SimpleBooleanProperty( );

    private final TournamentHeader _header;
}
