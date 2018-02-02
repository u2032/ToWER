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

package land.tower.core.view.tournament.detail;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.tournament.detail.TournamentViewModel.Factory;

/**
 * Created on 20/12/2017
 * @author Cédric Longo
 */
public final class TournamentViewProvider {

    @Inject
    public TournamentViewProvider( final Factory factory ) {
        _factory = factory;
    }

    public TournamentView forTournament( ObservableTournament tournament ) {
        return _viewModels.computeIfAbsent( tournament.getTournament( ).getId( ),
                                            id -> new TournamentView( _factory.forTournament( tournament ) ) );
    }

    public void removeTournamentView( final UUID id ) {
        _viewModels.remove( id );
    }

    private final Map<UUID, TournamentView> _viewModels = Maps.newConcurrentMap( );

    private final TournamentViewModel.Factory _factory;

}
