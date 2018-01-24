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

import java.time.ZonedDateTime;

/**
 * Created on 23/01/2018
 * @author Cédric Longo
 */
public final class TimerInfo {

    /**
     * Initial time of timer. ex: 30 min.
     */
    @SerializedName( "initialDuration" )
    private int _initialDuration;

    /**
     * Final delay around the end time when the timer has been stopped. If null, the timer is still ongoing
     */
    @SerializedName( "finalDuration" )
    private Integer _finalDuration;

    /**
     * Timestamp when the timer expires, if started. If null, the timer has not been started yet
     */
    @SerializedName( "endTime" )
    private ZonedDateTime _endTime;

    public TimerInfo( ) {
    }

    public TimerInfo( final int initialDuration ) {
        _initialDuration = initialDuration;
    }

    public int getInitialDuration( ) {
        return _initialDuration;
    }

    public void setInitialDuration( final int initialDuration ) {
        _initialDuration = initialDuration;
    }

    public Integer getFinalDuration( ) {
        return _finalDuration;
    }

    public void setFinalDuration( final Integer finalDuration ) {
        _finalDuration = finalDuration;
    }

    public ZonedDateTime getEndTime( ) {
        return _endTime;
    }

    public void setEndTime( final ZonedDateTime endTime ) {
        _endTime = endTime;
    }
}
