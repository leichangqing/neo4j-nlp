/*
 * Copyright (c) 2013-2016 GraphAware
 *
 * This file is part of the GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.graphaware.nlp.processor;

import com.graphaware.nlp.annotation.NLPTextProcessor;
import com.graphaware.nlp.util.ServiceLoader;
import java.util.Map;
import java.util.Set;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TextProcessorsManager {
    private static final Logger LOG = LoggerFactory.getLogger(TextProcessorsManager.class);

    private final GraphDatabaseService database;
    private Map<String, TextProcessor> textProcessors;
    
    @Autowired
    public TextProcessorsManager(GraphDatabaseService database) {
        this.database = database;
        loadTextProcessors();
        loadPipelines();
    }

    private void loadTextProcessors() {
        textProcessors = ServiceLoader.loadInstances(NLPTextProcessor.class);
    }

    public Set<String> getTextProcessors() {
        return textProcessors.keySet();
    }
    
    
    public TextProcessor getTextProcessor(String name) {
        return textProcessors.get(name);
    }    

    public PipelineCreationResult createPipeline(Map<String, Object> inputParams) {
        String processorName = (String) inputParams.get("textProcessor");
        
        if (processorName == null || !textProcessors.containsKey(processorName)) {
            return new PipelineCreationResult(-1, "Processor class not specified or not existing");
        }
        TextProcessor processor = textProcessors.get(processorName);
        
        //TODO add catch
        processor.createPipeline(inputParams);
        //if succeeded
        storePipelines(inputParams);
        return new PipelineCreationResult(0, "");
    }

    private void loadPipelines() {
        //Load stored pipeline
    }
    
    private void storePipelines(Map<String, Object> inputParams) {
        //store pipeline
    }

    public static class PipelineCreationResult {
        private final int result;
        private final String message;

        public PipelineCreationResult(int result, String message) {
            this.result = result;
            this.message = message;
        }

        public int getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }
}
