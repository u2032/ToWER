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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created on 16/12/2017
 * @author Cédric Longo
 */
public final class Tournament {

    @SerializedName( "id" )
    private UUID _id;

    @SerializedName( "key" )
    private UUID _key;

    @SerializedName( "header" )
    private TournamentHeader _header;

    @SerializedName( "teams" )
    private List<Team> _teams = new ArrayList<>( );

    // TODO Round information
    // TODO Final ranking

    public Tournament( ) {

    }

    public UUID getId( ) {
        return _id;
    }

    public UUID getKey( ) {
        return _key;
    }

    public TournamentHeader getHeader( ) {
        return _header;
    }

    public void setId( final UUID id ) {
        _id = id;
    }

    public void setKey( final UUID key ) {
        _key = key;
    }

    public void setHeader( final TournamentHeader header ) {
        _header = header;
    }

    public List<Team> getTeams( ) {
        return _teams;
    }

}
