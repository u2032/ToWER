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

import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.rules.ITournamentRulesProvider;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.TournamentUpdatedEvent;

/**
 * Created on 31/12/2017
 * @author Cédric Longo
 */
public final class SetScoreDialogModel {

    public interface Factory {

        SetScoreDialogModel forRound( ObservableTournament tournament, ObservableRound round );

    }

    @Inject
    SetScoreDialogModel( final I18nTranslator i18n,
                         @Assisted final ObservableTournament tournament,
                         @Assisted final ObservableRound round, final EventBus eventBus,
                         final ITournamentRulesProvider tournamentRules, final Stage owner ) {
        _i18n = i18n;
        _tournament = tournament;
        _round = round;
        _eventBus = eventBus;
        _tournamentRules = tournamentRules;
        _owner = owner;

        _errorInformation.bind( Bindings.createStringBinding( ( ) -> {
            final int leftWins = _leftScore.get( ) == null ? 0 : _leftScore.get( );
            final int draws = _draws.get( ) == null ? 0 : _draws.get( );
            final int rightWins = _rightScore.get( ) == null ? 0 : _rightScore.get( );
            final int position = _position.get( ) == null ? 0 : _position.get( );

            if ( leftWins > tournament.getHeader( ).getScoreMax( ) ) {
                return _i18n.get( "tournament.round.scoring.error.too.high", leftWins );
            }

            if ( rightWins > tournament.getHeader( ).getScoreMax( ) ) {
                return _i18n.get( "tournament.round.scoring.error.too.high", rightWins );
            }

            if ( leftWins + draws + rightWins == 0 ) {
                return _i18n.get( "tournament.round.scoring.error.empty" ).get( );
            }

            if ( position == 0 ) {
                // No error message but not valid
                return " ";
            }

            final boolean positionFound = round.getMatches( ).stream( )
                                               .anyMatch( m -> m.getPosition( ) == position );
            if ( !positionFound ) {
                return _i18n.get( "tournament.round.scoring.error.position.not.found", position );
            }

            return null;
        }, _leftScore, _draws, _rightScore, _position ) );

        _teamInfo.bind( Bindings.createStringBinding( ( ) -> {
            if ( _position.get( ) == null || _position.get( ) == 0 ) {
                return null;
            }

            final Optional<ObservableMatch> match = _round.getMatches( ).stream( )
                                                          .filter( m -> m.getPosition( ) == _position.get( ) )
                                                          .findAny( );

            if ( !match.isPresent( ) ) {
                return null;
            }

            final ObservableTeam left = _tournament.getTeam( match.get( ).getLeftTeamId( ) );
            final ObservableTeam right = _tournament.getTeam( match.get( ).getRightTeamId( ) );

            return String.format( "%s / %s", left.getName( ), right.getName( ) );

        }, _position ) );
    }

    void fireSaveScore( ) {
        final ObservableMatch match = _round.getMatches( ).stream( )
                                            .filter( m -> m.getPosition( ) == _position.get( ) )
                                            .findAny( )
                                            .orElseThrow( IllegalArgumentException::new );
        match.scoreLeftProperty( ).set( _leftScore.get( ) == null ? 0 : _leftScore.getValue( ) );
        match.scoreDrawProperty( ).set( _draws.get( ) == null ? 0 : _draws.getValue( ) );
        match.scoreRightProperty( ).set( _rightScore.get( ) == null ? 0 : _rightScore.getValue( ) );

        if ( _round.isEnded( ) ) {
            // If the round is ended, trigger ranking computing
            _tournamentRules.forGame( _tournament.getHeader( ).getGame( ) )
                            .getPairingRules( )
                            .get( _tournament.getHeader( ).getPairingMode( ) )
                            .getRankingComputer( )
                            .computeRanking( _tournament );
        }

        _eventBus.post( new TournamentUpdatedEvent( _tournament ) );
    }

    public Stage getOwner( ) {
        return _owner;
    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public ObservableRound getRound( ) {
        return _round;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public String getErrorInformation( ) {
        return _errorInformation.get( );
    }

    public SimpleStringProperty errorInformationProperty( ) {
        return _errorInformation;
    }

    public Integer getLeftScore( ) {
        return _leftScore.get( );
    }

    public SimpleObjectProperty<Integer> leftScoreProperty( ) {
        return _leftScore;
    }

    public Integer getDraws( ) {
        return _draws.get( );
    }

    public SimpleObjectProperty<Integer> drawsProperty( ) {
        return _draws;
    }

    public Integer getRightScore( ) {
        return _rightScore.get( );
    }

    public SimpleObjectProperty<Integer> rightScoreProperty( ) {
        return _rightScore;
    }

    public Integer getPosition( ) {
        return _position.get( );
    }

    public SimpleObjectProperty<Integer> positionProperty( ) {
        return _position;
    }

    public SimpleStringProperty teamInfoProperty( ) {
        return _teamInfo;
    }

    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
    private final ObservableRound _round;

    private final SimpleStringProperty _errorInformation = new SimpleStringProperty( );
    private final SimpleStringProperty _teamInfo = new SimpleStringProperty( );

    private final SimpleObjectProperty<Integer> _leftScore = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _draws = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _rightScore = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _position = new SimpleObjectProperty<>( );

    private final EventBus _eventBus;
    private final ITournamentRulesProvider _tournamentRules;
    private final Stage _owner;
}
