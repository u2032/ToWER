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

package land.tower.core.view.option;

import java.util.Locale;
import java.util.Optional;
import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.i18n.Language;
import land.tower.core.ext.preference.Preferences;

/**
 * Created on 31/12/2017
 * @author Cédric Longo
 */
public final class LanguageDialogModel {

    @Inject
    LanguageDialogModel( final Preferences preferences, final I18nTranslator i18n,
                         final Stage owner ) {
        _preferences = preferences;
        this.i18n = i18n;
        _owner = owner;
    }

    public I18nTranslator getI18n( ) {
        return i18n;
    }

    public Stage getOwner( ) {
        return _owner;
    }

    public Language getCurrentLanguage( ) {
        final Optional<String> fromPref = _preferences.getString( "language" );
        if ( fromPref.isPresent( ) ) {
            return Language.fromCode( fromPref.get( ) ).orElse( Language.EN );
        }

        final String fromLocale = Locale.getDefault( ).getLanguage( );
        return Language.fromCode( fromLocale ).orElse( Language.EN );
    }

    private final Preferences _preferences;
    private final I18nTranslator i18n;
    private final Stage _owner;
}
