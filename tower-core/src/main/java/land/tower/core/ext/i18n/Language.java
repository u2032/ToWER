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

package land.tower.core.ext.i18n;

/**
 * Created on 31/12/2017
 * @author Cédric Longo
 */
public enum Language {

    EN( "English", "en" ),
    FR( "Français", "fr" );

    Language( final String name, final String code ) {
        _name = name;
        _code = code;
    }

    public String getCode( ) {
        return _code;
    }

    @Override
    public String toString( ) {
        return _name;
    }

    private final String _name;
    private final String _code;
}
