/*
 * Copyright 2011-2015 PrimeFaces Extensions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */

package org.primefaces.extensions.optimizerplugin.util;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.WarningLevel;
import org.primefaces.extensions.optimizerplugin.model.Aggregation;

import java.io.File;
import java.util.Set;

/**
 * Container class containing all needed infos for a resource set describing JavaScript files..
 *
 * @author  Oleg Varaksin (ovaraksin@googlemail.com)
 */
public class ResourcesSetJsAdapter extends ResourcesSetAdapter {

    private CompilationLevel compilationLevel;

    private WarningLevel warningLevel;

    private boolean createSourceMap;

    public ResourcesSetJsAdapter(File inputDir, Set<File> files, CompilationLevel compilationLevel, WarningLevel warningLevel,
                                 boolean createSourceMap, Aggregation aggregation, String encoding, boolean failOnWarning, 
                                 String suffix) {
        super(inputDir, files, aggregation, encoding, failOnWarning, suffix);
        this.compilationLevel = compilationLevel;
        this.warningLevel = warningLevel;
        this.createSourceMap = createSourceMap;
    }

    public CompilationLevel getCompilationLevel() {
        return compilationLevel;
    }

    public WarningLevel getWarningLevel() {
        return warningLevel;
    }

    public boolean isCreateSourceMap() {
        return createSourceMap;
    }
}
