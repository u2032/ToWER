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

package land.tower.data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 30/12/2017
 * @author Cédric Longo
 */
public final class Round {

    private int _numero;
    private List<Match> _matches = new ArrayList<>( );

    private ZonedDateTime _startDate;

    public int getNumero( ) {
        return _numero;
    }

    public void setNumero( final int numero ) {
        _numero = numero;
    }

    public List<Match> getMatches( ) {
        return _matches;
    }

    public void setMatches( final List<Match> matches ) {
        _matches = matches;
    }

    public ZonedDateTime getStartDate( ) {
        return _startDate;
    }

    public void setStartDate( final ZonedDateTime startDate ) {
        _startDate = startDate;
    }
}
