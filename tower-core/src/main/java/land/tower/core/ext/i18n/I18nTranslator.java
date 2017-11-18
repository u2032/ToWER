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

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Properties;

/**
 * Created on 12/11/2017
 * @author Cédric Longo
 */
public final class I18nTranslator {

    public String get( final String key ) {
        return _texts.getOrDefault( key, key );
    }

    void registerEntries( final Properties entries ) {
        entries.stringPropertyNames( )
               .forEach( key -> _texts.put( key, entries.getProperty( key ) ) );
    }

    private final Map<String, String> _texts = Maps.newHashMap( );
}
