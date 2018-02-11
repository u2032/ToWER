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

package land.tower.core.ext.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import land.tower.core.ext.i18n.Language;

/**
 * Created on 15/12/2017
 * @author Cédric Longo
 */
class ConfigurationTest {

    @Test
    @DisplayName( "Configuration loads key-value pair from file" )
    void configurationTest( ) throws Exception {
        // Setup
        final Configuration config = new Configuration( "config.properties" );
        // Exercice
        final String splashscreen = config.get( "splashscreen" );
        // Verify
        assertThat( splashscreen ).isEqualTo( "img/splashscreen.png" );
    }

    @Test
    @DisplayName( "GetTitle returns title value" )
    void getTitleTest( ) throws Exception {
        // Setup
        final Configuration config = new Configuration( "config.properties" );
        config.register( "title", "mytitle" );
        // Exercice
        final String title = config.getTitle( );
        // Verify
        assertThat( title ).isEqualTo( "mytitle" );
    }

    @Test
    @DisplayName( "GameList returns expected values" )
    void gameListTest( ) throws Exception {
        // Setup
        final Configuration config = new Configuration( "config.properties" );
        config.register( "games", "Game1, Game2" );
        // Exercice
        final List<String> games = config.gameList( );
        // Verify
        assertThat( games ).contains( "Game1", "Game2" );
    }

    @Test
    @DisplayName( "Available languages returns expected values" )
    void availableLanguageTest( ) throws Exception {
        // Setup
        final Configuration config = new Configuration( "config.properties" );
        config.register( "languages", "en" );
        // Exercice
        final Language[] languages = config.availableLanguages( );
        // Verify
        assertThat( languages ).contains( Language.EN );
    }
}