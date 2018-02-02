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

package land.tower.core.model.rules;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

import java.util.Map;
import land.tower.core.model.pairing.direct.DirectEliminationSystem;
import land.tower.core.model.pairing.direct.DirectEliminiationRankingComputer;
import land.tower.core.model.pairing.swiss.SwissPairingSystem;
import land.tower.core.model.ranking.DefaultRankingComputer;
import land.tower.data.PairingMode;

/**
 * Created on 31/01/2018
 * @author Cédric Longo
 */
public final class TournamentRulesModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( ITournamentRulesProvider.class ).to( TournamentRulesProvider.class )
                                              .in( Scopes.SINGLETON );
    }

    @Provides
    @Singleton
    Map<PairingMode, PairingRule> defaultRules( final SwissPairingSystem swissPairingSystem,
                                                final DirectEliminationSystem directEliminationSystem,
                                                final DefaultRankingComputer defaultRankingComputer,
                                                final DirectEliminiationRankingComputer directRankingComputer ) {

        return ImmutableMap.<PairingMode, PairingRule>builder( )
                   .put( PairingMode.SWISS,
                         PairingRule.builder( )
                                    .pairingSystem( swissPairingSystem )
                                    .rankingComputer( defaultRankingComputer )
                                    .build( ) )

                   .put( PairingMode.DIRECT_ELIMINATION,
                         PairingRule.builder( )
                                    .pairingSystem( directEliminationSystem )
                                    .rankingComputer( directRankingComputer )
                                    .build( ) )
                   .build( );
    }
}
