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

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 30/12/2017
 * @author Cédric Longo
 */
public final class Round {

    @SerializedName( value = "numero", alternate = { "_numero" } )
    private int _numero;

    @SerializedName( value = "matches", alternate = { "_matches" } )
    private List<Match> _matches = new ArrayList<>( );

    @SerializedName( value = "final", alternate = { "_final" } )
    private boolean _final;

    @SerializedName( "timer" )
    private TimerInfo _timer = new TimerInfo( );

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

    public TimerInfo getTimer( ) {
        return _timer;
    }

    public void setTimer( final TimerInfo timer ) {
        _timer = timer;
    }

    public void setFinal( final boolean aFinal ) {
        _final = aFinal;
    }

    public boolean isFinal( ) {
        return _final;
    }
}
