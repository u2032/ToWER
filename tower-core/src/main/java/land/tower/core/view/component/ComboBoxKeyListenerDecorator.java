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

package land.tower.core.view.component;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Created on 30/01/2018
 * Note: Modified from original to use the converter defined on combobox, lambas, generics, ...
 * @author http://tech.chitgoks.com/2013/07/19/how-to-go-to-item-in-combobox-on-keypress-in-java-fx-2/
 */
public final class ComboBoxKeyListenerDecorator<T> implements EventHandler<KeyEvent> {

    private final ComboBox<T> comboBox;
    private final StringBuilder sb = new StringBuilder( );

    public ComboBoxKeyListenerDecorator( ComboBox<T> comboBox ) {
        this.comboBox = comboBox;
        decorate( );
    }

    private void decorate( ) {
        this.comboBox.setOnKeyReleased( ComboBoxKeyListenerDecorator.this );

        this.comboBox.addEventFilter( KeyEvent.KEY_RELEASED, event -> {
            if ( event.getCode( ) == KeyCode.ESCAPE && sb.length( ) > 0 ) {
                sb.delete( 0, sb.length( ) );
            }
        } );

        // add a focus listener such that if not in focus, reset the filtered typed keys
        this.comboBox.focusedProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue != null && !newValue ) {
                sb.setLength( 0 );
            } else {
                ListView lv = ( (ComboBoxListViewSkin) ComboBoxKeyListenerDecorator.this.comboBox.getSkin( ) )
                                  .getListView( );
                lv.scrollTo( lv.getSelectionModel( ).getSelectedIndex( ) );
            }
        } );

        this.comboBox.setOnMouseClicked( event -> {
            ListView lv = ( (ComboBoxListViewSkin) ComboBoxKeyListenerDecorator.this.comboBox.getSkin( ) )
                              .getListView( );
            lv.scrollTo( lv.getSelectionModel( ).getSelectedIndex( ) );
            sb.setLength( 0 );
        } );
    }

    @Override
    public void handle( KeyEvent event ) {
        if ( event.getCode( ) == KeyCode.DOWN || event.getCode( ) == KeyCode.UP
             || event.getCode( ) == KeyCode.TAB
             || event.getCode( ) == KeyCode.PAGE_DOWN || event.getCode( ) == KeyCode.PAGE_UP ) {
            return;
        } else if ( event.getCode( ) == KeyCode.BACK_SPACE && sb.length( ) > 0 ) {
            sb.setLength( 0 );
        } else {
            sb.append( event.getText( ) );
        }

        if ( sb.length( ) == 0 ) {
            return;
        }

        boolean found = false;
        ObservableList<T> items = comboBox.getItems( );
        for ( int i = 0; i < items.size( ); i++ ) {
            if ( event.getCode( ) != KeyCode.BACK_SPACE ) {
                final String itemValue = comboBox.getConverter( ) != null ?
                                         comboBox.getConverter( ).toString( items.get( i ) )
                                                                          : items.get( i ).toString( );
                if ( itemValue.toLowerCase( ).startsWith( sb.toString( ).toLowerCase( ) ) ) {
                    ListView lv = ( (ComboBoxListViewSkin) comboBox.getSkin( ) ).getListView( );
                    lv.getSelectionModel( ).clearAndSelect( i );
                    lv.scrollTo( lv.getSelectionModel( ).getSelectedIndex( ) );
                    found = true;
                    break;
                }
            }
        }

        if ( !found && sb.length( ) > 0 ) {
            sb.deleteCharAt( sb.length( ) - 1 );
        }
    }
}
