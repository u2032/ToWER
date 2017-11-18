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

package land.tower.core.view.main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.inject.Inject;

/**
 * Created on 18/11/2017
 * @author Cédric Longo
 */
final class ApplicationStatusBarModel {

    private final StringProperty _taskInfo = new SimpleStringProperty( );
    private final StringProperty _stateInfo = new SimpleStringProperty( );

    @Inject
    public ApplicationStatusBarModel( ) {

    }

    StringProperty taskInfoProperty( ) {
        return _taskInfo;
    }

    StringProperty stateInfoProperty( ) {
        return _stateInfo;
    }
}
