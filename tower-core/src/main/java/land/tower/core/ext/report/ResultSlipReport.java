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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 28/01/2018
 * @author Cédric Longo
 */
public final class ResultSlipReport implements Report {

    public interface Factory {

        ResultSlipReport create( ObservableTournament tournament, ObservableRound round );
    }

    @Inject
    public ResultSlipReport( final I18nTranslator i18n,
                             @Assisted final ObservableTournament tournament,
                             @Assisted final ObservableRound round ) {
        _i18n = i18n;
        _tournament = tournament;
        _round = round;
    }

    @Override
    public String getTemplate( ) {
        return "reports/slips_" + _tournament.getHeader( ).getScoringMode( ) + ".jrxml";
    }

    @Override
    public JRMatchListDataSource getJRDataSource( ) {
        final int NUMBER_BY_PAGE = 6; // according to the design of the template
        final List<ObservableMatch> byPosition = _round.getMatches( )
                                                       .stream( )
                                                       .sorted( Comparator.comparing( ObservableMatch::getPosition ) )
                                                       .collect( Collectors.toList( ) );

        final int NUMBER_OF_PAGES = byPosition.size( ) / NUMBER_BY_PAGE
                                    + ( byPosition.size( ) % NUMBER_BY_PAGE > 0 ? 1 : 0 );

        final List<ObservableMatch[]> parts = new ArrayList<>( );
        for ( int i = 0; i < NUMBER_OF_PAGES; i++ ) {
            parts.add( new ObservableMatch[NUMBER_BY_PAGE] );
        }

        int x = 0, y = 0;
        for ( int i = 0; i < byPosition.size( ); i++ ) {
            parts.get( x )[y] = byPosition.get( i );
            x++;
            if ( x >= NUMBER_OF_PAGES ) {
                x = 0;
                y++;
            }
        }

        final List<ObservableMatch> matches = new ArrayList<>( );
        parts.forEach( l -> {
            matches.addAll( Arrays.asList( l ) );
        } );

        return new JRMatchListDataSource( _tournament, matches );
    }

    @Override
    public Map<String, Object> getParameters( ) {
        final HashMap<String, Object> params = new HashMap<>( );
        params.put( "page.title", _i18n.get( "report.slips.title", _round.getNumero( ) ).toUpperCase( ) );
        params.put( "tournament.title", _tournament.getHeader( ).getTitle( ) );
        params.put( "double.score", _tournament.getHeader( ).getDoubleScore( ) );
        params.put( "match.position", _i18n.get( "match.position" ).getValue( ) );
        params.put( "match.team.left", _i18n.get( "report.pairing.team.left" ).getValue( ) );
        params.put( "match.team.right", _i18n.get( "report.pairing.team.right" ).getValue( ) );
        params.put( "round", _i18n.get( "tournament.tab.round" ).getValue( ) );
        params.put( "page", _i18n.get( "report.page" ).getValue( ) );
        params.put( "victory", _i18n.get( "match.score.wins" ).getValue( ) );
        params.put( "draws", _i18n.get( "match.score.draw" ).getValue( ) );
        params.put( "points", _i18n.get( "match.score.points" ).getValue( ) );
        params.put( "round.numero", _round.getNumero( ) );
        return params;
    }

    @Override
    public String getTitle( ) {
        return _i18n.get( "report.pairing.title", _round.getNumero( ) );
    }

    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
    private final ObservableRound _round;
}
