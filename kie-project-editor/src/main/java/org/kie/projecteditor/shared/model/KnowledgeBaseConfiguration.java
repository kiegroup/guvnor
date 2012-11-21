package org.kie.projecteditor.shared.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;

@Portable
public class KnowledgeBaseConfiguration {

    private String name;
    private String namespace;
    private String fullName;
    private AssertBehaviorOption equalsBehavior = AssertBehaviorOption.IDENTITY;
    private EventProcessingOption eventProcessingMode = EventProcessingOption.STREAM;
    private ArrayList<KSessionModel> kSessionModels = new ArrayList<KSessionModel>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEqualsBehavior(AssertBehaviorOption equalsBehavior) {
        this.equalsBehavior = equalsBehavior;
    }

    public AssertBehaviorOption getEqualsBehavior() {
        return equalsBehavior;
    }

    public void setEventProcessingMode(EventProcessingOption eventProcessingMode) {
        this.eventProcessingMode = eventProcessingMode;
    }

    public EventProcessingOption getEventProcessingMode() {
        return eventProcessingMode;
    }

    public void addKSession(KSessionModel kSessionModel) {
        kSessionModels.add(kSessionModel);
    }

    public ArrayList<KSessionModel> getKSessionModels() {
        return kSessionModels;
    }
}
