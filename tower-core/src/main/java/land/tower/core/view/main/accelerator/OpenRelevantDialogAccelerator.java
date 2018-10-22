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

package land.tower.core.view.main.accelerator;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.event.TournamentTabDisplayedEvent;
import land.tower.core.view.tournament.detail.TournamentView;
import land.tower.core.view.tournament.detail.TournamentViewModel;

/**
 * Created on 22/10/2018
 * @author Cédric Longo
 */
public final class OpenRelevantDialogAccelerator implements Runnable {

    @Inject
    public OpenRelevantDialogAccelerator( final EventBus eventBus ) {
        eventBus.register( this );
    }

    @Override
    public void run( ) {
        if ( _actor.get( ) == null ) {
            return;
        }
        _actor.get( ).openRelevantDialog( );
    }

    @Subscribe
    private void sceneRequested( final SceneRequestedEvent event ) {
        if ( event.getView( ) instanceof TournamentView ) {
            final TournamentViewModel viewModel = ( (TournamentView) event.getView( ) ).getModel( );
            if ( viewModel.getSelectedTab( ) instanceof RelevantDialogActor ) {
                _actor.set( ( (RelevantDialogActor) viewModel.getSelectedTab( ) ) );
                return;
            }
        }
        if ( event.getView( ) instanceof RelevantDialogActor ) {
            _actor.set( ( (RelevantDialogActor) event.getView( ) ) );
            return;
        }
        _actor.set( null );
    }

    @Subscribe
    private void tabDisplayed( final TournamentTabDisplayedEvent event ) {
        if ( event.getTab( ) instanceof RelevantDialogActor ) {
            _actor.set( ( (RelevantDialogActor) event.getTab( ) ) );
        } else {
            _actor.set( null );
        }
    }

    private final AtomicReference<RelevantDialogActor> _actor = new AtomicReference<>( );

}
