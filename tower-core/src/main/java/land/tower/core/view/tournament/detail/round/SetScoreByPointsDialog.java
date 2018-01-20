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

import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

/**
 * Created on 31/12/2017
 * @author Cédric Longo
 */
final class SetScoreByPointsDialog extends SetScoreDialog {

    public SetScoreByPointsDialog( final SetScoreDialogModel model ) {
        super( model );
    }

    @Override
    protected TextField getLeftWinsField( ) {
        return buildScoreField( Bindings.concat( getModel( ).getI18n( ).get( "match.score.points" ),
                                                 " ",
                                                 getModel( ).getI18n( ).get( "match.team.left" ) ),
                                getModel( ).leftScoreProperty( ) );
    }

    @Override
    protected TextField buildDrawsField( ) {
        return null;
    }

    @Override
    protected TextField buildRightWinsField( ) {
        return buildScoreField( Bindings.concat( getModel( ).getI18n( ).get( "match.score.points" ),
                                                 " ",
                                                 getModel( ).getI18n( ).get( "match.team.right" ) ),
                                getModel( ).rightScoreProperty( ) );
    }

    private TextField buildScoreField( final ObservableStringValue promptText,
                                       final SimpleObjectProperty<Integer> integerSimpleObjectProperty ) {
        final TextField field = new TextField( );
        field.setAlignment( Pos.CENTER );
        field.promptTextProperty( ).bind( promptText );
        field.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 null,
                                 c -> {
                                     final boolean matches = Pattern.matches( "\\d*", c.getControlNewText( ) );
                                     return matches ? c : null;
                                 } ) );
        field.textProperty( ).bindBidirectional( integerSimpleObjectProperty, new IntegerStringConverter( ) );
        return field;
    }

    @Override
    protected void configureAutoFocus( final TextField leftWinsField, final TextField drawsField,
                                       final TextField rightWinsField, final TextField positionField ) {
        setOnShowing( e -> Platform.runLater( leftWinsField::requestFocus ) );
    }
}
