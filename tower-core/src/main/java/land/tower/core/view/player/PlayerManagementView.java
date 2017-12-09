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

package land.tower.core.view.player;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javax.inject.Inject;

/**
 * Created on 09/12/2017
 * @author Cédric Longo
 */
public final class PlayerManagementView extends BorderPane {

    @Inject
    public PlayerManagementView( final PlayerManagementViewModel model ) {
        _model = model;

        final TableView<ObservablePlayer> tableView = new TableView<>( );
        tableView.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );

        final TableColumn<ObservablePlayer, String> numeroCol = new TableColumn<>( );
        numeroCol.textProperty( ).bind( model.i18nPlayerNumeroPropertyProperty( ) );
        numeroCol.setCellValueFactory( new PropertyValueFactory<>( "numero" ) );
        tableView.getColumns( ).add( numeroCol );

        final TableColumn<ObservablePlayer, String> firstNameCol = new TableColumn<>( "Firstname" );
        firstNameCol.textProperty( ).bind( model.i18nPlayerFirstPropertyProperty( ) );
        firstNameCol.setCellValueFactory( new PropertyValueFactory<>( "firstname" ) );
        tableView.getColumns( ).add( firstNameCol );

        final TableColumn<ObservablePlayer, String> lastNameCol = new TableColumn<>( "Lastname" );
        lastNameCol.textProperty( ).bind( model.i18nPlayerLastnamePropertyProperty( ) );
        lastNameCol.setCellValueFactory( new PropertyValueFactory<>( "lastname" ) );
        tableView.getColumns( ).add( lastNameCol );

        final TableColumn<ObservablePlayer, String> birthdayCol = new TableColumn<>( "Birthday" );
        birthdayCol.textProperty( ).bind( model.i18nPlayerBirthdayPropertyProperty( ) );
        birthdayCol.setCellValueFactory( new PropertyValueFactory<>( "birthday" ) );
        tableView.getColumns( ).add( birthdayCol );

        final Label emptyLabel = new Label( );
        emptyLabel.textProperty( ).bind( model.i18nPlaceholderPropertyProperty( ) );
        tableView.setPlaceholder( emptyLabel );

        tableView.itemsProperty( ).bind( _model.playerListProperty( ) );

        setCenter( tableView );
    }

    private final PlayerManagementViewModel _model;
}
