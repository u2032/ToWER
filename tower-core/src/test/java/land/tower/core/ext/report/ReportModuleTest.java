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

package land.tower.core.ext.report;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import land.tower.core.ext.config.ConfigurationModule;
import land.tower.core.ext.thread.ThreadingModule;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class ReportModuleTest {

    @Test
    @DisplayName( "Can inject ReportEngine in singleton" )
    void reportEngineInjection( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new ReportModule( ), new ConfigurationModule( ),
                                                        new ThreadingModule( ) );
        // Exercice
        final ReportEngine instance = injector.getInstance( ReportEngine.class );
        final ReportEngine instance2 = injector.getInstance( ReportEngine.class );
        // Verify
        assertThat( instance ).isNotNull( );
        assertThat( instance ).isSameAs( instance2 );
    }

    @Test
    @DisplayName( "Can inject LadderReport Factory in singleton" )
    void ladderReportFactoryInjection( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new ReportModule( ), new ConfigurationModule( ),
                                                        new ThreadingModule( ) );
        // Exercice
        final LadderReport.Factory instance = injector.getInstance( LadderReport.Factory.class );
        // Verify
        assertThat( instance ).isNotNull( );
    }

    @Test
    @DisplayName( "Can inject PairingReport Factory in singleton" )
    void pairingReportFactoryInjection( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new ReportModule( ), new ConfigurationModule( ),
                                                        new ThreadingModule( ) );
        // Exercice
        final PairingReport.Factory instance = injector.getInstance( PairingReport.Factory.class );
        // Verify
        assertThat( instance ).isNotNull( );
    }

    @Test
    @DisplayName( "Can inject ResultSlipReport Factory in singleton" )
    void resultSlipReportFactoryInjection( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new ReportModule( ), new ConfigurationModule( ),
                                                        new ThreadingModule( ) );
        // Exercice
        final ResultSlipReport.Factory instance = injector.getInstance( ResultSlipReport.Factory.class );
        // Verify
        assertThat( instance ).isNotNull( );
    }
}