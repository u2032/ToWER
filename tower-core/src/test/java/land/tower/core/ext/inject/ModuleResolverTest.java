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

package land.tower.core.ext.inject;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created on 15/12/2017
 * @author Cédric Longo
 */
class ModuleResolverTest {

    @Test
    @DisplayName( "Module resolver keeps only expected modules" )
    void overrideTest( ) throws Exception {
        // Setup
        // Exercice
        final List<Module> modules = ModuleResolver.withModules( new ModuleA( ), new ModuleB( ) )
                                                   .override( ModuleA.class, new ModuleC( ) )
                                                   .getModules( );
        // Verify
        assertThat( modules ).hasSize( 2 );
        assertThat( modules ).hasAtLeastOneElementOfType( ModuleC.class );
        assertThat( modules ).hasAtLeastOneElementOfType( ModuleB.class );
    }

    private class ModuleA extends AbstractModule {

        @Override
        protected void configure( ) {

        }
    }

    private class ModuleB extends AbstractModule {

        @Override
        protected void configure( ) {

        }
    }

    private class ModuleC extends AbstractModule {

        @Override
        protected void configure( ) {

        }
    }
}