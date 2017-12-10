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

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.view.event.CloseRequestEvent;

/**
 * Created on 12/11/2017
 * @author Cédric Longo
 */
final class ApplicationMenuBar extends MenuBar {

    @Inject
    public ApplicationMenuBar( final EventBus eventBus, final I18nTranslator translator ) {
        _eventBus = eventBus;
        _i18n = translator;
        getMenus( ).add( fileMenu( ) );
    }

    private Menu fileMenu( ) {
        final Menu fileMenu = new Menu( _i18n.get( "menu.file" ) );
        fileMenu.getItems( ).add( new SeparatorMenuItem( ) );

        final MenuItem exitMenu = new MenuItem( _i18n.get( "menu.file.exit" ) );
        exitMenu.setOnAction( event -> _eventBus.post( new CloseRequestEvent( ) ) );
        fileMenu.getItems( ).add( exitMenu );

        return fileMenu;
    }

    private final EventBus _eventBus;
    private final I18nTranslator _i18n;
}
