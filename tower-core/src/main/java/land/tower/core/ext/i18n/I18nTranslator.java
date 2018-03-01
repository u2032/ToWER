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

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created on 12/11/2017
 * @author Cédric Longo
 */
public final class I18nTranslator {

    public StringProperty get( final String key ) {
        return _texts.computeIfAbsent( key, SimpleStringProperty::new );
    }

    void registerEntries( final Properties entries ) {
        entries.stringPropertyNames( )
               .forEach( key -> {
                   _texts.computeIfAbsent( key, SimpleStringProperty::new )
                         .setValue( entries.getProperty( key ) );
               } );
    }

    public String get( final String key, final Object... args ) {
        return String.format( get( key ).get( ), args );
    }

    public boolean has( final String key ) {
        return _texts.containsKey( key ) && !_texts.get( key ).get( ).equals( key );
    }

    private final Map<String, StringProperty> _texts = Collections.synchronizedMap( Maps.newHashMap( ) );
}
