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

import org.controlsfx.control.Notifications;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.report.PairingReport;
import land.tower.core.ext.report.ReportEngine;
import land.tower.core.ext.report.ResultSlipReport;
import land.tower.core.model.rules.ITournamentRulesProvider;
import land.tower.core.model.rules.PairingRule;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.InformationEvent;
import land.tower.core.view.event.TournamentUpdatedEvent;
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
                                    final ManualPairingDialogModel.Factory manualPairingDialogFactory,
                                    final EventBus eventBus,
                                    final ReportEngine reportEngine,
                                    final PairingReport.Factory pairingReportFactory,
                                    final ResultSlipReport.Factory resultSlipReportFactory,
                                    final Stage owner,
                                    final ITournamentRulesProvider tournamentRules ) {
        _tournament = tournament;
        _round = round;
        _i18n = i18n;
        _setScoreDialogFactory = setScoreDialogFactory;
        _manualPairingDialogFactory = manualPairingDialogFactory;
        _eventBus = eventBus;
        _tournamentRules = tournamentRules;
        _reportEngine = reportEngine;
        _pairingReportFactory = pairingReportFactory;
        _resultSlipReportFactory = resultSlipReportFactory;

        tournament.getCurrentRound( ).getTimer( )
                  .overtimeProperty( )
                  .addListener( ( observable, oldValue, newValue ) -> {
                      if ( !oldValue && newValue && _round == _tournament.getCurrentRound( ) ) {
                          final Notifications notif = Notifications.create( );
                          notif.owner( owner );
                          notif.position( Pos.CENTER );
                          notif.hideAfter( new Duration( 600_000 ) );
                          notif.title( _i18n.get( "round.overtime.title", _round.getNumero( ) ) );
                          notif.text( _i18n.get( "round.overtime.message",
                                                 _round.getNumero( ),
                                                 _tournament.getHeader( ).getTitle( ) ) );
                          Platform.runLater( notif::showWarning );
                      }
                  } );
    }

    public void fireStartNewRound( ) {
        _eventBus.post( new InformationEvent( _i18n.get( "round.generation.started" ) ) );
        final PairingRule pairing = _tournamentRules.forGame( _tournament.getHeader( ).getGame( ) )
                                                    .getPairingRules( )
                                                    .get( _tournament.getHeader( ).getPairingMode( ) );
        final Round newRound = pairing.getPairingSystem( ).createNewRound( _tournament );
        _tournament.registerRound( new ObservableRound( newRound ) );

        pairing.getRankingComputer( ).computeRanking( _tournament );

        _eventBus.post( new TournamentUpdatedEvent( _tournament ) );
        _eventBus.post( new InformationEvent( _i18n.get( "round.generation.finished", newRound.getNumero( ) ) ) );
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

    public void firePrintLadderByPosition( ) {
        _eventBus.post( new InformationEvent( _i18n.get( "document.generation.started" ) ) );
        _reportEngine.generate( _pairingReportFactory.create( _tournament, _round, false ) )
                     .thenRun( ( ) -> {
                         _eventBus.post( new InformationEvent( _i18n.get( "document.generation.finished" ) ) );
                     } );
    }

    public void firePrintLadderByName( ) {
        _eventBus.post( new InformationEvent( _i18n.get( "document.generation.started" ) ) );
        _reportEngine.generate( _pairingReportFactory.create( _tournament, _round, true ) )
                     .thenRun( ( ) -> {
                         _eventBus.post( new InformationEvent( _i18n.get( "document.generation.finished" ) ) );
                     } );
    }

    boolean useDoubleScore( ) {
        return _tournament.getHeader( ).getDoubleScore( );
    }

    public void firePrintResultSlips( ) {
        _eventBus.post( new InformationEvent( _i18n.get( "document.generation.started" ) ) );
        _reportEngine.generate( _resultSlipReportFactory.create( _tournament, _round ) )
                     .thenRun( ( ) -> {
                         _eventBus.post( new InformationEvent( _i18n.get( "document.generation.finished" ) ) );
                     } );
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

    public void fireManualPairing( ) {
        new ManualPairingDialog( _manualPairingDialogFactory.forRound( _tournament, _round ) )
            .show( );
    }

    private final ObservableTournament _tournament;
    private final ObservableRound _round;
    private final I18nTranslator _i18n;
    private final SetScoreDialogModel.Factory _setScoreDialogFactory;
    private final ManualPairingDialogModel.Factory _manualPairingDialogFactory;

    private final SimpleBooleanProperty _filterNotEmptySource = new SimpleBooleanProperty( );
    private final EventBus _eventBus;
    private final ITournamentRulesProvider _tournamentRules;

    private final ReportEngine _reportEngine;
    private final PairingReport.Factory _pairingReportFactory;
    private final ResultSlipReport.Factory _resultSlipReportFactory;
}
