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
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.home.HomepageView;
import land.tower.core.view.tournament.detail.enrolment.TournamentEnrolmentTab;
import land.tower.core.view.tournament.detail.enrolment.TournamentEnrolmentTabModel;
import land.tower.core.view.tournament.detail.information.TournamentInformationTab;
import land.tower.core.view.tournament.detail.information.TournamentInformationTabModel;
import land.tower.core.view.tournament.detail.round.TournamentRoundTab;
import land.tower.core.view.tournament.detail.round.TournamentRoundTabModel;
import land.tower.data.TournamentStatus;

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
                                final TournamentInformationTabModel.Factory informationTabFactory,
                                final TournamentEnrolmentTabModel.Factory enrolmentTabFactory,
                                final TournamentRoundTabModel.Factory roundTabFactory ) {
        _tournament = tournament;
        _eventBus = eventBus;
        _homeviewProvider = homeviewProvider;
        _i18n = i18n;
        _informationTabFactory = informationTabFactory;
        _enrolmentTabFactory = enrolmentTabFactory;
        _roundTabFactory = roundTabFactory;

        _tabViews.add( new TournamentInformationTab( _informationTabFactory.forTournament( _tournament ) ) );
        _tabViews.add( new TournamentEnrolmentTab( _enrolmentTabFactory.forTournament( _tournament ) ) );
        _tournament.getRounds( ).forEach( round -> {
            final TournamentRoundTabModel roundTabModel = _roundTabFactory.create( _tournament, round );
            _tabViews.add( new TournamentRoundTab( roundTabModel ) );
        } );
        _tournament.getRounds( ).addListener( (ListChangeListener<ObservableRound>) c -> {
            if ( c.next( ) ) {
                if ( c.wasAdded( ) ) {
                    final TournamentRoundTabModel roundTabModel =
                        _roundTabFactory.create( _tournament, c.getAddedSubList( ).get( 0 ) );
                    final TournamentRoundTab roundTab = new TournamentRoundTab( roundTabModel );
                    _tabViews.add( roundTab );
                    _selectedTab.set( roundTab );
                }
                if ( c.wasRemoved( ) ) {
                    final ObservableRound removedRound = c.getRemoved( ).get( 0 );
                    _tabViews.removeIf( v -> {
                        if ( v instanceof TournamentRoundTab ) {
                            return ( (TournamentRoundTab) v ).getModel( ).getRound( ).equals( removedRound );
                        }
                        return false;
                    } );
                }
            }
        } );

        // Select default tab according to status
        if ( tournament.getHeader( ).getStatus( ) == TournamentStatus.ENROLMENT ) {
            _selectedTab.set( _tabViews.get( 1 ) );
        } else if ( tournament.getHeader( ).getStatus( ) == TournamentStatus.STARTED ) {
            _selectedTab.set( _tabViews.get( _tabViews.size( ) - 1 ) );
        } else {
            _selectedTab.set( _tabViews.get( 0 ) );
        }
    }

    void fireHomeButton( ) {
        _eventBus.post( new SceneRequestedEvent( _homeviewProvider.get( ) ) );
    }

    SimpleListProperty<Tab> tabListProperty( ) {
        return new SimpleListProperty<>( _tabViews );
    }

    public Tab getSelectedTab( ) {
        return _selectedTab.get( );
    }

    public SimpleObjectProperty<Tab> selectedTabProperty( ) {
        return _selectedTab;
    }

    private final ObservableTournament _tournament;
    private final EventBus _eventBus;
    private final Provider<HomepageView> _homeviewProvider;
    private final I18nTranslator _i18n;

    private final ObservableList<Tab> _tabViews = FXCollections.observableArrayList( );
    private final TournamentInformationTabModel.Factory _informationTabFactory;
    private final TournamentEnrolmentTabModel.Factory _enrolmentTabFactory;
    private final TournamentRoundTabModel.Factory _roundTabFactory;

    private final SimpleObjectProperty<Tab> _selectedTab = new SimpleObjectProperty<>( );
}
