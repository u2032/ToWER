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

/**
 * Created on 25/11/2017
 * @author Cédric Longo
 */
public final class Player {

    @SerializedName( "numero" )
    private long _numero;

    @SerializedName( "firstname" )
    private String _firstname;

    @SerializedName( "lastname" )
    private String _lastname;

    @SerializedName( "birthday" )
    private String _birthday;

    @SerializedName( "nationality" )
    private PlayerNationality _nationality;

    /**
     * @deprecated Should be used only for deserialization
     */
    @Deprecated
    public Player( ) {
    }

    public Player( final long numero, final String firstname, final String lastname, final String birthday,
                   final PlayerNationality nationality ) {
        _numero = numero;
        _firstname = firstname;
        _lastname = lastname;
        _birthday = birthday;
        _nationality = nationality;
    }

    public long getNumero( ) {
        return _numero;
    }

    public String getFirstname( ) {
        return _firstname;
    }

    public String getLastname( ) {
        return _lastname;
    }

    public String getBirthday( ) {
        return _birthday;
    }

    public PlayerNationality getNationality( ) {
        return _nationality;
    }

    @Override
    public String toString( ) {
        return "Player{" +
               "_numero=" + _numero +
               ", _firstname='" + _firstname + '\'' +
               ", _lastname='" + _lastname + '\'' +
               ", _birthday='" + _birthday + '\'' +
               ", _nationality=" + _nationality +
               '}';
    }
}
