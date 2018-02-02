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

import javafx.beans.binding.Bindings;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Window;
import javax.inject.Inject;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.view.component.FaMenu;
import land.tower.core.view.component.FaMenuItem;
import land.tower.core.view.player.AddPlayerDialog;
import land.tower.core.view.tournament.detail.round.DeleteRoundDialog;
import land.tower.data.TournamentStatus;

/**
 * Created on 12/11/2017
 * @author Cédric Longo
 */
final class ApplicationMenuBar extends MenuBar {

    @Inject
    public ApplicationMenuBar( final ApplicationMenuBarModel model ) {
        _model = model;
        getMenus( ).add( fileMenu( ) );
        getMenus( ).add( playerMenu( ) );
        getMenus( ).add( tournamentMenu( ) );
        getMenus( ).add( optionMenu( ) );
        getMenus( ).add( aboutMenu( ) );
    }

    private Menu fileMenu( ) {
        final Menu fileMenu = new Menu( );
        fileMenu.textProperty( ).bind( _model.getI18n( ).get( "menu.file" ) );

        final MenuItem homepageMenu = new FaMenuItem( FontAwesome.HOME, "black" );
        homepageMenu.textProperty( ).bind( Bindings.concat( _model.getI18n( ).get( "menu.home" ) ) );
        homepageMenu.setOnAction( event -> _model.fireHomeRequest( ) );
        fileMenu.getItems( ).add( homepageMenu );

        fileMenu.getItems( ).add( new SeparatorMenuItem( ) );

        final MenuItem exitMenu = new FaMenuItem( FontAwesome.OFF, "black" );
        exitMenu.textProperty( ).bind( _model.getI18n( ).get( "menu.file.exit" ) );
        exitMenu.setOnAction( event -> _model.fireCloseRequest( ) );
        fileMenu.getItems( ).add( exitMenu );

        return fileMenu;
    }

    private Menu tournamentMenu( ) {
        final Menu tournamentMenu = new Menu( );
        tournamentMenu.textProperty( ).bind( _model.getI18n( ).get( "menu.tournament" ) );

        final MenuItem addMenu = new FaMenuItem( FontAwesome.PLUS, "black" );
        addMenu.textProperty( ).bind( _model.getI18n( ).get( "menu.add.tournament" ) );
        addMenu.setOnAction( event -> _model.fireTournamentCreation( ) );
        tournamentMenu.getItems( ).add( addMenu );

        final MenuItem listMenu = new FaMenuItem( FontAwesome.LIST, "black" );
        listMenu.textProperty( ).bind( _model.getI18n( ).get( "tournament.management.title" ) );
        listMenu.setOnAction( event -> _model.fireTournamentManagement( ) );
        tournamentMenu.getItems( ).add( listMenu );

        tournamentMenu.getItems( ).add( new SeparatorMenuItem( ) );

        final MenuItem importMenu = new FaMenuItem( FontAwesome.IMPORT, "black" );
        importMenu.textProperty( ).bind( _model.getI18n( ).get( "tournament.import" ) );
        importMenu.setOnAction( event -> _model.fireFileImport( ) );
        tournamentMenu.getItems( ).add( importMenu );

        final MenuItem exportMenu = new FaMenuItem( FontAwesome.EXPORT, "black" );
        exportMenu.textProperty( ).bind( _model.getI18n( ).get( "tournament.export" ) );
        exportMenu.setOnAction( event -> _model.fireFileExport( ) );
        exportMenu.disableProperty( ).bind( _model.currentTournamentProperty( ).isNull( ) );
        tournamentMenu.getItems( ).add( exportMenu );

        tournamentMenu.getItems( ).add( new SeparatorMenuItem( ) );

        final MenuItem radiatorMenu = new FaMenuItem( FontAwesome.SCREEN, "black" );
        radiatorMenu.textProperty( ).bind( _model.getI18n( ).get( "tournament.radiator.information" ) );
        final ApplicationMenuBar menuBar = this;
        radiatorMenu.setOnAction( event -> {
            final Window owner = menuBar.getParent( ).getScene( ).getWindow( );
            _model.fireTournamentInformationRadiator( owner );
        } );
        radiatorMenu.disableProperty( ).bind( _model.currentTournamentProperty( ).isNull( ) );
        tournamentMenu.getItems( ).add( radiatorMenu );

        final MenuItem deleteRound = new FaMenuItem( FontAwesome.WARNING, "black" );
        deleteRound.textProperty( ).bind( _model.getI18n( ).get( "menu.round.delete" ) );
        deleteRound.setOnAction( event -> new DeleteRoundDialog( _model.createResetRoundDialogModel( ) ).show( ) );
        deleteRound.setDisable( true );
        _model.currentTournamentProperty( )
              .addListener( ( observable, oldValue, newValue ) -> {
                  deleteRound
                      .setDisable( newValue == null || newValue.getHeader( ).getStatus( ) == TournamentStatus.CLOSED );
              } );
        tournamentMenu.getItems( ).add( deleteRound );

        return tournamentMenu;
    }

    private Menu playerMenu( ) {
        final Menu playerMenu = new Menu( );
        playerMenu.textProperty( ).bind( _model.getI18n( ).get( "menu.player" ) );

        final MenuItem addMenu = new FaMenuItem( FontAwesome.PLUS, "black" );
        addMenu.textProperty( ).bind( _model.getI18n( ).get( "menu.add.player" ) );
        addMenu.setOnAction( event -> new AddPlayerDialog( _model.newAddPlayerDialogModel( ) )
                                          .showAndWait( )
                                          .ifPresent( _model::firePlayerCreated ) );
        playerMenu.getItems( ).add( addMenu );

        final MenuItem listMenu = new FaMenuItem( FontAwesome.LIST, "black" );
        listMenu.textProperty( ).bind( _model.getI18n( ).get( "player.management.title" ) );
        listMenu.setOnAction( event -> _model.firePlayerManagement( ) );
        playerMenu.getItems( ).add( listMenu );

        return playerMenu;
    }

    private Menu optionMenu( ) {
        final Menu optionMenu = new Menu( );
        optionMenu.textProperty( ).bind( _model.getI18n( ).get( "menu.option" ) );

        final MenuItem languageMenu = new FaMenuItem( FontAwesome.LANGUAGE, "black" );
        languageMenu.textProperty( ).bind( _model.getI18n( ).get( "menu.option.language" ) );
        languageMenu.setOnAction( event -> _model.fireLanguageSelection( ) );
        optionMenu.getItems( ).add( languageMenu );

        return optionMenu;
    }

    private Menu aboutMenu( ) {
        final Menu aboutMenu = new FaMenu( FontAwesome.ABOUT, "darkgrey" );

        final MenuItem documentation = new FaMenuItem( FontAwesome.BOOK, "black" );
        documentation.textProperty( ).bind( Bindings.concat( _model.getI18n( ).get( "menu.about.documentation" ) ) );
        documentation.setOnAction( event -> _model.fireOpenDocumentation( ) );
        aboutMenu.getItems( ).add( documentation );

        final MenuItem about = new FaMenuItem( FontAwesome.INFO, "black" );
        about.textProperty( ).bind( Bindings.concat( _model.getI18n( ).get( "menu.about.about" ) ) );
        about.setOnAction( event -> _model.fireAboutDialog( ) );
        aboutMenu.getItems( ).add( about );

        return aboutMenu;
    }

    private final ApplicationMenuBarModel _model;
}
