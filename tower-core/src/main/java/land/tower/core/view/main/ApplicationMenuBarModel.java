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

import java.io.File;
import java.util.Optional;
import java.util.Set;
import javafx.application.HostServices;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.i18n.I18nService;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.preference.Preferences;
import land.tower.core.model.player.PlayerRepository;
import land.tower.core.model.player.suggestion.IPlayerSuggestionProvider;
import land.tower.core.model.tournament.ITournamentStorage;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.model.tournament.TournamentRepository;
import land.tower.core.view.about.AboutDialog;
import land.tower.core.view.event.CloseRequestEvent;
import land.tower.core.view.event.InformationEvent;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.home.HomepageView;
import land.tower.core.view.option.LanguageDialog;
import land.tower.core.view.player.AddPlayerDialogModel;
import land.tower.core.view.player.PlayerManagementView;
import land.tower.core.view.tournament.detail.TournamentView;
import land.tower.core.view.tournament.detail.TournamentViewProvider;
import land.tower.core.view.tournament.detail.round.DeleteRoundDialogModel;
import land.tower.core.view.tournament.detail.round.DeleteRoundDialogModel.Factory;
import land.tower.core.view.tournament.management.TournamentManagementView;
import land.tower.core.view.tournament.radiator.RadiatorDialogFactory;
import land.tower.data.Player;
import land.tower.data.Tournament;

/**
 * Created on 18/12/2017
 * @author Cédric Longo
 */
final class ApplicationMenuBarModel {

