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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 28/01/2018
 * @author Cédric Longo
 */
public final class LadderReport implements Report {

    public interface Factory {

        LadderReport create( ObservableTournament tournament );
    }

    @Inject
    public LadderReport( final I18nTranslator i18n,
                         @Assisted final ObservableTournament tournament ) {
        _i18n = i18n;
        _tournament = tournament;
    }

    @Override
    public String getTemplate( ) {
        return "reports/ladder.jrxml";
    }

    @Override
    public JRTeamListDataSource getJRDataSource( ) {
        return new JRTeamListDataSource( _tournament.getTeams( ),
                                         Comparator.comparing( t -> t.getRanking( ).getRank( ) ) );
    }

    @Override
    public Map<String, Object> getParameters( ) {
        final HashMap<String, Object> params = new HashMap<>( );
        params.put( "page.title", _i18n.get( "report.ladder.title" ).get( ).toUpperCase( ) );
        params.put( "tournament.title", _tournament.getHeader( ).getTitle( ) );
        params.put( "team.rank", _i18n.get( "ranking.rank" ).getValue( ) );
        params.put( "team.points", _i18n.get( "ranking.points" ).getValue( ) );
        params.put( "team.name", _i18n.get( "team.name" ).getValue( ) );
        params.put( "page", _i18n.get( "report.page" ).getValue( ) );
        return params;
    }

    @Override
    public String getTitle( ) {
        return _i18n.get( "report.ladder.title" ).get( );
    }

    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
}
