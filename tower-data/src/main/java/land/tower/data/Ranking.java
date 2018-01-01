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

/**
 * Created on 01/01/2018
 * See https://fr.wikipedia.org/wiki/Syst%C3%A8me_de_d%C3%A9partage
 * @author Cédric Longo
 */
public final class Ranking {

    @SerializedName( "rank" )
    private int _rank;

    // Kashdan
    @SerializedName( "points" )
    private int _points;

    // Baumbach
    @SerializedName( "d1" )
    private int _d1;

    // Coons (~ Sonneborn-Berger)
    @SerializedName( "d2" )
    private int _d2;

    // Solkoff truncated / Cutoff
    @SerializedName( "d3" )
    private int _d3;

    // Koya
    @SerializedName( "d4" )
    private int _d4;

    public int getRank( ) {
        return _rank;
    }

    public void setRank( final int rank ) {
        _rank = rank;
    }

    public int getPoints( ) {
        return _points;
    }

    public void setPoints( final int points ) {
        _points = points;
    }

    public int getD1( ) {
        return _d1;
    }

    public void setD1( final int d1 ) {
        _d1 = d1;
    }

    public int getD3( ) {
        return _d3;
    }

    public void setD3( final int d3 ) {
        _d3 = d3;
    }

    public int getD2( ) {
        return _d2;
    }

    public void setD2( final int d2 ) {
        _d2 = d2;
    }

    public int getD4( ) {
        return _d4;
    }

    public void setD4( final int d4 ) {
        _d4 = d4;
    }
}
