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

package land.tower.core.view.tournament.detail.information;

import com.google.inject.assistedinject.Assisted;

import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 20/12/2017
 * @author Cédric Longo
 */
public final class TournamentInformationTabModel {

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public static interface Factory {

        TournamentInformationTabModel forTournament( final ObservableTournament tournament );
    }

    @Inject
    TournamentInformationTabModel( final @Assisted ObservableTournament tournament, final I18nTranslator i18n ) {
        _i18n = i18n;
        _tournament = tournament;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
}