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

import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Tab;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.thread.ApplicationThread;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.model.tournament.TournamentRepository;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.event.TournamentRoundTabDisplayedEvent;
import land.tower.core.view.home.HomepageView;
import land.tower.core.view.tournament.detail.enrolment.TournamentEnrolmentTab;
import land.tower.core.view.tournament.detail.enrolment.TournamentEnrolmentTabModel;
import land.tower.core.view.tournament.detail.information.TournamentInformationTab;
import land.tower.core.view.tournament.detail.information.TournamentInformationTabModel;
import land.tower.core.view.tournament.detail.ladder.TournamentLadderView;
import land.tower.core.view.tournament.detail.ladder.TournamentLadderViewModel;
import land.tower.core.view.tournament.detail.round.TournamentRoundTab;
import land.tower.core.view.tournament.detail.round.TournamentRoundTabFactory;
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
                                final TournamentRoundTabFactory roundTabFactory,
                                final TournamentLadderViewModel.Factory ladderTabFactory,
                                final TournamentRepository tournamentRepository,
                                final TournamentViewProvider tournamentViewProvider,
                                @ApplicationThread final ScheduledExecutorService scheduler ) {
        _tournament = tournament;
        _eventBus = eventBus;
        _homeviewProvider = homeviewProvider;
        _i18n = i18n;
        _informationTabFactory = informationTabFactory;
        _enrolmentTabFactory = enrolmentTabFactory;
        _roundTabFactory = roundTabFactory;
        _ladderTabFactory = ladderTabFactory;
        _tournamentRepository = tournamentRepository;
        _tournamentViewProvider = tournamentViewProvider;
        _scheduler = scheduler;

        _tabViews.add( new TournamentInformationTab( _informationTabFactory.forTournament( _tournament ) ) );
        _tabViews.add( new TournamentEnrolmentTab( _enrolmentTabFactory.forTournament( _tournament ) ) );
        _tournament.getRounds( ).forEach( round -> {
            _tabViews.add( _roundTabFactory.create( _tournament, round ) );
        } );
        _tournament.getRounds( ).addListener( (ListChangeListener<ObservableRound>) c -> {
            if ( c.next( ) ) {
                if ( c.wasAdded( ) ) {
                    final TournamentRoundTab roundTab = _roundTabFactory
                                                            .create( _tournament, c.getAddedSubList( ).get( 0 ) );
                    _tabViews.add( _tabViews.size( ) - 1, roundTab );
                    selectDefaultTab( );
                }
                if ( c.wasRemoved( ) ) {
                    final ObservableRound removedRound = c.getRemoved( ).get( 0 );
                    _tabViews.removeIf( v -> {
                        if ( v instanceof TournamentRoundTab ) {
                            return ( (TournamentRoundTab) v ).getModel( ).getRound( ).equals( removedRound );
                        }
                        return false;
                    } );
                    selectDefaultTab( );
                }
            }
        } );
        _tabViews.add( new TournamentLadderView( _ladderTabFactory.forTournament( _tournament ) ) );
        _selectedTab.addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue instanceof TournamentRoundTab ) {
                _eventBus.post( new TournamentRoundTabDisplayedEvent( ( (TournamentRoundTab) newValue ) ) );
            } else {
                _eventBus.post( new TournamentRoundTabDisplayedEvent( null ) );
            }
        } );

        selectDefaultTab( );
        startTimerTask( );
    }

    private void selectDefaultTab( ) {
        _selectedTab.set( null );
        if ( _tournament.getHeader( ).titleProperty( ).isEmpty( ).get( ) ) {
            _selectedTab.set( _tabViews.get( 0 ) );
            return;
        }

        // Select default tab according to status
        if ( _tournament.getHeader( ).getStatus( ) == TournamentStatus.ENROLMENT ) {
            _selectedTab.set( _tabViews.get( 1 ) );
        } else if ( _tournament.getHeader( ).getStatus( ) == TournamentStatus.STARTED ) {
            _selectedTab.set( _tabViews.get( _tabViews.size( ) - 2 ) );
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

    public ObservableValue<ObservableList<ObservableTournament>> getOpenedTournaments( ) {
        final ObservableList<ObservableTournament> tournamentList = _tournamentRepository.getTournamentList( );

        final FilteredList<ObservableTournament> filteredTournaments = new FilteredList<>( tournamentList );
        filteredTournaments.setPredicate( t -> t.getHeader( ).getStatus( ) != TournamentStatus.CLOSED );

        final SortedList<ObservableTournament> sortedTournaments = new SortedList<>( filteredTournaments );
        sortedTournaments.setComparator( Comparator.comparing( t -> t.getHeader( ).getDate( ) ) );

        return new SimpleListProperty<>( sortedTournaments );
    }

    public void fireTournamentSelection( final ObservableTournament tournament ) {
        _eventBus.post( new SceneRequestedEvent( _tournamentViewProvider.forTournament( tournament ) ) );
    }

    private void startTimerTask( ) {
        _scheduler.scheduleWithFixedDelay( ( ) -> {
            try {
                _tournament.getRounds( )
                           .stream( )
                           .filter( r -> !r.isEnded( ) )
                           .forEach( round -> {
                               round.getTimer( ).update( );
                           } );
            } catch ( final Exception e ) {
                LoggerFactory.getLogger( Loggers.MAIN )
                             .error( "Error during updating clock schedule task", e );
            }
        }, 1, 1, TimeUnit.SECONDS );
    }

    private final ObservableTournament _tournament;
    private final EventBus _eventBus;
    private final Provider<HomepageView> _homeviewProvider;
    private final I18nTranslator _i18n;

    private final ObservableList<Tab> _tabViews = FXCollections.observableArrayList( );
    private final TournamentInformationTabModel.Factory _informationTabFactory;
    private final TournamentEnrolmentTabModel.Factory _enrolmentTabFactory;
    private final TournamentRoundTabFactory _roundTabFactory;
    private final TournamentLadderViewModel.Factory _ladderTabFactory;

    private final SimpleObjectProperty<Tab> _selectedTab = new SimpleObjectProperty<>( );
    private final TournamentRepository _tournamentRepository;

    private final TournamentViewProvider _tournamentViewProvider;

    private final ScheduledExecutorService _scheduler;
}
