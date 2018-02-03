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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import land.tower.data.PairingMode;

/**
 * Created on 31/01/2018
 * @author Cédric Longo
 */
final class TournamentRulesProvider implements ITournamentRulesProvider {

    @Inject
    public TournamentRulesProvider( final Map<PairingMode, PairingRule> defaultRules ) {
        _default = TournamentRules.builder( )
                                  .scoringMode( Optional.empty( ) )
                                  .scoreMax( Optional.empty( ) )
                                  .pairingRules( defaultRules )
                                  .build( );
    }

    @Override
    public TournamentRules forGame( final String game ) {
        return _rules.getOrDefault( game, _default );
    }

    @Override
    public void registerRules( final String game, final TournamentRules rules ) {
        _rules.put( game, rules );
    }

    private final TournamentRules _default;
    private final Map<String, TournamentRules> _rules = new HashMap<>( );

}

