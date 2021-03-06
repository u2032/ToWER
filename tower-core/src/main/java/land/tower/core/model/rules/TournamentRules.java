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

import java.util.Map;
import java.util.Optional;
import land.tower.data.PairingMode;
import land.tower.data.TournamentScoringMode;
import lombok.Builder;
import lombok.Data;

/**
 * Created on 31/01/2018
 * @author Cédric Longo
 */
@Builder
@Data
public final class TournamentRules {

    private final Optional<TournamentScoringMode> scoringMode;
    private final Optional<Integer> scoreMax;
    private final Optional<Integer> byeScore;
    private final Optional<Integer> scoreMaxBis;
    private final Optional<Integer> byeScoreBis;

    private final Map<PairingMode, PairingRule> pairingRules;

    private final Map<String, String[]> teamExtraInfo;
}
