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

package land.tower.core.view.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import land.tower.core.ext.browser.Browser;
import land.tower.core.ext.config.ConfigurationModule;
import land.tower.core.ext.event.EventModule;
import land.tower.core.ext.i18n.I18nModule;
import land.tower.core.ext.thread.ThreadingModule;
import land.tower.core.ext.updater.VersionInformation;

/**
 * Created on 09/02/2018
 * @author Cédric Longo
 */
class UpdateViewModuleTest {

    @Test
    @DisplayName( "Can inject UpdateDialogModel" )
    void dialogModelInjection( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new UpdateViewModule( ),
                                                        new EventModule( ), new ThreadingModule( ),
                                                        new I18nModule( ), new ConfigurationModule( ),
                                                        mockModule( ) );
        // Exercice
        final UpdateDialogModel instance = injector.getInstance( UpdateDialogModel.class );
        // Verify
        assertThat( instance ).isNotNull( );
    }

    @Test
    @DisplayName( "When open is fired, browser is touched" )
    void dialogOpenTest( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new UpdateViewModule( ),
                                                        new EventModule( ), new ThreadingModule( ),
                                                        new I18nModule( ), new ConfigurationModule( ),
                                                        mockModule( ) );
        final Browser browser = injector.getInstance( Browser.class );
        final UpdateDialogModel instance = injector.getInstance( UpdateDialogModel.class );
        // Exercice
        instance.fireOpenUrl( new VersionInformation( "2018.4", "updateUrl" ) );
        // Verify
        verify( browser ).open( "updateUrl" );
    }

    private Module mockModule( ) {
        return new AbstractModule( ) {
            @Override
            protected void configure( ) {
                bind( Browser.class ).toInstance( mock( Browser.class ) );
            }
        };
    }
}