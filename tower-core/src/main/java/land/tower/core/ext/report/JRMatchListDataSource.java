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
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 28/01/2018
 * @author Cédric Longo
 */
final class JRMatchListDataSource implements JRRewindableDataSource {

    JRMatchListDataSource( final ObservableTournament tournament, final List<ObservableMatch> matches ) {
        _tournament = tournament;
        _matches = new ArrayList<>( matches );
    }

    JRMatchListDataSource( final ObservableTournament tournament, final List<ObservableMatch> matches,
                           Comparator<ObservableMatch> comparator ) {
        _tournament = tournament;
        _matches = new ArrayList<>( matches );
        _matches.sort( comparator );
    }

    @Override
    public boolean next( ) throws JRException {
        index++;
        return index < _matches.size( );
    }

    @Override
    public Object getFieldValue( final JRField jrField ) throws JRException {
        final ObservableMatch match = _matches.get( index );
        switch ( jrField.getName( ) ) {
            case "position":
                return match.getPosition( );
            case "team.left":
                return _tournament.getTeam( match.getLeftTeamId( ) ).getName( );
            case "team.right":
                return _tournament.getTeam( match.getRightTeamId( ) ).getName( );
        }
        return null;
    }

    @Override
    public void moveFirst( ) throws JRException {
        index = -1;
    }

    private final ObservableTournament _tournament;
    private final List<ObservableMatch> _matches;
    private int index = -1;
}
