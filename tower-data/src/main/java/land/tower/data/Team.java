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

/**
 * Created on 27/12/2017
 * @author Cédric Longo
 */
public final class Team {

    @SerializedName( "id" )
    private int _id;

    @SerializedName( "active" )
    private boolean _active;

    @SerializedName( "name" )
    private String _name;

    @SerializedName( "players" )
    private List<Player> _players = new ArrayList<>( 1 );

    @SerializedName( "ranking" )
    private Ranking _ranking = new Ranking( );

    public String getName( ) {
        return _name;
    }

    public int getId( ) {
        return _id;
    }

    public void setId( final int id ) {
        _id = id;
    }

    public void setName( final String name ) {
        _name = name;
    }

    public boolean isActive( ) {
        return _active;
    }

    public void setActive( final boolean active ) {
        _active = active;
    }

    public List<Player> getPlayers( ) {
        return _players;
    }

    public void setPlayers( final List<Player> players ) {
        _players = players;
    }

    public Ranking getRanking( ) {
        return _ranking;
    }

    public boolean hasPlayer( final Player p ) {
        return getPlayers( ).stream( ).anyMatch( p2 -> p.getNumero( ) == p2.getNumero( ) );
    }

}
