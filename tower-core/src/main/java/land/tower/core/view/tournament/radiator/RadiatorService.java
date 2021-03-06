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

package land.tower.core.view.tournament.radiator;

import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.service.IService;
import land.tower.core.ext.service.ServicePriority;

/**
 * Created on 27/01/2018
 * @author Cédric Longo
 */
final class RadiatorService implements IService {

    @Inject
    RadiatorService( final RadiatorDialogFactory radiatorDialogFactory ) {
        _radiatorDialogFactory = radiatorDialogFactory;
    }

    @Override
    public void start( ) {

    }

    @Override
    public void stop( ) {
        _radiatorDialogFactory.getDialogs( ).forEach( Stage::close );
    }

    @Override
    public ServicePriority getPriority( ) {
        return ServicePriority.LOW;
    }

    private final RadiatorDialogFactory _radiatorDialogFactory;
}
