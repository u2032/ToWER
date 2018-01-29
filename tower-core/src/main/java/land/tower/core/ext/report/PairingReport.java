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

package land.tower.core.ext.report;

import com.google.inject.assistedinject.Assisted;

import net.sf.jasperreports.engine.JRDataSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.Match;
import land.tower.data.Teams;

/**
 * Created on 28/01/2018
 * @author Cédric Longo
 */
public final class PairingReport implements Report {

    public interface Factory {

        PairingReport create( ObservableTournament tournament, ObservableRound round, boolean byName );
    }

    @Inject
    public PairingReport( final I18nTranslator i18n,
                          @Assisted final ObservableTournament tournament,
                          @Assisted final ObservableRound round,
                          @Assisted final boolean byName ) {
        _i18n = i18n;
        _tournament = tournament;
        _round = round;
        _byName = byName;
    }

    @Override
    public String getTemplate( ) {
        return "reports/pairing.jrxml";
    }

    @Override
    public JRDataSource getJRDataSource( ) {
        if ( !_byName ) {
            return new JRMatchListDataSource( _tournament, _round.getMatches( ),
                                              Comparator.comparing( ObservableMatch::getPosition ) );
        }

        // We simulate inversed matches
        final List<ObservableMatch> matches = new ArrayList<>( _round.getMatches( ) );
        _round.getMatches( ).forEach( m -> {
            final Match match = new Match( );
            match.setPosition( m.getPosition( ) );
            match.setLeftTeamId( m.getRightTeamId( ) );
            match.setRightTeamId( m.getLeftTeamId( ) );
            matches.add( new ObservableMatch( match ) );
        } );
        matches.removeIf( m -> m.getLeftTeamId( ) == Teams.BYE_TEAM.getId( ) );
        return new JRMatchListDataSource( _tournament, matches,
                                          Comparator.comparing( m -> _tournament.getTeam( m.getLeftTeamId( ) )
                                                                                .getName( ) ) );
    }

    @Override
    public Map<String, Object> getParameters( ) {
        final HashMap<String, Object> params = new HashMap<>( );
        params.put( "page.title", _i18n.get( "report.pairing.title", _round.getNumero( ) ).toUpperCase( ) );
        params.put( "tournament.title", _tournament.getHeader( ).getTitle( ) );
        params.put( "match.position", _i18n.get( "match.position.short" ).getValue( ) );
        params.put( "match.team.left", _i18n.get( "report.pairing.team.left" ).getValue( ) );
        params.put( "match.team.right", _i18n.get( "report.pairing.team.right" ).getValue( ) );
        params.put( "page", _i18n.get( "report.page" ).getValue( ) );
        return params;
    }

    @Override
    public String getTitle( ) {
        return _i18n.get( "report.pairing.title", _round.getNumero( ) );
    }

    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
    private final ObservableRound _round;
    private final boolean _byName;
}
