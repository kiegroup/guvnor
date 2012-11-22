/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.jcr2vfsmigration.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MigrationConfig {

    private File inputJcrRepository;
    private File outputVfsRepository;

    public File getInputJcrRepository() {
        return inputJcrRepository;
    }

    public File getOutputVfsRepository() {
        return outputVfsRepository;
    }

    // ************************************************************************
    // Configuration methods
    // ************************************************************************

    public void parseArgs(String[] args) {
        Options options = new Options();
        options.addOption("i", "inputJcrRepository", true, "The Guvnor 5 JCR repository");
        options.addOption("o", "outputVfsRepository", true, "The Guvnor 6 VFS repository");
        CommandLine commandLine;
        try {
            commandLine = new BasicParser().parse(options, args);
        } catch (ParseException e) {
            throw new IllegalArgumentException("The arguments (" + Arrays.toString(args) + ") could not be parsed.", e);
        }
        if (!commandLine.getArgList().isEmpty()) {
            throw new IllegalArgumentException("The arguments (" + Arrays.toString(args)
                    + ") have unsupported arguments (" + commandLine.getArgList() + ").");
        }
        parseArgInputJcrRepository(commandLine);
        parseArgOutputVfsRepository(commandLine);
    }

    private void parseArgInputJcrRepository(CommandLine commandLine) {
        inputJcrRepository = commandLine.hasOption("i")
                ? new File(commandLine.getOptionValue("i"))
                : new File("."); // TODO is this a good default or should we fail fast?
        if (!inputJcrRepository.exists()) {
            throw new IllegalArgumentException("The inputJcrRepository (" + inputJcrRepository + ") does not exist.");
        }
        try {
            inputJcrRepository = inputJcrRepository.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("The inputJcrRepository (" + inputJcrRepository + ") has issues.", e);
        }
    }

    private void parseArgOutputVfsRepository(CommandLine commandLine) {
        outputVfsRepository = commandLine.hasOption("o")
                ? new File(commandLine.getOptionValue("o"))
                : new File("vfs"); // TODO is this a good default or should we fail fast?
        if (outputVfsRepository.exists()) {
            throw new IllegalArgumentException("The outputVfsRepository (" + outputVfsRepository + ") already exists.");
        }
        try {
            outputVfsRepository = outputVfsRepository.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("The outputVfsRepository (" + outputVfsRepository + ") has issues.", e);
        }
        outputVfsRepository.mkdirs();
    }

}
