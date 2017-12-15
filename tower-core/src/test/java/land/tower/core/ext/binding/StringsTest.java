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

package land.tower.core.ext.binding;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created on 15/12/2017
 * @author Cédric Longo
 */
class StringsTest {

    @Test
    @DisplayName( "Uppercase binding works like expected" )
    void upperCaseStringBinding( ) throws Exception {
        // Setup
        final SimpleStringProperty property = new SimpleStringProperty( "Good bye!" );
        // Exercice
        final StringBinding ucBinding = Strings.toUpperCase( property );
        property.set( "Hi, i'm cédric" );
        // Verify
        assertThat( ucBinding.get( ) ).isEqualTo( "HI, I'M CÉDRIC" );
    }
}