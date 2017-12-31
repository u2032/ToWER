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

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import land.tower.core.view.tournament.detail.enrolment.AddTeamDialogModel;
import land.tower.core.view.tournament.detail.enrolment.TournamentEnrolmentTabModel;
import land.tower.core.view.tournament.detail.information.TournamentInformationTabModel;
import land.tower.core.view.tournament.detail.round.TournamentRoundTabModel;

/**
 * Created on 19/12/2017
 * @author Cédric Longo
 */
public final class TournamentViewModule extends AbstractModule {

    @Override
    protected void configure( ) {
        bind( TournamentViewModelProvider.class ).in( Scopes.SINGLETON );

        install( new FactoryModuleBuilder( )
                     .implement( TournamentViewModel.class, TournamentViewModel.class )
                     .build( TournamentViewModel.Factory.class ) );

        install( new FactoryModuleBuilder( )
                     .implement( TournamentInformationTabModel.class, TournamentInformationTabModel.class )
                     .build( TournamentInformationTabModel.Factory.class ) );

        install( new FactoryModuleBuilder( )
                     .implement( TournamentEnrolmentTabModel.class, TournamentEnrolmentTabModel.class )
                     .build( TournamentEnrolmentTabModel.Factory.class ) );

        install( new FactoryModuleBuilder( )
                     .implement( AddTeamDialogModel.class, AddTeamDialogModel.class )
                     .build( AddTeamDialogModel.Factory.class ) );

        install( new FactoryModuleBuilder( )
                     .implement( TournamentRoundTabModel.class, TournamentRoundTabModel.class )
                     .build( TournamentRoundTabModel.Factory.class ) );

    }
}
