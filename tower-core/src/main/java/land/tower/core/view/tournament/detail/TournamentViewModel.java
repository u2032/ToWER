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

package land.tower.core.view.tournament.detail;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.home.HomepageView;
import land.tower.core.view.tournament.detail.information.TournamentInformationTab;
import land.tower.core.view.tournament.detail.information.TournamentInformationTabModel;

/**
 * Created on 19/12/2017
 * @author Cédric Longo
 */
public final class TournamentViewModel {

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public interface Factory {

        TournamentViewModel forTournament( final ObservableTournament tournament );
    }

    @Inject
    public TournamentViewModel( @Assisted final ObservableTournament tournament,
                                final EventBus eventBus,
                                final Provider<HomepageView> homeviewProvider,
                                final I18nTranslator i18n,
                                final TournamentInformationTabModel.Factory informationTabFactory ) {
        _tournament = tournament;
        _eventBus = eventBus;
        _homeviewProvider = homeviewProvider;
        _i18n = i18n;
        _informationTabFactory = informationTabFactory;

        _tabViews.add( new TournamentInformationTab( _informationTabFactory.forTournament( _tournament ) ) );
    }

    void fireHomeButton( ) {
        _eventBus.post( new SceneRequestedEvent( _homeviewProvider.get( ) ) );
    }

    SimpleListProperty<Tab> tabListProperty( ) {
        return new SimpleListProperty<>( _tabViews );
    }

    private final ObservableTournament _tournament;
    private final EventBus _eventBus;
    private final Provider<HomepageView> _homeviewProvider;
    private final I18nTranslator _i18n;

    private final ObservableList<Tab> _tabViews = FXCollections.observableArrayList( );
    private final TournamentInformationTabModel.Factory _informationTabFactory;
}
