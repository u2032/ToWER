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

package land.tower.core.view.tournament.detail.ladder;

import com.google.inject.assistedinject.Assisted;

import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.TournamentStatus;

/**
 * Created on 31/12/2017
 * @author Cédric Longo
 */
public final class CloseTournamentDialogModel {

    public interface Factory {
        CloseTournamentDialogModel forTournament( ObservableTournament tournament );
    }

    @Inject
    public CloseTournamentDialogModel( final I18nTranslator i18n,
                                       final @Assisted ObservableTournament tournament,
                                       final Stage owner ) {
        _i18n = i18n;
        _tournament = tournament;
        _owner = owner;
    }

    public Stage getOwner( ) {
        return _owner;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    void fireCloseTournament( ) {
        _tournament.getHeader( ).setStatus( TournamentStatus.CLOSED );
    }

    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
    private final Stage _owner;
}
