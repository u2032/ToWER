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
import javax.inject.Inject;
import land.tower.core.ext.font.FontAwesome;

/**
 * Created on 12/11/2017
 * @author Cédric Longo
 */
final class ApplicationMenuBar extends MenuBar {

    @Inject
    public ApplicationMenuBar( final ApplicationMenuBarModel model ) {
        _model = model;
        getMenus( ).add( fileMenu( ) );
        getMenus( ).add( tournamentMenu( ) );
    }

    private Menu fileMenu( ) {
        final Menu fileMenu = new Menu( );
        fileMenu.textProperty( ).bind( _model.getI18n( ).get( "menu.file" ) );

        final MenuItem homepageMenu = new MenuItem( );
        homepageMenu.getStyleClass( ).add( "fa" );
        homepageMenu.textProperty( ).bind( Bindings.concat( FontAwesome.HOME, " ",
                                                            _model.getI18n( ).get( "menu.home" ) ) );
        homepageMenu.setOnAction( event -> _model.fireHomeRequest( ) );
        fileMenu.getItems( ).add( homepageMenu );

        fileMenu.getItems( ).add( new SeparatorMenuItem( ) );

        final MenuItem exitMenu = new MenuItem( );
        exitMenu.getStyleClass( ).add( "fa" );
        exitMenu.textProperty( ).bind( Bindings.concat( FontAwesome.OFF, " ",
                                                        _model.getI18n( ).get( "menu.file.exit" ) ) );
        exitMenu.setOnAction( event -> _model.fireCloseRequest( ) );
        fileMenu.getItems( ).add( exitMenu );

        return fileMenu;
    }

    private Menu tournamentMenu( ) {
        final Menu tournamentMenu = new Menu( );
        tournamentMenu.textProperty( ).bind( _model.getI18n( ).get( "menu.tournament" ) );

        final MenuItem homepageMenu = new MenuItem( );
        homepageMenu.getStyleClass( ).add( "fa" );
        homepageMenu.textProperty( ).bind( Bindings.concat( FontAwesome.PLUS, " ",
                                                            _model.getI18n( ).get( "menu.add.tournament" ) ) );
        homepageMenu.setOnAction( event -> _model.fireTournamentCreation( ) );
        tournamentMenu.getItems( ).add( homepageMenu );

        return tournamentMenu;
    }

    private final ApplicationMenuBarModel _model;
}
