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

package land.tower.core.model.tournament;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import land.tower.core.view.player.ObservablePlayer;
import land.tower.data.PairingMode;
import land.tower.data.TournamentHeader;
import land.tower.data.TournamentScoringMode;
import land.tower.data.TournamentStatus;
import land.tower.data.TournamentType;

/**
 * Created on 17/12/2017
 * @author Cédric Longo
 */
public final class ObservableTournamentHeader {

    public ObservableTournamentHeader( final TournamentHeader header ) {
        _header = header;

        _title = new SimpleStringProperty( header.getTitle( ) );
        _title.addListener( ( obs, oldValue, newValue ) -> header.setTitle( newValue ) );
        _title.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _game = new SimpleStringProperty( header.getGame( ) );
        _game.addListener( ( obs, oldValue, newValue ) -> header.setGame( newValue ) );
        _game.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _date = new SimpleObjectProperty<>( header.getDate( ) );
        _date.addListener( ( obs, oldV, newV ) -> header.setDate( newV ) );
        _date.addListener( ( obs, oldV, newV ) -> _dirty.set( true ) );

        _status = new SimpleObjectProperty<>( header.getStatus( ) );
        _status.addListener( ( ( observable, oldValue, newValue ) -> header.setStatus( newValue ) ) );
        _status.addListener( ( ( observable, oldValue, newValue ) -> _dirty.set( true ) ) );

        _pairingMode = new SimpleObjectProperty<>( header.getPairingMode( ) );
        _pairingMode.addListener( ( ( observable, oldValue, newValue ) -> header.setPairingMode( newValue ) ) );
        _pairingMode.addListener( ( ( observable, oldValue, newValue ) -> _dirty.set( true ) ) );

        _matchDuration = new SimpleObjectProperty<>( header.getMatchDuration( ) );
        _matchDuration.addListener( ( obs, oldValue, newValue ) ->
                                        header.setMatchDuration( newValue != null ? newValue : 30 ) );
        _matchDuration.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _teamSize = new SimpleObjectProperty<>( header.getTeamSize( ) );
        _teamSize.addListener( ( obs, oldValue, newValue ) -> header.setTeamSize( newValue != null ? newValue : 1 ) );
        _teamSize.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _scoreMax = new SimpleObjectProperty<>( header.getScoreMax( ) );
        _scoreMax.addListener(
            ( obs, oldValue, newValue ) -> header.setScoreMax( newValue != null ? newValue : 1 ) );
        _scoreMax.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _scoreMaxBis = new SimpleObjectProperty<>( header.getScoreMaxBis( ) );
        _scoreMaxBis.addListener(
            ( obs, oldValue, newValue ) -> header.setScoreMaxBis( newValue != null ? newValue : 1 ) );
        _scoreMaxBis.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _doubleScore = new SimpleBooleanProperty( header.isDoubleScore( ) );
        _doubleScore.addListener(
            ( obs, oldValue, newValue ) -> header.setDoubleScore( newValue != null ? newValue : false ) );
        _doubleScore.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _type = new SimpleObjectProperty<>( header.getTournamentType( ) );
        _type.addListener( ( obs, oldValue, newValue ) -> header.setTournamentType( newValue ) );
        _type.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _scoringMode = new SimpleObjectProperty<>( header.getScoringMode( ) );
        _scoringMode.addListener( ( obs, oldValue, newValue ) -> header.setScoringMode( newValue ) );
        _scoringMode.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _address = new ObservableAddress( _header.getAddress( ) );
        _address.dirtyProperty( )
                .addListener( ( observable, oldValue, newValue ) -> _dirty.set( isDirty( ) || newValue ) );

        _mainJudge = new SimpleObjectProperty<>( header.getMainJudge( ) != null ?
                                                 new ObservablePlayer( header.getMainJudge( ) ) : null );
        _mainJudge.addListener(
            ( obs, oldValue, newValue ) -> header.setMainJudge( newValue == null ? null : newValue.getPlayer( ) ) );
        _mainJudge.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        header.getJudges( )
              .forEach( judge -> _judges.add( new ObservablePlayer( judge ) ) );
        _judges.addListener( (ListChangeListener<ObservablePlayer>) c -> {
            header.setJudges( _judges.stream( )
                                     .map( ObservablePlayer::getPlayer )
                                     .collect( Collectors.toList( ) ) );
        } );
        _judges.addListener( (ListChangeListener<ObservablePlayer>) c -> _dirty.set( true ) );

        _dirty.setValue( false );
    }

    public String getTitle( ) {
        return _title.get( );
    }

    public SimpleStringProperty titleProperty( ) {
        return _title;
    }

    public ZonedDateTime getDate( ) {
        return _date.get( );
    }

    public SimpleObjectProperty<ZonedDateTime> dateProperty( ) {
        return _date;
    }

    public void setDate( final ZonedDateTime date ) {
        this._date.set( date );
    }

    public TournamentStatus getStatus( ) {
        return _status.get( );
    }

