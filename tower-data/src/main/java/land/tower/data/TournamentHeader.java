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

package land.tower.data;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

/**
 * Created on 16/12/2017
 * @author Cédric Longo
 */
public final class TournamentHeader {

    @SerializedName( "status" )
    private TournamentStatus _status;

    @SerializedName( "title" )
    private String _title;

    @SerializedName( "date" )
    private LocalDateTime _date;

    @SerializedName( "pairingMode" )
    private PairingMode _pairingMode;

    @SerializedName( "address" )
    private Address _address;

    @SerializedName( "mainJudge" )
    private Player _mainJudge;

    @SerializedName( "judges" )
    private Player _judges;

    @SerializedName( "kValue" )
    private int _kValue;

    public TournamentStatus getStatus( ) {
        return _status;
    }

    public void setStatus( final TournamentStatus status ) {
        _status = status;
    }

    public String getTitle( ) {
        return _title;
    }

    public void setTitle( final String title ) {
        _title = title;
    }

    public LocalDateTime getDate( ) {
        return _date;
    }

    public void setDate( final LocalDateTime date ) {
        _date = date;
    }

    public PairingMode getPairingMode( ) {
        return _pairingMode;
    }

    public void setPairingMode( final PairingMode pairingMode ) {
        _pairingMode = pairingMode;
    }

    public Address getAddress( ) {
        return _address;
    }

    public void setAddress( final Address address ) {
        _address = address;
    }

    public Player getMainJudge( ) {
        return _mainJudge;
    }

    public void setMainJudge( final Player mainJudge ) {
        _mainJudge = mainJudge;
    }

    public Player getJudges( ) {
        return _judges;
    }

    public void setJudges( final Player judges ) {
        _judges = judges;
    }

    public int getkValue( ) {
        return _kValue;
    }

    public void setkValue( final int kValue ) {
        _kValue = kValue;
    }
}
