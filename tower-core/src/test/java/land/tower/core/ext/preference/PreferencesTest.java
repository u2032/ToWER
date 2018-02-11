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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class PreferencesTest {

    @Test
    @DisplayName( "A saved value persists in preference store" )
    void saveTest( ) throws Exception {
        // Setup
        final Preferences pref = new Preferences( );
        // Exercice
        pref.save( "key1", "value1" );
        // Verify
        assertThat( pref.getString( "key1" ).isPresent( ) ).isTrue( );
        assertThat( pref.getString( "key1" ).get( ) ).isEqualTo( "value1" );
    }

    @Test
    @DisplayName( "A saved json value persists in preference store" )
    void saveJsonTest( ) throws Exception {
        // Setup
        final Preferences pref = new Preferences( );
        // Exercice
        pref.saveJson( "key1", new TestObject( "value1" ) );
        // Verify
        final Optional<TestObject> obj = pref.getFromJson( "key1", TestObject.class );
        assertThat( obj.isPresent( ) ).isTrue( );
        assertThat( obj.get( ).value ).isEqualTo( "value1" );
    }

    private static class TestObject {

        private TestObject( final String value ) {
            this.value = value;
        }

        private final String value;
    }
}