    public SimpleObjectProperty<TournamentStatus> statusProperty( ) {
        return _status;
    }

    public void setStatus( final TournamentStatus status ) {
        this._status.set( status );
    }

    public PairingMode getPairingMode( ) {
        return _pairingMode.get( );
    }

    public SimpleObjectProperty<PairingMode> pairingModeProperty( ) {
        return _pairingMode;
    }

    public boolean isDirty( ) {
        return _dirty.get( );
    }

    public SimpleBooleanProperty dirtyProperty( ) {
        return _dirty;
    }

    public TournamentHeader getHeader( ) {
        return _header;
    }

    public ObservableAddress getAddress( ) {
        return _address;
    }

    public int getMatchDuration( ) {
        return _matchDuration.get( );
    }

    public SimpleObjectProperty<Integer> matchDurationProperty( ) {
        return _matchDuration;
    }

    public int getTeamSize( ) {
        return _teamSize.get( );
    }

    public SimpleObjectProperty<Integer> teamSizeProperty( ) {
        return _teamSize;
    }

    public Integer getScoreMax( ) {
        return _scoreMax.get( );
    }

    public SimpleObjectProperty<Integer> scoreMaxProperty( ) {
        return _scoreMax;
    }

    public void setTitle( final String title ) {
        this._title.set( title );
    }

    public void setPairingMode( final PairingMode pairingMode ) {
        this._pairingMode.set( pairingMode );
    }

    public void setMatchDuration( final Integer matchDuration ) {
        this._matchDuration.set( matchDuration );
    }

    public void setTeamSize( final Integer teamSize ) {
        this._teamSize.set( teamSize );
    }

    public String getGame( ) {
        return _game.get( );
    }

    public SimpleStringProperty gameProperty( ) {
        return _game;
    }

    public void setGame( final String game ) {
        this._game.set( game );
    }

    public void setScoreMax( final Integer scoreMax ) {
        this._scoreMax.set( scoreMax );
    }

    public void setType( final TournamentType type ) {
        this._type.set( type );
    }

    public TournamentType getType( ) {
        return _type.get( );
    }

    public SimpleObjectProperty<TournamentType> typeProperty( ) {
        return _type;
    }

    public TournamentScoringMode getScoringMode( ) {
        return _scoringMode.get( );
    }

    public SimpleObjectProperty<TournamentScoringMode> scoringModeProperty( ) {
        return _scoringMode;
    }

    public void setScoringMode( final TournamentScoringMode scoringMode ) {
        this._scoringMode.set( scoringMode );
    }

    public ObservableList<ObservablePlayer> getJudges( ) {
        return _judges;
    }

    public ObservablePlayer getMainJudge( ) {
        return _mainJudge.get( );
    }

    public void setMainJudge( final ObservablePlayer mainJudge ) {
        this._mainJudge.set( mainJudge );
    }

    public SimpleObjectProperty<ObservablePlayer> mainJudgeProperty( ) {
        return _mainJudge;
    }

    public Boolean getDoubleScore( ) {
        return _doubleScore.get( );
    }

    public SimpleBooleanProperty doubleScoreProperty( ) {
        return _doubleScore;
    }

    public Integer getScoreMaxBis( ) {
        return _scoreMaxBis.get( );
    }

    public SimpleObjectProperty<Integer> scoreMaxBisProperty( ) {
        return _scoreMaxBis;
    }

    public void setDoubleScore( final Boolean doubleScore ) {
        this._doubleScore.set( doubleScore );
    }

    public void setScoreMaxBis( final Integer scoreMaxBis ) {
        this._scoreMaxBis.set( scoreMaxBis );
    }

    public void markAsClean( ) {
        _dirty.set( false );
        _address.markAsClean( );
    }

    public ObservableValue<ObservableList<ObservablePlayer>> judgesProperty( ) {
        return new SimpleListProperty<>( _judges );
    }

    private final SimpleStringProperty _title;
    private final SimpleStringProperty _game;
    private final SimpleObjectProperty<ZonedDateTime> _date;
    private final SimpleObjectProperty<TournamentStatus> _status;
    private final SimpleObjectProperty<TournamentType> _type;
    private final SimpleObjectProperty<TournamentScoringMode> _scoringMode;
    private final SimpleObjectProperty<PairingMode> _pairingMode;
    private final SimpleObjectProperty<Integer> _matchDuration;
    private final SimpleObjectProperty<Integer> _teamSize;
    private final SimpleObjectProperty<Integer> _scoreMax;
    private final SimpleBooleanProperty _doubleScore;
    private final SimpleObjectProperty<Integer> _scoreMaxBis;
    private final ObservableAddress _address;
    private final SimpleObjectProperty<ObservablePlayer> _mainJudge;
    private final ObservableList<ObservablePlayer> _judges = FXCollections.observableArrayList( );

    private final SimpleBooleanProperty _dirty = new SimpleBooleanProperty( );

    private final TournamentHeader _header;
}
