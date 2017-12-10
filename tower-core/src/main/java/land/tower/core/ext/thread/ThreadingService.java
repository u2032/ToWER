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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import land.tower.core.ext.service.IService;

/**
 * Created on 06/12/2017
 * @author Cédric Longo
 */
final class ThreadingService implements IService {

    @Inject
    ThreadingService( final @ApplicationThread ExecutorService executorService,
                      final @ApplicationThread ScheduledExecutorService scheduledExecutorService ) {
        _executorService = executorService;
        _scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public void start( ) {
        // Nothing to do
    }

    @Override
    public void stop( ) {
        _executorService.shutdown( );
        _scheduledExecutorService.shutdown( );
    }

    private final ExecutorService _executorService;
    private final ScheduledExecutorService _scheduledExecutorService;
}
