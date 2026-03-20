package org.zaproxy.addon.copyurl;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;

public class ExtensionCopyRequest extends ExtensionAdaptor {

    public static final String NAME = "ExtensionCopyRequest";
    protected static final String PREFIX = "copyRequest";

    private static final String ICON_URL =
            "/org/zaproxy/addon/copyurl/resources/copyurl.png";
    private static final String ICON_CURL =
            "/org/zaproxy/addon/copyurl/resources/copycurl.png";

    private static final Logger LOGGER = LogManager.getLogger(ExtensionCopyRequest.class);

    private PopupMenuCopyUrl popupCopyUrl;
    private PopupMenuCopyCurl popupCopyCurl;

    public ExtensionCopyRequest() {
        super(NAME);
        setI18nPrefix(PREFIX);
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);
        if (hasView()) {
            extensionHook.getHookMenu().addPopupMenuItem(getPopupCopyUrl());
            extensionHook.getHookMenu().addPopupMenuItem(getPopupCopyCurl());
        }
    }

    @Override
    public boolean canUnload() {
        return true;
    }

    @Override
    public String getDescription() {
        return Constant.messages.getString(PREFIX + ".desc");
    }

    private PopupMenuCopyUrl getPopupCopyUrl() {
        if (popupCopyUrl == null) {
            popupCopyUrl = new PopupMenuCopyUrl(
                    Constant.messages.getString(PREFIX + ".popup.copyurl.title"));
            setIcon(popupCopyUrl, ICON_URL);
        }
        return popupCopyUrl;
    }

    private PopupMenuCopyCurl getPopupCopyCurl() {
        if (popupCopyCurl == null) {
            popupCopyCurl = new PopupMenuCopyCurl(
                    Constant.messages.getString(PREFIX + ".popup.copycurl.title"));
            setIcon(popupCopyCurl, ICON_CURL);
        }
        return popupCopyCurl;
    }

    private void setIcon(JMenuItem item, String path) {
        try {
            java.net.URL iconUrl = getClass().getResource(path);
            if (iconUrl != null) {
                item.setIcon(new ImageIcon(iconUrl));
            }
        } catch (Exception e) {
            LOGGER.debug("No se pudo cargar el icono: {}", path, e);
        }
    }
}
