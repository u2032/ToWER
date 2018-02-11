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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import land.tower.core.model.tournament.ObservableTeam;

/**
 * Created on 28/01/2018
 * @author Cédric Longo
 */
final class JRTeamListDataSource implements JRRewindableDataSource {

    JRTeamListDataSource( final List<ObservableTeam> teams ) {
        _teams = new ArrayList<>( teams );
    }

    JRTeamListDataSource( final List<ObservableTeam> teams, Comparator<ObservableTeam> comparator ) {
        _teams = new ArrayList<>( teams );
        _teams.sort( comparator );
    }

    @Override
    public boolean next( ) throws JRException {
        index++;
        return index < _teams.size( );
    }

    @Override
    public Object getFieldValue( final JRField jrField ) throws JRException {
        final ObservableTeam team = _teams.get( index );
        switch ( jrField.getName( ) ) {
            case "name":
                return team.getName( );
            case "rank":
                return team.getRanking( ).getRank( );
            case "points":
                return team.getRanking( ).getPoints( );
            case "d1":
                return team.getRanking( ).getD1( );
            case "d2":
                return team.getRanking( ).getD2( );
            case "d3":
                return team.getRanking( ).getD3( );
            case "d4":
                return team.getRanking( ).getD4( );
        }
        return null;
    }

    @Override
    public void moveFirst( ) throws JRException {
        index = -1;
    }

    List<ObservableTeam> getTeams( ) {
        return _teams;
    }

    private final List<ObservableTeam> _teams;
    private int index = -1;
}
