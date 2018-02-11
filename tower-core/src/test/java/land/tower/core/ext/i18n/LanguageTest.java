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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class LanguageTest {

    @Test
    @DisplayName( "fromCode returns expected language" )
    void fromCodeTest( ) throws Exception {
        // Setup
        // Exercice
        final Optional<Language> lang = Language.fromCode( "en_US" );
        // Verify
        assertThat( lang.isPresent( ) ).isTrue( );
        assertThat( lang.get( ) ).isEqualTo( Language.EN );
    }

    @Test
    @DisplayName( "fromCode returns null if null code" )
    void fromCodeNullTest( ) throws Exception {
        // Setup
        // Exercice
        final Optional<Language> lang = Language.fromCode( null );
        // Verify
        assertThat( lang.isPresent( ) ).isFalse( );
    }

    @Test
    @DisplayName( "toString returns localized name" )
    void toStringTest( ) throws Exception {
        // Setup
        // Exercice
        final String lang = Language.FR.toString( );
        // Verify
        assertThat( lang ).isEqualTo( "Français" );
    }
}