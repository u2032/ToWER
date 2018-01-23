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
 * Created on 30/12/2017
 * @author Cédric Longo
 */
public final class Match {

    @SerializedName( value = "position", alternate = { "_position" } )
    private int _position;

    @SerializedName( value = "leftTeamId", alternate = { "_leftTeamId" } )
    private int _leftTeamId;

    @SerializedName( value = "rightTeamId", alternate = { "_rightTeamId" } )
    private int _rightTeamId;

    @SerializedName( value = "scoreLeft", alternate = { "_scoreLeft" } )
    private int _scoreLeft;

    @SerializedName( value = "scoreDraw", alternate = { "_scoreDraw" } )
    private int _scoreDraw;

    @SerializedName( value = "scoreRight", alternate = { "_scoreRight" } )
    private int _scoreRight;

    public int getLeftTeamId( ) {
        return _leftTeamId;
    }

    public int getRightTeamId( ) {
        return _rightTeamId;
    }

    public int getPosition( ) {
        return _position;
    }

    public void setPosition( final int position ) {
        _position = position;
    }

    public void setLeftTeamId( final int leftTeamId ) {
        _leftTeamId = leftTeamId;
    }

    public void setRightTeamId( final int rightTeamId ) {
        _rightTeamId = rightTeamId;
    }

    public int getScoreLeft( ) {
        return _scoreLeft;
    }

    public void setScoreLeft( final int scoreLeft ) {
        _scoreLeft = scoreLeft;
    }

    public int getScoreDraw( ) {
        return _scoreDraw;
    }

    public void setScoreDraw( final int scoreDraw ) {
        _scoreDraw = scoreDraw;
    }

    public int getScoreRight( ) {
        return _scoreRight;
    }

    public void setScoreRight( final int scoreRight ) {
        _scoreRight = scoreRight;
    }
}
