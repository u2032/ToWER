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

package land.tower.core.view.main;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javafx.beans.property.SimpleObjectProperty;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.i18n.I18nService;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.model.tournament.TournamentRepository;
import land.tower.core.view.about.AboutDialog;
import land.tower.core.view.event.CloseRequestEvent;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.home.HomepageView;
import land.tower.core.view.option.LanguageDialog;
import land.tower.core.view.tournament.detail.TournamentView;
import land.tower.core.view.tournament.detail.TournamentViewModelProvider;
import land.tower.core.view.tournament.detail.round.DeleteRoundDialogModel;
import land.tower.core.view.tournament.detail.round.DeleteRoundDialogModel.Factory;

/**
 * Created on 18/12/2017
 * @author Cédric Longo
 */
final class ApplicationMenuBarModel {

    @Inject
    ApplicationMenuBarModel( final EventBus eventBus, final I18nTranslator i18n,
                             final Provider<HomepageView> homepageViewProvider,
                             final TournamentRepository tournamentRepository,
                             final TournamentViewModelProvider tournamentViewModelProvider,
                             final Provider<AboutDialog> aboutDialogProvider,
                             final Provider<LanguageDialog> languageDialogProvider,
                             final Factory resertRoundDialogFactory,
                             final I18nService i18nService ) {
        _eventBus = eventBus;
        _languageDialogProvider = languageDialogProvider;
        _resertRoundDialogFactory = resertRoundDialogFactory;
        _i18nService = i18nService;
        _eventBus.register( this );
        _i18n = i18n;
        _homepageViewProvider = homepageViewProvider;
        _tournamentRepository = tournamentRepository;
        _tournamentViewModelProvider = tournamentViewModelProvider;
        _aboutDialogProvider = aboutDialogProvider;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    void fireCloseRequest( ) {
        _eventBus.post( new CloseRequestEvent( ) );
    }

    void fireHomeRequest( ) {
        _eventBus.post( new SceneRequestedEvent( _homepageViewProvider.get( ) ) );
    }

    void fireTournamentCreation( ) {
        final ObservableTournament tournament = _tournamentRepository.create( );
        final TournamentView view = new TournamentView( _tournamentViewModelProvider.forTournament( tournament ) );
        _eventBus.post( new SceneRequestedEvent( view ) );
    }

    void fireAboutDialog( ) {
        _aboutDialogProvider.get( ).show( );
    }

    @Subscribe
    private void sceneRequested( final SceneRequestedEvent event ) {
        if ( event.getView( ) instanceof TournamentView ) {
            final TournamentView view = ( (TournamentView) event.getView( ) );
            _currentTournament.set( view.getModel( ).getTournament( ) );
        } else {
            _currentTournament.set( null );
        }
    }

    DeleteRoundDialogModel createResetRoundDialogModel( ) {
        return _resertRoundDialogFactory.forTournament( _currentTournament.get( ) );
    }

    public ObservableTournament getCurrentTournament( ) {
        return _currentTournament.get( );
    }

    public SimpleObjectProperty<ObservableTournament> currentTournamentProperty( ) {
        return _currentTournament;
    }

    public void fireLanguageSelection( ) {
        _languageDialogProvider.get( )
                               .showAndWait( )
                               .ifPresent( lang -> {
                                   _i18nService.loadAllBundles( lang.getCode( ), null );
                               } );
    }

    private final EventBus _eventBus;
    private final I18nTranslator _i18n;
    private final Provider<HomepageView> _homepageViewProvider;
    private final TournamentRepository _tournamentRepository;

    private final TournamentViewModelProvider _tournamentViewModelProvider;
    private final Provider<AboutDialog> _aboutDialogProvider;
    private final Provider<LanguageDialog> _languageDialogProvider;

    private final SimpleObjectProperty<ObservableTournament> _currentTournament = new SimpleObjectProperty<>( );
    private final Factory _resertRoundDialogFactory;
    private final I18nService _i18nService;
}
