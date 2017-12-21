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
 * Created on 16/12/2017
 * @author Cédric Longo
 */
public final class Address {

    @SerializedName( "name" )
    public String _name;

    @SerializedName( "line1" )
    public String _line1;

    @SerializedName( "line2" )
    public String _line2;

    @SerializedName( "postalCode" )
    public String _postalCode;

    @SerializedName( "city" )
    public String _city;

    @SerializedName( "country" )
    public String _country;

    public String getName( ) {
        return _name;
    }

    public void setName( final String name ) {
        _name = name;
    }

    public String getLine1( ) {
        return _line1;
    }

    public void setLine1( final String line1 ) {
        _line1 = line1;
    }

    public String getLine2( ) {
        return _line2;
    }

    public void setLine2( final String line2 ) {
        _line2 = line2;
    }

    public String getPostalCode( ) {
        return _postalCode;
    }

    public void setPostalCode( final String postalCode ) {
        _postalCode = postalCode;
    }

    public String getCity( ) {
        return _city;
    }

    public void setCity( final String city ) {
        _city = city;
    }

    public String getCountry( ) {
        return _country;
    }

    public void setCountry( final String country ) {
        _country = country;
    }
}
