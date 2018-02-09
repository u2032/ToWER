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

package land.tower.core.ext.updater;

import static java.lang.Integer.parseInt;

import com.google.gson.annotations.SerializedName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 07/02/2018
 * @author Cédric Longo
 */
public final class VersionInformation {

    VersionInformation( ) {

    }

    public VersionInformation( final String version, final String url ) {
        _version = version;
        _url = url;
    }

    @SerializedName( "version" )
    private String _version;

    @SerializedName( "url" )
    private String _url;

    public String getVersion( ) {
        return _version;
    }

    public void setVersion( final String version ) {
        _version = version;
    }

    public boolean isGreaterThan( final String version ) {
        final Matcher otherMather = VERSION_PATTERN.matcher( version );
        final Matcher thisMather = VERSION_PATTERN.matcher( _version );
        if ( !otherMather.matches( ) || !thisMather.matches( ) ) {
            return false;
        }

        if ( parseInt( otherMather.group( "major" ) ) > parseInt( thisMather.group( "major" ) ) ) {
            return false;
        }
        if ( parseInt( otherMather.group( "major" ) ) < parseInt( thisMather.group( "major" ) ) ) {
            return true;
        }
        if ( parseInt( otherMather.group( "minor" ) ) > parseInt( thisMather.group( "minor" ) ) ) {
            return false;
        }
        if ( parseInt( otherMather.group( "minor" ) ) < parseInt( thisMather.group( "minor" ) ) ) {
            return true;
        }
        if ( otherMather.group( "classifier" ).isEmpty( ) && !thisMather.group( "classifier" ).isEmpty( ) ) {
            return false;
        }
        if ( !otherMather.group( "classifier" ).isEmpty( ) && thisMather.group( "classifier" ).isEmpty( ) ) {
            return true;
        }
        if ( otherMather.group( "classifier" ).isEmpty( ) && thisMather.group( "classifier" ).isEmpty( ) ) {
            return false;
        }
        return otherMather.group( "classifier" ).compareTo( thisMather.group( "classifier" ) ) < 0;
    }

    public String getUrl( ) {
        return _url;
    }

    @Override
    public String toString( ) {
        return "VersionInformation{" +
               "version='" + _version + '\'' +
               ", url='" + _url + '\'' +
               '}';
    }

    private static Pattern VERSION_PATTERN = Pattern.compile( "(?<major>\\d+)\\.(?<minor>\\d+)(?<classifier>(-.*)*)" );

}
