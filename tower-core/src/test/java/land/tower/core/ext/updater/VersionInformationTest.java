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

package land.tower.core.ext.updater;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Created on 07/02/2018
 * @author Cédric Longo
 */
class VersionInformationTest {

    @Test
    @DisplayName( "Basic version can be compared" )
    void simpleVersionTest( ) throws Exception {
        // Setup
        final VersionInformation version = new VersionInformation( );
        version.setVersion( "2018.2" );
        // Exercice
        // Verify
        assertThat( version.isGreaterThan( "2018.1" ) ).isTrue( );
        assertThat( version.isGreaterThan( "2018.3" ) ).isFalse( );
    }

    @Test
    @DisplayName( "Version with classifier can be compared" )
    void classifierVersionTest( ) throws Exception {
        // Setup
        final VersionInformation version = new VersionInformation( );
        version.setVersion( "2018.2-RC3" );
        // Exercice
        // Verify
        assertThat( version.isGreaterThan( "2018.2-RC2" ) ).isTrue( );
        assertThat( version.isGreaterThan( "2018.2-RC4" ) ).isFalse( );
    }

    @Test
    @DisplayName( "Version mixed can be compared" )
    void mixedVersionTest( ) throws Exception {
        // Setup
        final VersionInformation version = new VersionInformation( );
        version.setVersion( "2018.2-RC3" );
        // Exercice
        // Verify
        assertThat( version.isGreaterThan( "2018.3" ) ).isFalse( );
        assertThat( version.isGreaterThan( "2018.2" ) ).isFalse( );
        assertThat( version.isGreaterThan( "2018.1" ) ).isTrue( );
    }
}