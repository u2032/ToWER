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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import land.tower.core.ext.service.IService;

/**
 * Created on 15/12/2017
 * @author Cédric Longo
 */
class I18nModuleTest {

    @Test
    @DisplayName( "I18nTranslator can be injected as singleton" )
    void can_inject_I18nTranslator( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new I18nModule( ) );
        // Exercice
        final I18nTranslator instance = injector.getInstance( I18nTranslator.class );
        final I18nTranslator instance2 = injector.getInstance( I18nTranslator.class );
        // Verify
        assertThat( instance ).isNotNull( );
        assertThat( instance ).isSameAs( instance2 );
    }

    @Test
    @DisplayName( "I18nService can be injected as singleton" )
    void can_inject_I18nService( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new I18nModule( ) );
        // Exercice
        final I18nService instance = injector.getInstance( I18nService.class );
        final I18nService instance2 = injector.getInstance( I18nService.class );
        // Verify
        assertThat( instance ).isNotNull( );
        assertThat( instance ).isSameAs( instance2 );
    }

    @Test
    @DisplayName( "I18nService is registered as Service" )
    void I18nService_is_registered( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new I18nModule( ) );
        // Exercice
        final Set<IService> instance = injector.getInstance( Key.get( new TypeLiteral<Set<IService>>( ) {
        } ) );
        // Verify
        assertThat( instance ).hasAtLeastOneElementOfType( I18nService.class );
    }
}