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

package land.tower.core.view.tournament.detail.information;

import java.util.Collections;
import java.util.List;
import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.player.IPlayerNumeroValidator;
import land.tower.core.model.player.suggestion.IPlayerSuggestionProvider;
import land.tower.data.Player;

/**
 * Created on 30/03/2018
 * @author Cédric Longo
 */
final class SelectPlayerDialogModel {

    @Inject
    public SelectPlayerDialogModel( final Stage owner, final I18nTranslator i18n,
                                    final IPlayerNumeroValidator playerNumeroValidator,
                                    final IPlayerSuggestionProvider playerSuggestionProvider ) {
        _owner = owner;
        _i18n = i18n;
        _playerNumeroValidator = playerNumeroValidator;
        _playerSuggestionProvider = playerSuggestionProvider;
    }

    public Stage getOwner( ) {
        return _owner;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public IPlayerNumeroValidator getPlayerNumeroValidator( ) {
        return _playerNumeroValidator;
    }

    public List<Player> getPlayerSuggestionsForName( final String userText ) {
        return _playerSuggestionProvider.getSuggestionsForName( userText );
    }

    public List<Player> getPlayerSuggestionsForNumero( final String userText ) {
        try {
            return _playerSuggestionProvider.getSuggestionsForNumero( Long.parseLong( userText.trim( ) ) );
        } catch ( final NumberFormatException ignored ) {
            return Collections.emptyList( );
        }
    }

    private final Stage _owner;
    private final I18nTranslator _i18n;
    private final IPlayerNumeroValidator _playerNumeroValidator;
    private final IPlayerSuggestionProvider _playerSuggestionProvider;
}
