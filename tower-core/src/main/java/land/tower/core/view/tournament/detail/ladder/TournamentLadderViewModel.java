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

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;

import java.util.Map;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.report.LadderReport;
import land.tower.core.ext.report.ReportEngine;
import land.tower.core.model.rules.ITournamentRulesProvider;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.InformationEvent;

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
                                      final I18nTranslator i18n,
                                      final CloseTournamentDialogModel.Factory closeTournamentDialogModelProvider,
                                      final ChainTournamentDialogModel.Factory chainTournamentDialogModelProvider,
                                      final ReportEngine reportEngine,
                                      final LadderReport.Factory ladderReportFactory,
                                      final EventBus eventBus,
                                      final ITournamentRulesProvider rulesProvider ) {
        _tournament = tournament;
        _i18n = i18n;
        _closeTournamentDialogModelProvider = closeTournamentDialogModelProvider;
        _chainTournamentDialogModelProvider = chainTournamentDialogModelProvider;
        _reportEngine = reportEngine;
        _ladderReportFactory = ladderReportFactory;
        _eventBus = eventBus;
        _rulesProvider = rulesProvider;
    }

    CloseTournamentDialogModel createCloseTournamentViewModel( ) {
        return _closeTournamentDialogModelProvider.forTournament( _tournament );
    }

    void fireChainTournamentDialog( ) {
        final ChainTournamentDialogModel model = _chainTournamentDialogModelProvider.forTournament( _tournament );
        new ChainTournamentDialog( model ).show( );
    }

    public void firePrintLadder( ) {
        _eventBus.post( new InformationEvent( _i18n.get( "document.generation.started" ) ) );
        _reportEngine.generate( _ladderReportFactory.create( _tournament ) )
                     .thenRun( ( ) -> {
                         _eventBus.post( new InformationEvent( _i18n.get( "document.generation.finished" ) ) );
                     } );
    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public Map<String, String[]> getTeamExtraInformation( ) {
        return _rulesProvider.forGame( _tournament.getHeader( ).getGame( ) ).getTeamExtraInfo( );
    }

    private final ObservableTournament _tournament;
    private final I18nTranslator _i18n;
    private final CloseTournamentDialogModel.Factory _closeTournamentDialogModelProvider;
    private final ChainTournamentDialogModel.Factory _chainTournamentDialogModelProvider;

    private final ReportEngine _reportEngine;
    private final LadderReport.Factory _ladderReportFactory;
    private final EventBus _eventBus;
    private final ITournamentRulesProvider _rulesProvider;
}
