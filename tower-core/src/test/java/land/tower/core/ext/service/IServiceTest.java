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

package land.tower.core.ext.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class IServiceTest {

    @Test
    @DisplayName( "Default priority is normal" )
    void defaultPriority( ) throws Exception {
        // Setup
        // Exercice
        final ServicePriority priority = new MyTestService( ).getPriority( );
        // Verify
        assertThat( priority ).isEqualTo( ServicePriority.NORMAL );
    }

    @Test
    @DisplayName( "Default name is the name class" )
    void nameTest( ) throws Exception {
        // Setup
        // Exercice
        final String name = new MyTestService( ).getName( );
        // Verify
        assertThat( name ).isEqualTo( "MyTestService" );
    }

    private static class MyTestService implements IService {

        @Override
        public void start( ) {

        }

        @Override
        public void stop( ) {

        }
    }
}