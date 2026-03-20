package org.zaproxy.addon.copyurl;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.view.popup.PopupMenuItemHttpMessageContainer;

public class PopupMenuCopyUrl extends PopupMenuItemHttpMessageContainer {

    private static final long serialVersionUID = 1L;

    public PopupMenuCopyUrl(String label) {
        super(label);
    }

    @Override
    public void performAction(HttpMessage msg) {
        String url = msg.getRequestHeader().getURI().toString();
        StringSelection selection = new StringSelection(url);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }
}
