package org.drools.guvnor.client.explorer;

import com.google.gwt.place.impl.AbstractPlaceHistoryMapper;
import org.drools.guvnor.client.explorer.GuvnorPlaceHistoryMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.impl.AbstractPlaceHistoryMapper.PrefixAndToken;
import com.google.gwt.core.client.GWT;

public class GuvnorPlaceHistoryMapperImpl extends AbstractPlaceHistoryMapper<Void> implements GuvnorPlaceHistoryMapper {
  
  protected PrefixAndToken getPrefixAndToken(Place newPlace) {
    if (newPlace instanceof org.drools.guvnor.client.explorer.AssetEditorPlace) {
      org.drools.guvnor.client.explorer.AssetEditorPlace place = (org.drools.guvnor.client.explorer.AssetEditorPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.AssetEditorPlace> t = GWT.create(org.drools.guvnor.client.explorer.AssetEditorPlace.Tokenizer.class);
      return new PrefixAndToken("AssetEditorPlace", t.getToken((org.drools.guvnor.client.explorer.AssetEditorPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.FindPlace) {
      org.drools.guvnor.client.explorer.FindPlace place = (org.drools.guvnor.client.explorer.FindPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.FindPlace> t = GWT.create(org.drools.guvnor.client.explorer.FindPlace.Tokenizer.class);
      return new PrefixAndToken("FindPlace", t.getToken((org.drools.guvnor.client.explorer.FindPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.ModuleEditorPlace) {
      org.drools.guvnor.client.explorer.ModuleEditorPlace place = (org.drools.guvnor.client.explorer.ModuleEditorPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.ModuleEditorPlace> t = GWT.create(org.drools.guvnor.client.explorer.ModuleEditorPlace.Tokenizer.class);
      return new PrefixAndToken("ModuleEditorPlace", t.getToken((org.drools.guvnor.client.explorer.ModuleEditorPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.MultiAssetPlace) {
      org.drools.guvnor.client.explorer.MultiAssetPlace place = (org.drools.guvnor.client.explorer.MultiAssetPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.MultiAssetPlace> t = GWT.create(org.drools.guvnor.client.explorer.MultiAssetPlace.Tokenizer.class);
      return new PrefixAndToken("MultiAssetPlace", t.getToken((org.drools.guvnor.client.explorer.MultiAssetPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace) {
      org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace place = (org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace> t = GWT.create(org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace.Tokenizer.class);
      return new PrefixAndToken("ManagerPlace", t.getToken((org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.navigation.browse.CategoryPlace) {
      org.drools.guvnor.client.explorer.navigation.browse.CategoryPlace place = (org.drools.guvnor.client.explorer.navigation.browse.CategoryPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.navigation.browse.CategoryPlace> t = GWT.create(org.drools.guvnor.client.explorer.navigation.browse.CategoryPlace.Tokenizer.class);
      return new PrefixAndToken("CategoryPlace", t.getToken((org.drools.guvnor.client.explorer.navigation.browse.CategoryPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.navigation.browse.InboxPlace) {
      org.drools.guvnor.client.explorer.navigation.browse.InboxPlace place = (org.drools.guvnor.client.explorer.navigation.browse.InboxPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.navigation.browse.InboxPlace> t = GWT.create(org.drools.guvnor.client.explorer.navigation.browse.InboxPlace.Tokenizer.class);
      return new PrefixAndToken("InboxPlace", t.getToken((org.drools.guvnor.client.explorer.navigation.browse.InboxPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.navigation.browse.StatePlace) {
      org.drools.guvnor.client.explorer.navigation.browse.StatePlace place = (org.drools.guvnor.client.explorer.navigation.browse.StatePlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.navigation.browse.StatePlace> t = GWT.create(org.drools.guvnor.client.explorer.navigation.browse.StatePlace.Tokenizer.class);
      return new PrefixAndToken("StatePlace", t.getToken((org.drools.guvnor.client.explorer.navigation.browse.StatePlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace) {
      org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace place = (org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace> t = GWT.create(org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace.Tokenizer.class);
      return new PrefixAndToken("ProcessOverviewPlace", t.getToken((org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace) {
      org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace place = (org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace> t = GWT.create(org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace.Tokenizer.class);
      return new PrefixAndToken("ReportTemplatesPlace", t.getToken((org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace) {
      org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace place = (org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace> t = GWT.create(org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace.Tokenizer.class);
      return new PrefixAndToken("PreferencesPlace", t.getToken((org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace) {
      org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace place = (org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace> t = GWT.create(org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace.Tokenizer.class);
      return new PrefixAndToken("GroupTasksPlace", t.getToken((org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace) {
      org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace place = (org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace> t = GWT.create(org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace.Tokenizer.class);
      return new PrefixAndToken("PersonalTasksPlace", t.getToken((org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace) place));
    }
    if (newPlace instanceof org.drools.guvnor.client.moduleeditor.AssetViewerPlace) {
      org.drools.guvnor.client.moduleeditor.AssetViewerPlace place = (org.drools.guvnor.client.moduleeditor.AssetViewerPlace) newPlace;
      PlaceTokenizer<org.drools.guvnor.client.moduleeditor.AssetViewerPlace> t = GWT.create(org.drools.guvnor.client.moduleeditor.AssetViewerPlace.Tokenizer.class);
      return new PrefixAndToken("AssetViewerPlace", t.getToken((org.drools.guvnor.client.moduleeditor.AssetViewerPlace) place));
    }
    return null;
  }
  
  protected PlaceTokenizer<?> getTokenizer(String prefix) {
    if ("AssetEditorPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.AssetEditorPlace.Tokenizer.class);
    }
    if ("ReportTemplatesPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace.Tokenizer.class);
    }
    if ("ProcessOverviewPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace.Tokenizer.class);
    }
    if ("GroupTasksPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace.Tokenizer.class);
    }
    if ("StatePlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.navigation.browse.StatePlace.Tokenizer.class);
    }
    if ("ModuleEditorPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.ModuleEditorPlace.Tokenizer.class);
    }
    if ("ManagerPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace.Tokenizer.class);
    }
    if ("PersonalTasksPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace.Tokenizer.class);
    }
    if ("InboxPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.navigation.browse.InboxPlace.Tokenizer.class);
    }
    if ("PreferencesPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace.Tokenizer.class);
    }
    if ("CategoryPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.navigation.browse.CategoryPlace.Tokenizer.class);
    }
    if ("FindPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.FindPlace.Tokenizer.class);
    }
    if ("MultiAssetPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.explorer.MultiAssetPlace.Tokenizer.class);
    }
    if ("AssetViewerPlace".equals(prefix)) {
      return GWT.create(org.drools.guvnor.client.moduleeditor.AssetViewerPlace.Tokenizer.class);
    }
    return null;
  }

}
