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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.InformationEvent;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.event.TournamentUpdatedEvent;
import land.tower.core.view.tournament.detail.TournamentView;
import land.tower.data.Teams;

/**
 * Created on 18/11/2017
 * @author Cédric Longo
 */
final class ApplicationStatusBarModel {

    private final StringProperty _taskInfo = new SimpleStringProperty( );
    private final StringProperty _stateInfo = new SimpleStringProperty( );

    private final I18nTranslator _i18n;

    @Inject
    public ApplicationStatusBarModel( final EventBus eventBus, final I18nTranslator i18n ) {
        _i18n = i18n;
        eventBus.register( this );
    }

    @Subscribe
    public void informationEvent( final InformationEvent event ) {
        Platform.runLater( ( ) -> taskInfoProperty( ).setValue( event.getText( ) ) );
    }

    @Subscribe
    private void tournamentViewDisplayed( final SceneRequestedEvent event ) {
        if ( event.getView( ) instanceof TournamentView ) {
            final TournamentView view = ( (TournamentView) event.getView( ) );
            final ObservableTournament tournament = view.getModel( ).getTournament( );
            updateStatusInfo( tournament );
        } else {
            Platform.runLater( ( ) -> _stateInfo.set( null ) );
        }
    }

    @Subscribe
    private void tournamentUpdated( final TournamentUpdatedEvent event ) {
        updateStatusInfo( event.getTournament( ) );
    }

    private void updateStatusInfo( final ObservableTournament tournament ) {
        final ObservableRound currentRound = tournament.getCurrentRound( );
        if ( currentRound == null ) {
            Platform.runLater( ( ) -> _stateInfo.set( null ) );
            return;
        }

        final long teamCount = currentRound.getMatches( ).stream( )
                                           .flatMapToInt( m -> IntStream.of( m.getLeftTeamId( ), m.getRightTeamId( ) ) )
                                           .filter( id -> id != Teams.BYE_TEAM.getId( ) )
                                           .count( );

        final long matchFinishedCount = currentRound.getMatches( ).stream( )
                                                    .filter( ObservableMatch::hasScore )
                                                    .count( );

        final String info = _i18n.get( "tournament.round.state.infobar",
                                       currentRound.getNumero( ),
                                       teamCount,
                                       matchFinishedCount,
                                       currentRound.getMatches( ).size( ) );

        Platform.runLater( ( ) -> _stateInfo.setValue( info ) );
    }

    StringProperty taskInfoProperty( ) {
        return _taskInfo;
    }

    StringProperty stateInfoProperty( ) {
        return _stateInfo;
    }
}
