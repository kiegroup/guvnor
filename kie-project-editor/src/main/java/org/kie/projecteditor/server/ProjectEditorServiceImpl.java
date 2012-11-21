/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.projecteditor.server;

import org.drools.kproject.KBase;
import org.drools.kproject.KProject;
import org.drools.kproject.KSession;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.projecteditor.shared.model.KProjectModel;
import org.kie.projecteditor.shared.model.KSessionModel;
import org.kie.projecteditor.shared.model.KnowledgeBaseConfiguration;
import org.kie.projecteditor.shared.service.ProjectEditorService;
import org.kie.runtime.conf.ClockTypeOption;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;

@Service
@ApplicationScoped
public class ProjectEditorServiceImpl
        implements ProjectEditorService {


    @Override
    public void save(KProjectModel model) {
        //TODO -Rikkola-
    }

    @Override
    public KProjectModel load(Path path) {

        KProjectModel configurations = new KProjectModel();

        KProject kProject = KProjectLoader.load();
        for (String name : kProject.getKBases().keySet()) {
            configurations.add(extractModel(name, kProject.getKBases().get(name)));
        }

        return configurations;
    }

    private KnowledgeBaseConfiguration extractModel(String name, KBase kBase) {
        KnowledgeBaseConfiguration model = new KnowledgeBaseConfiguration();
        model.setFullName(name);

        model.setName(kBase.getName());
        model.setNamespace(kBase.getNamespace());
        model.setEqualsBehavior(optionToOption(kBase.getEqualsBehavior()));
        model.setEventProcessingMode(optionToOption(kBase.getEventProcessingMode()));

        for (String kSessionName : kBase.getKSessions().keySet()) {
            model.addKSession(extractModel(kBase.getKSessions().get(kSessionName), kSessionName));
        }

        return model;
    }

    private KSessionModel extractModel(KSession kSession, String name) {
        KSessionModel model = new KSessionModel();

        model.setFullName(name);
        model.setName(kSession.getName());
        model.setNamespace(kSession.getNamespace());
        model.setType(kSession.getType());
        model.setClockType(optionToOption(kSession.getClockType()));

        return model;
    }

    private org.kie.projecteditor.shared.model.ClockTypeOption optionToOption(ClockTypeOption clockType) {
        if (clockType.getClockType().equals("pseudo")) {
            return org.kie.projecteditor.shared.model.ClockTypeOption.PSEUDO;
        } else if (clockType.getClockType().equals("realtime")) {
            return org.kie.projecteditor.shared.model.ClockTypeOption.REALTIME;
        } else {
            return null;
        }
    }

    private org.kie.projecteditor.shared.model.EventProcessingOption optionToOption(EventProcessingOption eventProcessingMode) {
        switch (eventProcessingMode) {
            case CLOUD:
                return org.kie.projecteditor.shared.model.EventProcessingOption.CLOUD;
            case STREAM:
                return org.kie.projecteditor.shared.model.EventProcessingOption.STREAM;
            default:
                return null;
        }

    }

    private org.kie.projecteditor.shared.model.AssertBehaviorOption optionToOption(AssertBehaviorOption equalsBehavior) {
        switch (equalsBehavior) {
            case EQUALITY:
                return org.kie.projecteditor.shared.model.AssertBehaviorOption.EQUALITY;
            case IDENTITY:
                return org.kie.projecteditor.shared.model.AssertBehaviorOption.IDENTITY;
            default:
                return null;
        }
    }

}