    @Inject
    ApplicationMenuBarModel( final EventBus eventBus, final I18nTranslator i18n,
                             final Provider<HomepageView> homepageViewProvider,
                             final Provider<TournamentManagementView> tournamentManagementViewProvider,
                             final Provider<PlayerManagementView> playerManagementViewProvider,
                             final TournamentRepository tournamentRepository,
                             final TournamentViewProvider tournamentViewProvider,
                             final Provider<AboutDialog> aboutDialogProvider,
                             final Provider<LanguageDialog> languageDialogProvider,
                             final Provider<AddPlayerDialogModel> addPlayerDialogModelProvider,
                             final RadiatorDialogFactory radiatorDialogFacotry,
                             final Factory resertRoundDialogFactory,
                             final I18nService i18nService,
                             final Preferences preferences,
                             final Configuration configuration, final HostServices hostServices,
                             final PlayerRepository playerRepository,
                             final Provider<Stage> owner,
                             final ITournamentStorage tournamentStorage,
                             final Set<Menu> extraMenus,
                             final IPlayerSuggestionProvider playerSuggestionProvider ) {
        _eventBus = eventBus;
        _tournamentManagementViewProvider = tournamentManagementViewProvider;
        _playerManagementViewProvider = playerManagementViewProvider;
        _languageDialogProvider = languageDialogProvider;
        _addPlayerDialogModelProvider = addPlayerDialogModelProvider;
        _radiatorDialogFacotry = radiatorDialogFacotry;
        _resertRoundDialogFactory = resertRoundDialogFactory;
        _i18nService = i18nService;
        _preferences = preferences;
        _configuration = configuration;
        _hostServices = hostServices;
        _playerRepository = playerRepository;
        _owner = owner;
        _tournamentStorage = tournamentStorage;
        _extraMenus = extraMenus;
        _playerSuggestionProvider = playerSuggestionProvider;
        _eventBus.register( this );
        _i18n = i18n;
        _homepageViewProvider = homepageViewProvider;
        _tournamentRepository = tournamentRepository;
        _tournamentViewProvider = tournamentViewProvider;
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
        final TournamentView view = _tournamentViewProvider.forTournament( tournament );
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
                                   _preferences.save( "language", lang.getCode( ) );
                                   _eventBus.post( new InformationEvent( _i18n.get( "information.saved" ) ) );
                               } );
    }

    public void fireOpenDocumentation( ) {
        _hostServices.showDocument( _configuration.get( "documentation.url" ) );
    }

    public void fireTournamentManagement( ) {
        _eventBus.post( new SceneRequestedEvent( _tournamentManagementViewProvider.get( ) ) );
    }

    public void firePlayerManagement( ) {
        _eventBus.post( new SceneRequestedEvent( _playerManagementViewProvider.get( ) ) );
    }

    public AddPlayerDialogModel newAddPlayerDialogModel( ) {
        return _addPlayerDialogModelProvider.get( );
    }

    public void firePlayerCreated( final Player player ) {
        _playerSuggestionProvider.getSuggestionsForNumero( player.getNumero( ) )
                                 .forEach( p -> {
                                     if ( p.getNumero( ) == player.getNumero( ) ) {
                                         player.setLastname( p.getLastname( ) );
                                         player.setFirstname( p.getFirstname( ) );
                                         player.setBirthday( p.getBirthday( ) );
                                         player.setNationality( p.getNationality( ) );
                                     }
                                 } );
        _playerRepository.registerPlayer( player );
        _eventBus.post( new InformationEvent( _i18n.get( "player.created" ) ) );
        _preferences.save( "player.nationality", player.getNationality( ).name( ) );
    }

    public void fireTournamentInformationRadiator( final Window owner ) {
        _radiatorDialogFacotry.create( owner, _currentTournament.get( ) ).show( );
    }

    public void fireFileExport( ) {
        final FileChooser fileChooser = new FileChooser( );
        fileChooser.setTitle( _i18n.get( "file.chooser.export" ).getValue( ) );
        fileChooser.getExtensionFilters( )
                   .addAll( new ExtensionFilter( "Tower Files", "*.twr" ) );
        fileChooser.setInitialDirectory( new File( System.getProperty( "user.home" ) ) );
        fileChooser.setInitialFileName( _currentTournament.get( ).getTournament( ).getId( ) + ".twr" );
        final File file = fileChooser.showSaveDialog( _owner.get( ) );
        if ( file != null ) {
            _tournamentStorage.saveTournament( _currentTournament.get( ), file.toPath( ) );
            _eventBus.post( new InformationEvent( _i18n.get( "tournament.exported" ) ) );
        }
    }

    public void fireFileImport( ) {
        final FileChooser fileChooser = new FileChooser( );
        fileChooser.setTitle( _i18n.get( "file.chooser.import" ).getValue( ) );
        fileChooser.getExtensionFilters( )
                   .addAll( new ExtensionFilter( "Tower Files", "*.twr" ) );
        fileChooser.setInitialDirectory( new File( System.getProperty( "user.home" ) ) );
        final File file = fileChooser.showOpenDialog( _owner.get( ) );
        if ( file != null ) {
            final Tournament tournament = _tournamentStorage.loadTournament( file.toPath( ) );
            if ( tournament != null ) {
                if ( !_configuration.get( "app.name" ).equals( tournament.getFlags( ).get( "app.name" ) ) ) {
                    _eventBus.post( new InformationEvent( _i18n.get( "tournament.imported.app.mismatch" ) ) );
                    return;
                }

                final Optional<ObservableTournament> existing =
                    _tournamentRepository.getTournament( tournament.getId( ) );
                if ( !existing.isPresent( ) ) {
                    final ObservableTournament otournament = new ObservableTournament( tournament );
                    _tournamentRepository.add( otournament );
                    _eventBus.post( new SceneRequestedEvent( _tournamentViewProvider.forTournament( otournament ) ) );
                    _eventBus.post( new InformationEvent( _i18n.get( "tournament.imported" ) ) );

                } else {
                    final Alert alert = new Alert( AlertType.WARNING );
                    alert.initOwner( _owner.get( ) );
                    alert.headerTextProperty( ).bind( _i18n.get( "tournament.import.existing.warning" ) );
                    alert.setContentText( _i18n.get( "tournament.import.existing.warning.message",
                                                     existing.get( ).getHeader( ).getTitle( ) ) );

                    final ButtonType eraseButtonType =
                        new ButtonType( _i18n.get( "action.erase" ).get( ).toUpperCase( ), ButtonData.APPLY );
                    final ButtonType cancelButtonType =
                        new ButtonType( _i18n.get( "action.cancel" ).get( ).toUpperCase( ), ButtonData.CANCEL_CLOSE );
                    alert.getDialogPane( ).getButtonTypes( ).setAll( eraseButtonType, cancelButtonType );

                    final Button cancelButton = (Button) alert.getDialogPane( ).lookupButton( cancelButtonType );
                    cancelButton.setDefaultButton( true );

                    final Button eraseButton = (Button) alert.getDialogPane( ).lookupButton( eraseButtonType );
                    eraseButton.setDefaultButton( false );

                    final Optional<ButtonType> clicked = alert.showAndWait( );
                    if ( clicked.isPresent( ) && clicked.get( ) == eraseButtonType ) {
                        _tournamentViewProvider.removeTournamentView( tournament.getId( ) );
                        _tournamentRepository.delete( tournament.getId( ) );
                        final ObservableTournament otournament = new ObservableTournament( tournament );
                        _tournamentRepository.add( otournament );
                        _eventBus.post( new SceneRequestedEvent(
                            _tournamentViewProvider.forTournament( otournament ) ) );
                        _eventBus.post( new InformationEvent( _i18n.get( "tournament.imported" ) ) );
                    }
                }
            } else {
                _eventBus.post( new InformationEvent( _i18n.get( "tournament.imported.failure" ) ) );
            }
        }
    }

    public Set<Menu> extraMenus( ) {
        return _extraMenus;
    }

    private final EventBus _eventBus;
    private final I18nTranslator _i18n;
    private final Provider<HomepageView> _homepageViewProvider;
    private final Provider<TournamentManagementView> _tournamentManagementViewProvider;
    private final Provider<PlayerManagementView> _playerManagementViewProvider;
    private final TournamentRepository _tournamentRepository;

    private final TournamentViewProvider _tournamentViewProvider;
    private final Provider<AboutDialog> _aboutDialogProvider;
    private final Provider<LanguageDialog> _languageDialogProvider;

    private final Provider<AddPlayerDialogModel> _addPlayerDialogModelProvider;
    private final RadiatorDialogFactory _radiatorDialogFacotry;

    private final SimpleObjectProperty<ObservableTournament> _currentTournament = new SimpleObjectProperty<>( );
    private final Factory _resertRoundDialogFactory;
    private final I18nService _i18nService;

    private final Preferences _preferences;
    private final Configuration _configuration;
    private final HostServices _hostServices;
    private final PlayerRepository _playerRepository;
    private final Provider<Stage> _owner;

    private final ITournamentStorage _tournamentStorage;
    private final Set<Menu> _extraMenus;
    private final IPlayerSuggestionProvider _playerSuggestionProvider;
}


