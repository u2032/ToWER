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

package land.tower.core.ext.preference;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

/**
 * Created on 04/01/2018
 * @author Cédric Longo
 */
public final class Preferences {

    public Preferences( ) {
    }

    public Optional<String> getString( final String key ) {
        return Optional.ofNullable( _items.get( key ) );
    }

    public void save( final String key, final Object value ) {
        _items.put( key, value.toString( ) );
    }

    public Map<String, String> getItems( ) {
        return _items;
    }

    private final Map<String, String> _items = Maps.newConcurrentMap( );
}
