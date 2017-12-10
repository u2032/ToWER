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

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import land.tower.core.ext.service.IService;

/**
 * Created on 04/12/2017
 * @author Cédric Longo
 */
public final class ThreadingModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( ExecutorService.class ).annotatedWith( ApplicationThread.class )
                                     .toInstance( Executors.newFixedThreadPool(
                                         Runtime.getRuntime( ).availableProcessors( ) * 2 ) );

        bind( ScheduledExecutorService.class ).annotatedWith( ApplicationThread.class )
                                              .toInstance( Executors.newScheduledThreadPool(
                                                  Runtime.getRuntime( ).availableProcessors( ) ) );

        Multibinder.newSetBinder( binder( ), IService.class )
                   .addBinding( ).to( ThreadingService.class );
    }
}
