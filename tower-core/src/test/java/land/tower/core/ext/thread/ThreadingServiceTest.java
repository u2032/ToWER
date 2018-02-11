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

package land.tower.core.ext.thread;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class ThreadingServiceTest {

    @Inject
    @ApplicationThread
    private ExecutorService _executorService;

    @Inject
    @ApplicationThread
    private ScheduledExecutorService _scheduledExecutorService;

    @Inject
    private ThreadingService _service;

    @BeforeEach
    void setUp( ) {
        Guice.createInjector( mockModule( ) )
             .injectMembers( this );
    }

    private Module mockModule( ) {
        return new AbstractModule( ) {
            @Override
            protected void configure( ) {
                bind( ThreadingService.class );
                bind( ExecutorService.class ).annotatedWith( ApplicationThread.class )
                                             .toInstance( mock( ExecutorService.class ) );
                bind( ScheduledExecutorService.class ).annotatedWith( ApplicationThread.class )
                                                      .toInstance( mock( ScheduledExecutorService.class ) );
            }
        };
    }

    @Test
    @DisplayName( "When service is closed, application executors are shutdown" )
    void shutdownTest( ) throws Exception {
        // Setup
        // Exercice
        _service.stop( );
        // Verify
        verify( _executorService ).shutdown( );
        verify( _scheduledExecutorService ).shutdown( );
    }
}