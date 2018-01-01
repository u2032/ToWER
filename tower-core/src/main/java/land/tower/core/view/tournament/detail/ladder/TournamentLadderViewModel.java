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

import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 01/01/2018
 * @author Cédric Longo
 */
public final class TournamentLadderViewModel {

    public interface Factory {

        TournamentLadderViewModel forTournament( ObservableTournament tournament );

    }

    @Inject
    public TournamentLadderViewModel( @Assisted final ObservableTournament tournament,
                                      final I18nTranslator i18n ) {
        _tournament = tournament;
        _i18n = i18n;
    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    private final ObservableTournament _tournament;
    private final I18nTranslator _i18n;
}
