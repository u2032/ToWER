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

package land.tower.core.ext.inject;

import com.google.inject.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 15/12/2017
 * @author Cédric Longo
 */
public final class ModuleResolver {

    public static ModuleResolver withModules( final Module... modules ) {
        return new ModuleResolver( Arrays.asList( modules ) );
    }

    private ModuleResolver( final List<Module> modules ) {
        _modules = new ArrayList<>( modules );
    }

    public ModuleResolver with( final Module module ) {
        _modules.add( module );
        return this;
    }

    public ModuleResolver with( final List<Module> modules ) {
        for ( final Module module : modules ) {
            with( module );
        }
        return this;
    }

    public ModuleResolver override( final Class mclass, final Module module ) {
        _modules.removeIf( mclass::isInstance );
        _modules.add( module );
        return this;
    }

    public List<Module> getModules( ) {
        return _modules;
    }

    private final List<Module> _modules;
}
