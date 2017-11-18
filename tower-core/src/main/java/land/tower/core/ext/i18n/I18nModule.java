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

package land.tower.core.ext.i18n;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import land.tower.core.ext.logger.Loggers;

/**
 * Created on 12/11/2017
 * @author Cédric Longo
 */
public final class I18nModule extends AbstractModule {

    @Override
    protected void configure( ) {
    }

    @Provides
    @Singleton
    I18nTranslator loadTranslator( ) {
        final Locale defaultLocale = Locale.getDefault( );
        LoggerFactory.getLogger( Loggers.MAIN ).info( "Default Locale is: {}", defaultLocale );

        final I18nTranslator i18nTranslator = new I18nTranslator( );

        load( "i18n/i18n_en.properties" )
            .ifPresent( i18nTranslator::registerEntries );

        load( "i18n/i18n_" + defaultLocale.getLanguage( ) + ".properties" )
            .ifPresent( i18nTranslator::registerEntries );

        load( "i18n/i18n_" + defaultLocale.getLanguage( ) + "_" + defaultLocale.getCountry( ) + ".properties" )
            .ifPresent( i18nTranslator::registerEntries );

        return i18nTranslator;
    }

    private Optional<Properties> load( final String name ) {
        try ( final InputStream in = getClass( ).getClassLoader( ).getResourceAsStream( name ) ) {
            if ( in == null ) {
                return Optional.empty( );
            }

            LoggerFactory.getLogger( Loggers.MAIN ).info( "Loading text entries from: {}", name );
            final Properties properties = new Properties( );
            properties.load( in );
            return Optional.of( properties );

        } catch ( final IOException e ) {
            return Optional.empty( );
        }
    }
}
