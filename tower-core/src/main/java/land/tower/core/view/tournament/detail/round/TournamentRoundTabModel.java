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

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;

import java.util.Map;
import javafx.beans.property.SimpleBooleanProperty;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.pairing.PairingSystem;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.TournamentUpdatedEvent;
import land.tower.data.PairingMode;
import land.tower.data.Round;

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
                                    final SetScoreDialogModel.Factory setScoreDialogFactory,
                                    final EventBus eventBus,
                                    final Map<PairingMode, PairingSystem> pairingSystems ) {
        _tournament = tournament;
        _round = round;
        _i18n = i18n;
        _setScoreDialogFactory = setScoreDialogFactory;
        _eventBus = eventBus;
        _pairingSystems = pairingSystems;
    }

    public void fireStartNewRound( ) {
        final PairingSystem pairing = _pairingSystems.get( _tournament.getHeader( ).getPairingMode( ) );
        final Round newRound = pairing.createNewRound( _tournament );
        _tournament.registerRound( new ObservableRound( newRound ) );

        _pairingSystems.get( _tournament.getHeader( ).getPairingMode( ) )
                       .getRankingComputer( )
                       .computeRanking( _tournament );

        _eventBus.post( new TournamentUpdatedEvent( _tournament ) );
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
        switch ( _tournament.getHeader( ).getScoringMode( ) ) {
            case BY_WINS:
                return new SetScoreDialog( _setScoreDialogFactory.forRound( _tournament, _round ) );
            case BY_POINTS:
                return new SetScoreByPointsDialog( _setScoreDialogFactory.forRound( _tournament, _round ) );
        }
        throw new UnsupportedOperationException( "Scoring dialog for scoring mode " +
                                                 _tournament.getHeader( ).getScoringMode( ) + " is not defined" );
    }

    private final ObservableTournament _tournament;
    private final ObservableRound _round;
    private final I18nTranslator _i18n;
    private final SetScoreDialogModel.Factory _setScoreDialogFactory;

    private final SimpleBooleanProperty _filterNotEmptySource = new SimpleBooleanProperty( );
    private final EventBus _eventBus;
    private final Map<PairingMode, PairingSystem> _pairingSystems;
}
