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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.stage.Window;
import javax.inject.Inject;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.tournament.radiator.RadiatorDialogModel.Factory;

/**
 * Created on 27/01/2018
 * @author Cédric Longo
 */
public final class RadiatorDialogFactory {

    @Inject
    public RadiatorDialogFactory( final Factory factory ) {
        _factory = factory;
    }

    public RadiatorDialog create( final Window window, final ObservableTournament tournament ) {
        final RadiatorDialog dialog = new RadiatorDialog( window, _factory.forTournament( tournament ) );
        dialog.setOnCloseRequest( e -> _dialogs.remove( dialog ) );
        _dialogs.add( dialog );
        return dialog;
    }

    public List<RadiatorDialog> getDialogs( ) {
        return _dialogs;
    }

    private final List<RadiatorDialog> _dialogs = Collections.synchronizedList( new ArrayList<>( ) );
    private final RadiatorDialogModel.Factory _factory;
}
