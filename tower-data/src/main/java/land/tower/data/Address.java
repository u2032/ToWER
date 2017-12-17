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

}
