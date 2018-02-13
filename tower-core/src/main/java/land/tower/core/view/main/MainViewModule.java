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

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

import javafx.scene.control.Menu;

/**
 * Created on 12/11/2017
 * @author Cédric Longo
 */
public final class MainViewModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( ApplicationScene.class ).in( Scopes.SINGLETON );
        bind( ApplicationSceneModel.class ).in( Scopes.SINGLETON );
        bind( ApplicationMenuBar.class ).in( Scopes.SINGLETON );
        bind( ApplicationMenuBarModel.class ).in( Scopes.SINGLETON );
        bind( ApplicationStatusBar.class ).in( Scopes.SINGLETON );
        bind( ApplicationStatusBarModel.class ).in( Scopes.SINGLETON );

        Multibinder.newSetBinder( binder( ), Menu.class );
    }
}
