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
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/12/2017
 * @author Cédric Longo
 */
public final class TournamentHeader {

    @SerializedName( "status" )
    private TournamentStatus _status;

    @SerializedName( "title" )
    private String _title;

    @SerializedName( "date" )
    private ZonedDateTime _date;

    @SerializedName( "pairingMode" )
    private PairingMode _pairingMode;

    @SerializedName( "address" )
    private Address _address;

    @SerializedName( "mainJudge" )
    private Player _mainJudge;

    @SerializedName( "judges" )
    private List<Player> _judges = new ArrayList<>( 0 );

    @SerializedName( "game" )
    private String _game;

    @SerializedName( "matchDuration" )
    private int _matchDuration;

    @SerializedName( "winningGameCount" )
    private int _winningGameCount;

    @SerializedName( "teamSize" )
    private int _teamSize;

    @SerializedName( "type" )
    private TournamentType _tournamentType;

    @SerializedName( "scoringMode" )
    private TournamentScoringMode _scoringMode = TournamentScoringMode.BY_WINS;

    public TournamentStatus getStatus( ) {
        return _status;
    }

    public void setStatus( final TournamentStatus status ) {
        _status = status;
    }

    public String getTitle( ) {
        return _title;
    }

    public void setTitle( final String title ) {
        _title = title;
    }

    public ZonedDateTime getDate( ) {
        return _date;
    }

    public void setDate( final ZonedDateTime date ) {
        _date = date;
    }

    public PairingMode getPairingMode( ) {
        return _pairingMode;
    }

    public void setPairingMode( final PairingMode pairingMode ) {
        _pairingMode = pairingMode;
    }

    public Address getAddress( ) {
        return _address;
    }

    public void setAddress( final Address address ) {
        _address = address;
    }

    public Player getMainJudge( ) {
        return _mainJudge;
    }

    public void setMainJudge( final Player mainJudge ) {
        _mainJudge = mainJudge;
    }

    public List<Player> getJudges( ) {
        return _judges;
    }

    public void setJudges( final List<Player> judges ) {
        _judges = judges;
    }

    public String getGame( ) {
        return _game;
    }

    public void setGame( final String game ) {
        _game = game;
    }

    public int getMatchDuration( ) {
        return _matchDuration;
    }

    public void setMatchDuration( final int matchDuration ) {
        _matchDuration = matchDuration;
    }

    public int getTeamSize( ) {
        return _teamSize;
    }

    public void setTeamSize( final int teamSize ) {
        _teamSize = teamSize;
    }

    public int getWinningGameCount( ) {
        return _winningGameCount;
    }

    public void setWinningGameCount( final int winningGameCount ) {
        _winningGameCount = winningGameCount;
    }

    public TournamentType getTournamentType( ) {
        return _tournamentType;
    }

    public void setTournamentType( final TournamentType tournamentType ) {
        _tournamentType = tournamentType;
    }

    public TournamentScoringMode getScoringMode( ) {
        return _scoringMode;
    }

    public void setScoringMode( final TournamentScoringMode scoringMode ) {
        _scoringMode = scoringMode;
    }
}
