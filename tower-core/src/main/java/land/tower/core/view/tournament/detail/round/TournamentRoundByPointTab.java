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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import land.tower.core.model.tournament.ObservableMatch;

/**
 * Created on 20/01/2018
 * @author Cédric Longo
 */
public final class TournamentRoundByPointTab extends TournamentRoundTab {

    public TournamentRoundByPointTab( final TournamentRoundTabModel model ) {
        super( model );
    }

    protected void addScoreColumns( final TableColumn<ObservableMatch, String> scoreCol ) {
        // CODEREVIEW These columns should be not reorderable but available only since Java 9
        final TableColumn<ObservableMatch, String> winsLeftCol = new TableColumn<>( );
        winsLeftCol.setEditable( false );
        winsLeftCol.setPrefWidth( 100 );
        winsLeftCol.textProperty( ).bind( getModel( ).getI18n( ).get( "match.score.points" ) );
        winsLeftCol.setCellValueFactory(
            param -> {
                final ObservableMatch match = param.getValue( );
                final SimpleStringProperty value = new SimpleStringProperty( );
                value.bind( Bindings.createStringBinding( ( ) -> {
                    if ( getModel( ).useDoubleScore( ) ) {
                        return match.hasScore( ) ? String.format( "%s [%s]",
                                                                  match.getScoreLeft( ),
                                                                  match.getScoreLeftBis( ) ) : "";
                    }
                    return match.hasScore( ) ? String.valueOf( match.getScoreLeft( ) ) : "";
                }, match.hasScoreProperty( ), match.scoreLeftProperty( ), match.scoreLeftBisProperty( ) ) );
                return value;
            } );
        scoreCol.getColumns( ).add( winsLeftCol );

        final TableColumn<ObservableMatch, String> winsRightCol = new TableColumn<>( );
        winsRightCol.setEditable( false );
        winsRightCol.setPrefWidth( 100 );
        winsRightCol.textProperty( ).bind( getModel( ).getI18n( ).get( "match.score.points" ) );
        winsRightCol.setCellValueFactory( param -> {
            final ObservableMatch match = param.getValue( );
            final SimpleStringProperty value = new SimpleStringProperty( );
            value.bind( Bindings.createStringBinding( ( ) -> {
                if ( getModel( ).useDoubleScore( ) ) {
                    return match.hasScore( ) ? String.format( "%s [%s]",
                                                              match.getScoreRight( ),
                                                              match.getScoreRightBis( ) ) : "";
                }
                return match.hasScore( ) ? String.valueOf( match.getScoreRight( ) ) : "";
            }, match.hasScoreProperty( ), match.scoreRightProperty( ), match.scoreRightBisProperty( ) ) );
            return value;
        } );
        scoreCol.getColumns( ).add( winsRightCol );
    }

}
