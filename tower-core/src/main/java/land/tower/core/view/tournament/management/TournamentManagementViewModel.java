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

package land.tower.core.view.tournament.management;

import com.google.common.eventbus.EventBus;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.model.tournament.TournamentRepository;
import land.tower.core.view.event.InformationEvent;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.home.HomepageView;
import land.tower.core.view.tournament.detail.TournamentView;
import land.tower.core.view.tournament.detail.TournamentViewProvider;
import land.tower.data.Tournament;

/**
 * Created on 17/12/2017
 * @author Cédric Longo
 */
final class TournamentManagementViewModel {

    @Inject
    TournamentManagementViewModel( final EventBus eventBus, final I18nTranslator i18n,
                                   final Provider<HomepageView> homepageViewProvider,
                                   final TournamentRepository tournamentRepository,
                                   final TournamentViewProvider tournamentViewProvider, final Stage owner ) {
        _i18n = i18n;
        _eventBus = eventBus;
        _homepageViewProvider = homepageViewProvider;
        _tournamentRepository = tournamentRepository;
        _tournamentViewProvider = tournamentViewProvider;
        _owner = owner;
        _eventBus.register( this );
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    void fireHomeButton( ) {
        _eventBus.post( new SceneRequestedEvent( _homepageViewProvider.get( ) ) );
    }

    ObservableValue<ObservableList<ObservableTournament>> tournamentListProperty( ) {
        return new SimpleListProperty<>( _tournamentRepository.getTournamentList( ) );
    }

    void fireTournamentDeleted( final Tournament tournament ) {
        _tournamentRepository.remove( tournament );
        _eventBus.post( new InformationEvent( _i18n.get( "tournament.deleted" ) ) );
    }

    void fireTournamentCreation( ) {
        final ObservableTournament tournament = _tournamentRepository.create( );
        fireTournamentDisplay( tournament );
    }

    void fireTournamentDisplay( final ObservableTournament tournament ) {
        final TournamentView view = _tournamentViewProvider.forTournament( tournament );
        _eventBus.post( new SceneRequestedEvent( view ) );
    }

    public Stage getOwner( ) {
        return _owner;
    }

    private final EventBus _eventBus;
    private final I18nTranslator _i18n;

    private final Provider<HomepageView> _homepageViewProvider;
    private final TournamentRepository _tournamentRepository;
    private final TournamentViewProvider _tournamentViewProvider;
    private final Stage _owner;
}
