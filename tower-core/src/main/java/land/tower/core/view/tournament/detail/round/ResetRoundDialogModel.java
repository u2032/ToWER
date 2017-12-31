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

import com.google.inject.assistedinject.Assisted;

import java.util.Map;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.pairing.PairingSystem;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.PairingMode;
import land.tower.data.Round;

/**
 * Created on 31/12/2017
 * @author Cédric Longo
 */
public final class ResetRoundDialogModel {

    public interface Factory {

        ResetRoundDialogModel forTournament( ObservableTournament tournament );
    }

    @Inject
    public ResetRoundDialogModel( final Configuration config, final I18nTranslator i18n,
                                  final @Assisted ObservableTournament tournament,
                                  final Map<PairingMode, PairingSystem> pairingSystems ) {
        _config = config;
        _i18n = i18n;
        _tournament = tournament;
        _pairingSystems = pairingSystems;
    }

    public Configuration getConfig( ) {
        return _config;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public void fireResetRound( ) {
        final ObservableRound lastRound = _tournament.getCurrentRound( );
        if ( lastRound != null ) {
            _tournament.getRounds( ).remove( lastRound );
        }

        final Round newRound = _pairingSystems.get( _tournament.getHeader( ).getPairingMode( ) )
                                              .createNewRound( _tournament.getTournament( ) );
        _tournament.registerRound( new ObservableRound( newRound ) );
    }

    private final Configuration _config;
    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;

    private final Map<PairingMode, PairingSystem> _pairingSystems;
}
