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

import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.view.event.CloseRequestEvent;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.home.HomepageView;

/**
 * Created on 18/12/2017
 * @author Cédric Longo
 */
final class ApplicationMenuBarModel {

    @Inject
    ApplicationMenuBarModel( final EventBus eventBus, final I18nTranslator i18n,
                             final Provider<HomepageView> homepageViewProvider ) {
        _eventBus = eventBus;
        _i18n = i18n;
        _homepageViewProvider = homepageViewProvider;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    void fireCloseRequest( ) {
        _eventBus.post( new CloseRequestEvent( ) );
    }

    public void fireHomeRequest( ) {
        _eventBus.post( new SceneRequestedEvent( _homepageViewProvider.get( ) ) );
    }

    private final EventBus _eventBus;
    private final I18nTranslator _i18n;
    private final Provider<HomepageView> _homepageViewProvider;
}
