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

package land.tower.core.view.update;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.browser.Browser;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.updater.UpdateEvent;
import land.tower.core.ext.updater.VersionInformation;

/**
 * Created on 09/02/2018
 * @author Cédric Longo
 */
final class UpdateDialogModel {

    @Inject
    UpdateDialogModel( final Browser browser, final EventBus eventBus,
                       final I18nTranslator i18n, final Provider<Stage> owner ) {
        _browser = browser;
        _eventBus = eventBus;
        _i18n = i18n;
        _owner = owner;
        _eventBus.register( this );
    }

    @Subscribe
    public void updateEvent( final UpdateEvent event ) {
        Platform.runLater( ( ) -> {
            new UpdateDialog( this, event.getVersion( ) ).show( );
        } );
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public Window getOwner( ) {
        return _owner.get( );
    }

    public void fireOpenUrl( final VersionInformation version ) {
        _browser.open( version.getUrl( ) );
    }

    private final Browser _browser;
    private final EventBus _eventBus;
    private final I18nTranslator _i18n;
    private final Provider<Stage> _owner;
}
