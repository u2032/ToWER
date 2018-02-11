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

package land.tower.core.ext.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class LevenshteinDistanceTest {

    @Test
    @DisplayName( "Distance is equals to 1 when one char changed" )
    void distanceTest1( ) throws Exception {
        // Setup
        final LevenshteinDistance instance = LevenshteinDistance.getDefaultInstance( );
        // Exercice
        final int dist = instance.apply( "This is a test",
                                         "This is 1 test" );
        // Verify
        assertThat( dist ).isEqualTo( 1 );
    }

    @Test
    @DisplayName( "Distance is equals to 0 when same string" )
    void distanceTest0( ) throws Exception {
        // Setup
        final LevenshteinDistance instance = LevenshteinDistance.getDefaultInstance( );
        // Exercice
        final int dist = instance.apply( "This is a test",
                                         "This is a test" );
        // Verify
        assertThat( dist ).isEqualTo( 0 );
    }

    @Test
    @DisplayName( "Distance returns expected value" )
    void distanceTest2( ) throws Exception {
        // Setup
        final LevenshteinDistance instance = LevenshteinDistance.getDefaultInstance( );
        // Exercice
        final int dist = instance.apply( "This is a test",
                                         "This is not a test" );
        // Verify
        assertThat( dist ).isEqualTo( 4 );
    }
}