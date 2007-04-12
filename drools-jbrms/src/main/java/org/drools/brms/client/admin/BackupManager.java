package org.drools.brms.client.admin;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BackupManager extends Composite {

    private ListBox currentStatuses;

    public BackupManager() {
        FormStyleLayout form = new FormStyleLayout("images/backup_large.png", "Manage Backups");
        form.addAttribute( "", new HTML("<i>-</i>") );
        
        currentStatuses = new ListBox();
        currentStatuses.setVisibleItemCount( 7 );
        currentStatuses.setWidth( "50%" );

        initWidget( form );
    }
}
