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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import land.tower.core.ext.config.ConfigurationModule;
import land.tower.core.ext.preference.PreferenceModule;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class I18nServiceTest {

    @Inject
    private I18nService _service;

    @BeforeEach
    void setUp( ) {
        Guice.createInjector( new I18nModule( ), new ConfigurationModule( ), new PreferenceModule( ) )
             .injectMembers( this );
    }

    @Test
    @DisplayName( "When service is started, translator is populated with texts" )
    void name( ) throws Exception {
        // Setup
        // Exercice
        _service.start( );
        // Verify
        assertThat( _service.get( ).has( "language" ) );
        assertThat( _service.get( ).get( "language" ) ).isNotEqualTo( "language" );
    }
}