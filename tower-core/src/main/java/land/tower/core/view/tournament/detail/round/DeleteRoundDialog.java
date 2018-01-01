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

package land.tower.core.view.tournament.detail.round;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/**
 * Created on 31/12/2017
 * @author Cédric Longo
 */
public final class DeleteRoundDialog extends Alert {

    public DeleteRoundDialog( final DeleteRoundDialogModel model ) {
        super( AlertType.WARNING );
        getDialogPane( ).getStylesheets( ).add( model.getConfig( ).getApplicationStyle( ) );

        titleProperty( ).bind( model.getI18n( ).get( "tournament.round.delete.title" ) );
        headerTextProperty( ).bind( model.getI18n( ).get( "tournament.round.delete.title" ) );
        contentTextProperty( ).bind( model.getI18n( ).get( "tournament.round.delete.message" ) );

        final ButtonType okButtonType =
            new ButtonType( model.getI18n( ).get( "action.ok" ).get( ).toUpperCase( ), ButtonData.OK_DONE );

        final ButtonType cancelButtonType =
            new ButtonType( model.getI18n( ).get( "action.cancel" ).get( ).toUpperCase( ), ButtonData.CANCEL_CLOSE );
        getDialogPane( ).getButtonTypes( ).setAll( okButtonType, cancelButtonType );

        final Button cancelButton = (Button) getDialogPane( ).lookupButton( cancelButtonType );
        cancelButton.setDefaultButton( true );

        final Button okButton = (Button) getDialogPane( ).lookupButton( okButtonType );
        okButton.setDefaultButton( false );
        okButton.setOnAction( event -> {
            model.fireDeleteRound( );
        } );
    }
}
