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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * Created on 18/11/2017
 * @author Cédric Longo
 */
class I18nTranslatorTest {

    @BeforeEach
    void setUp( ) {
        _i18nTranslator = new I18nTranslator( );
    }

    @Test
    @DisplayName( "Getting text from key return expected value" )
    void getFromKey( ) throws Exception {
        // Setup
        final Properties properties = new Properties(  );
        properties.setProperty( "key1", "value1" );
        properties.setProperty( "key2", "value2" );
        _i18nTranslator.registerEntries( properties );
        // Exercice
        final String value = _i18nTranslator.get( "key1" ).getValue( );
        // Verify
        assertThat(value).isEqualTo( "value1" );
    }


    @Test
    @DisplayName( "Getting text from an unknown key return the key itself" )
    void getUnknownKey( ) throws Exception {
        // Setup
        final Properties properties = new Properties(  );
        properties.setProperty( "key1", "value1" );
        properties.setProperty( "key2", "value2" );
        _i18nTranslator.registerEntries( properties );
        // Exercice
        final String value = _i18nTranslator.get( "key3" ).getValue( );
        // Verify
        assertThat(value).isEqualTo( "key3" );
    }

    @Test
    @DisplayName( "Getting text with parameters replaced correctly placeholders" )
    void getWithParamaters( ) throws Exception {
        // Setup
        final Properties properties = new Properties( );
        properties.setProperty( "key1", "value %1$s of %2$s or %1$s" );
        properties.setProperty( "key2", "value2" );
        _i18nTranslator.registerEntries( properties );
        // Exercice
        final String value = _i18nTranslator.get( "key1", "none", "any" );
        // Verify
        assertThat( value ).isEqualTo( "value none of any or none" );
    }

    @Test
    @DisplayName( "Has method returns true is translator has key" )
    void hasTest( ) throws Exception {
        // Setup
        final Properties properties = new Properties( );
        properties.setProperty( "key1", "value %1$s of %2$s or %1$s" );
        properties.setProperty( "key2", "value2" );
        _i18nTranslator.registerEntries( properties );
        // Exercice
        // Verify
        assertThat( _i18nTranslator.has( "key1" ) ).isTrue( );
        assertThat( _i18nTranslator.has( "key3" ) ).isFalse( );
    }

    private I18nTranslator _i18nTranslator;
}