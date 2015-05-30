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

package org.primefaces.extensions.optimizerplugin.optimizer;

import com.google.common.io.Files;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.primefaces.extensions.optimizerplugin.util.ResourcesSetAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Basis abstract class for Google Closure Compiler / YUI Compressor Optimizers.
 *
 * @author  Oleg Varaksin (ovaraksin@googlemail.com)
 */
public abstract class AbstractOptimizer {

    private static final String OUTPUT_FILE_SUFFIX = ".source";

    private long sizeTotalOriginal = 0;

    private long sizeTotalOptimized = 0;

    public abstract void optimize(final ResourcesSetAdapter rsa, final Log log) throws MojoExecutionException;

    public long getTotalOriginalSize() {
        return sizeTotalOriginal;
    }

    public long getTotalOptimizedSize() {
        return sizeTotalOptimized;
    }

    protected File getFileWithSuffix(String path, String suffix) throws IOException {
        // get file extension
        String extension = FileUtils.extension(path);
        if (StringUtils.isNotEmpty(extension)) {
            extension = "." + extension;
        }

        // path of file with suffix
        String pathSuffix = FileUtils.removeExtension(path) + suffix + extension;

        // create a new file with suffix
        File outputFile = new File(pathSuffix);
        Files.touch(outputFile);

        return outputFile;
    }

    protected File aggregateFiles(ResourcesSetAdapter rsa, Charset cset, boolean delimeters) throws IOException {
        File outputFile = getOutputFile(rsa);

        if (rsa.getAggregation().getPrependedFile() != null) {
            // write / append to be prepended file into / to the output file
            prependFile(rsa.getAggregation().getPrependedFile(), outputFile, cset, rsa);
        }

        for (File file : rsa.getFiles()) {
            Reader in = getReader(rsa, file);
            StringWriter writer = new StringWriter();
            IOUtil.copy(in, writer);

            if (delimeters && outputFile.length() > 0) {
                // append semicolon to the new file in order to avoid invalid JS code
                Files.append(";", outputFile, cset);
            }

            // write / append content into / to the new file
            Files.append(writer.toString(), outputFile, cset);
            IOUtil.close(in);
        }

        return outputFile;
    }

    protected void deleteFilesIfNecessary(ResourcesSetAdapter rsa, Log log) {
        if (rsa.getAggregation().isRemoveIncluded() && rsa.getFiles().size() > 0) {
            for (File file : rsa.getFiles()) {
                if (file.exists() && !file.delete()) {
                    log.warn("File " + file.getName() + " could not be deleted after aggregation.");
                }
            }
        }
    }

    protected void renameOutputFileIfNecessary(ResourcesSetAdapter rsa, File outputFile) throws IOException {
        if (outputFile != null && outputFile.exists()) {
            FileUtils.rename(outputFile, rsa.getAggregation().getOutputFile());
        }
    }

    protected void prependFile(File prependedFile, File outputFile, Charset cset,
                               ResourcesSetAdapter rsa) throws IOException {
        Reader in = getReader(rsa, prependedFile);
        StringWriter writer = new StringWriter();
        IOUtil.copy(in, writer);

        writer.write(System.getProperty("line.separator"));

        // write / append compiled content into / to the new file
        Files.append(writer.toString(), outputFile, cset);
        IOUtil.close(in);
    }

    protected File getOutputFile(ResourcesSetAdapter rsa) throws IOException {
        File outputFile = rsa.getAggregation().getOutputFile();

        // prevent overwriting of existing CSS or JS file with the same name as the output file
        String extension = FileUtils.extension(outputFile.getName());
        if (StringUtils.isNotEmpty(extension)) {
            extension = "." + extension;
        }

        String pathSuffix = FileUtils.removeExtension(outputFile.getCanonicalPath()) + OUTPUT_FILE_SUFFIX + extension;
        File aggrFile = new File(pathSuffix);

        Files.createParentDirs(aggrFile);
        Files.touch(aggrFile);

        return aggrFile;
    }

    protected Reader getReader(ResourcesSetAdapter rsAdapter, File file)
    throws FileNotFoundException, UnsupportedEncodingException {
        return new InputStreamReader(new FileInputStream(file), rsAdapter.getEncoding());
    }

    protected long addToOriginalSize(File file) {
        long lenght = file.length();
        sizeTotalOriginal = sizeTotalOriginal + lenght;
        return lenght;
    }

    protected void addToOptimizedSize(File file) {
        sizeTotalOptimized = sizeTotalOptimized + file.length();
    }

    protected void addToOptimizedSize(long size) {
        sizeTotalOptimized = sizeTotalOptimized + size;
    }
}
