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

import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.rules.ITournamentRulesProvider;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.InformationEvent;
import land.tower.core.view.event.TournamentUpdatedEvent;

/**
 * Created on 31/12/2017
 * @author Cédric Longo
 */
public final class DeleteRoundDialogModel {

    public interface Factory {

        DeleteRoundDialogModel forTournament( ObservableTournament tournament );
    }

    @Inject
    public DeleteRoundDialogModel( final I18nTranslator i18n,
                                   final @Assisted ObservableTournament tournament,
                                   final ITournamentRulesProvider tournamentRules,
                                   final EventBus eventBus, final Stage owner ) {
        _i18n = i18n;
        _tournament = tournament;
        _tournamentRules = tournamentRules;
        _eventBus = eventBus;
        _owner = owner;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    void fireDeleteRound( ) {
        final ObservableRound lastRound = _tournament.getCurrentRound( );
        if ( lastRound != null ) {
            _tournament.getRounds( ).remove( lastRound );
            _tournament.getTeams( ).forEach( team -> team.getPairingFlags( ).remove( "final" ) );
            _eventBus.post( new InformationEvent( _i18n.get( "round.deleted", lastRound.getNumero( ) ) ) );
        }
        _tournamentRules.forGame( _tournament.getHeader( ).getGame( ) )
                        .getPairingRules( )
                        .get( _tournament.getHeader( ).getPairingMode( ) )
                        .getRankingComputer( )
                        .computeRanking( _tournament );
        _eventBus.post( new TournamentUpdatedEvent( _tournament ) );
    }

    public Stage getOwner( ) {
        return _owner;
    }

    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
    private final ITournamentRulesProvider _tournamentRules;
    private final EventBus _eventBus;
    private final Stage _owner;
}
