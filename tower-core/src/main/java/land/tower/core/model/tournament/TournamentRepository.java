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

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.synchronizedObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.ObservableList;
import javax.inject.Inject;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.service.IService;

/**
 * Created on 16/12/2017
 * @author Cédric Longo
 */
public final class TournamentRepository implements IService {

    @Inject
    public TournamentRepository( final TournamentStorage storage ) {
        _storage = storage;
    }

    @Override
    public void start( ) {
        _storage.loadTournaments( )
                .stream( )
                .map( ObservableTournament::new )
                .forEach( _tournaments::add );
        _logger.info( "{} tournaments loaded from storage", _tournaments.size( ) );
    }

    @Override
    public void stop( ) {

    }

    public ObservableList<ObservableTournament> getTournamentList( ) {
        return _tournaments;
    }

    private final ObservableList<ObservableTournament> _tournaments =
        synchronizedObservableList( observableArrayList( ) );

    private final TournamentStorage _storage;

    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
