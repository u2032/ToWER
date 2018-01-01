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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.ranking.IRankingComputer;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableRound;
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
    SetScoreDialogModel( final Configuration config, final I18nTranslator i18n,
                         @Assisted final ObservableTournament tournament,
                         @Assisted final ObservableRound round, final EventBus eventBus,
                         final IRankingComputer rankingComputer ) {
        _config = config;
        _i18n = i18n;
        _tournament = tournament;
        _round = round;
        _eventBus = eventBus;
        _rankingComputer = rankingComputer;

        _errorInformation.bind( Bindings.createStringBinding( ( ) -> {
            final int leftWins = _leftWins.get( ) == null ? 0 : _leftWins.get( );
            final int draws = _draws.get( ) == null ? 0 : _draws.get( );
            final int rightWins = _rightWins.get( ) == null ? 0 : _rightWins.get( );
            final int position = _position.get( ) == null ? 0 : _position.get( );

            if ( leftWins > tournament.getHeader( ).getWinningGameCount( ) ) {
                return _i18n.get( "tournament.round.scoring.error.too.high", leftWins );
            }

            if ( draws > tournament.getHeader( ).getWinningGameCount( ) ) {
                return _i18n.get( "tournament.round.scoring.error.too.high", draws );
            }

            if ( rightWins > tournament.getHeader( ).getWinningGameCount( ) ) {
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
        }, _leftWins, _draws, _rightWins, _position ) );
    }

    public void fireSaveScore( ) {
        final ObservableMatch match = _round.getMatches( ).stream( )
                                            .filter( m -> m.getPosition( ) == _position.get( ) )
                                            .findAny( )
                                            .orElseThrow( IllegalArgumentException::new );
        match.scoreLeftProperty( ).set( _leftWins.get( ) == null ? 0 : _leftWins.getValue( ) );
        match.scoreDrawProperty( ).set( _draws.get( ) == null ? 0 : _draws.getValue( ) );
        match.scoreRightProperty( ).set( _rightWins.get( ) == null ? 0 : _rightWins.getValue( ) );

        if ( _round.isEnded( ) ) {
            // If the round is ended, trigger ranking computing
            _rankingComputer.computeRanking( _tournament );
        }

        _eventBus.post( new TournamentUpdatedEvent( _tournament ) );
    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public ObservableRound getRound( ) {
        return _round;
    }

    public Configuration getConfig( ) {
        return _config;
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

    public Integer getLeftWins( ) {
        return _leftWins.get( );
    }

    public SimpleObjectProperty<Integer> leftWinsProperty( ) {
        return _leftWins;
    }

    public Integer getDraws( ) {
        return _draws.get( );
    }

    public SimpleObjectProperty<Integer> drawsProperty( ) {
        return _draws;
    }

    public Integer getRightWins( ) {
        return _rightWins.get( );
    }

    public SimpleObjectProperty<Integer> rightWinsProperty( ) {
        return _rightWins;
    }

    public Integer getPosition( ) {
        return _position.get( );
    }

    public SimpleObjectProperty<Integer> positionProperty( ) {
        return _position;
    }

    private final Configuration _config;
    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
    private final ObservableRound _round;

    private final SimpleStringProperty _errorInformation = new SimpleStringProperty( );

    private final SimpleObjectProperty<Integer> _leftWins = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _draws = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _rightWins = new SimpleObjectProperty<>( );
    private final SimpleObjectProperty<Integer> _position = new SimpleObjectProperty<>( );

    private final EventBus _eventBus;
    private final IRankingComputer _rankingComputer;
}
