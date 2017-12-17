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

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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

        _date = new SimpleStringProperty( header.getDate( ) );
        _date.addListener( ( obs, oldV, newV ) -> header.setDate( newV ) );

        _status = new SimpleObjectProperty<>( header.getStatus( ) );
        _status.addListener( ( ( observable, oldValue, newValue ) -> header.setStatus( newValue ) ) );
    }

    public String getTitle( ) {
        return _title.get( );
    }

    public SimpleStringProperty titleProperty( ) {
        return _title;
    }

    public String getDate( ) {
        return _date.get( );
    }

    public SimpleStringProperty dateProperty( ) {
        return _date;
    }

    public void setDate( final String date ) {
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

    private final SimpleStringProperty _title;
    private final SimpleStringProperty _date;
    private final SimpleObjectProperty<TournamentStatus> _status;

    private final TournamentHeader _header;
}
