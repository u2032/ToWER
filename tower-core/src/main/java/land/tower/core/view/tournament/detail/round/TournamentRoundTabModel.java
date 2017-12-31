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

package land.tower.core.view.tournament.detail.round;

import com.google.inject.assistedinject.Assisted;

import javafx.beans.property.SimpleBooleanProperty;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 30/12/2017
 * @author Cédric Longo
 */
public final class TournamentRoundTabModel {

    public interface Factory {

        TournamentRoundTabModel create( ObservableTournament tournament, ObservableRound round );

    }
    @Inject
    public TournamentRoundTabModel( @Assisted ObservableTournament tournament,
                                    @Assisted ObservableRound round,
                                    final I18nTranslator i18n,
                                    final SetScoreDialogModel.Factory setScoreDialogFactory ) {
        _tournament = tournament;
        _round = round;
        _i18n = i18n;
        _setScoreDialogFactory = setScoreDialogFactory;
    }

    public SimpleBooleanProperty filterNotEmptyScoreProperty( ) {
        return _filterNotEmptySource;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public ObservableRound getRound( ) {
        return _round;
    }

    public SetScoreDialog createSetScoreDialog( ) {
        return new SetScoreDialog( _setScoreDialogFactory.forRound( _tournament, _round ) );
    }

    private final ObservableTournament _tournament;
    private final ObservableRound _round;
    private final I18nTranslator _i18n;
    private final SetScoreDialogModel.Factory _setScoreDialogFactory;

    private SimpleBooleanProperty _filterNotEmptySource = new SimpleBooleanProperty( );
}
