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

import javax.inject.Inject;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.tournament.detail.round.TournamentRoundTabModel.Factory;

/**
 * Created on 20/01/2018
 * @author Cédric Longo
 */
public final class TournamentRoundTabFactory {

    @Inject
    public TournamentRoundTabFactory( final Factory roundTabModelFactory ) {
        _roundTabModelFactory = roundTabModelFactory;
    }

    public TournamentRoundTab create( final ObservableTournament tournament, final ObservableRound round ) {
        switch ( tournament.getHeader( ).getScoringMode( ) ) {
            case BY_WINS:
                return new TournamentRoundTab( _roundTabModelFactory.create( tournament, round ) );
            case BY_POINTS:
                return new TournamentRoundByPointTab( _roundTabModelFactory.create( tournament, round ) );
        }
        throw new UnsupportedOperationException( "Scoring mode " + tournament.getHeader( ).getScoringMode( )
                                                 + " not fully implemented" );
    }

    private final TournamentRoundTabModel.Factory _roundTabModelFactory;

}
