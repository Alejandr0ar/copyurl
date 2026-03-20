package org.zaproxy.addon.copyurl;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import org.parosproxy.paros.network.HttpHeader;
import org.parosproxy.paros.network.HttpHeaderField;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpRequestHeader;
import org.zaproxy.zap.view.popup.PopupMenuItemHttpMessageContainer;

/**
 * Copia la request como comando cURL en el mismo formato que Burp Suite:
 *   - --path-as-is  : evita que curl normalice el path
 *   - -i -s -k      : respuesta con headers, silencioso, sin verificar SSL
 *   - $'...'        : ANSI-C quoting — maneja ' \ " y caracteres de control
 *   - URL al final  : como Burp
 *   - -b cookies    : Cookie extraído en su propio flag
 *
 * Headers ignorados:
 *   - content-length  : curl lo recalcula desde el body
 *   - content-encoding: ZAP ya descomprimió el body; mandarlo causaría error 400
 */
public class PopupMenuCopyCurl extends PopupMenuItemHttpMessageContainer {

    private static final long serialVersionUID = 1L;

    public PopupMenuCopyCurl(String label) {
        super(label);
    }

    @Override
    public void performAction(HttpMessage msg) {
        if (msg == null || msg.getRequestHeader().isEmpty()) {
            return;
        }
        StringSelection selection = new StringSelection(buildCurlCommand(msg));
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    private String buildCurlCommand(HttpMessage msg) {
        HttpRequestHeader requestHeader = msg.getRequestHeader();
        StringBuilder sb = new StringBuilder("curl --path-as-is -i -s -k");

        sb.append(" -X ").append(requestHeader.getMethod());

        String cookieValue = null;

        for (HttpHeaderField header : requestHeader.getHeaders()) {
            String nameLower = header.getName().trim().toLowerCase();

            if (nameLower.equals("content-length") || nameLower.equals("content-encoding")) {
                continue;
            }
            if (nameLower.equals("cookie")) {
                cookieValue = header.getValue().trim();
                continue;
            }

            sb.append(" \\\n  -H ")
              .append(ansiQuote(header.getName().trim() + ": " + header.getValue().trim()));
        }

        if (cookieValue != null && !cookieValue.isEmpty()) {
            sb.append(" \\\n  -b ").append(ansiQuote(cookieValue));
        }

        String body = msg.getRequestBody().toString();
        if (body != null && !body.isEmpty()) {
            sb.append(" \\\n  --data-binary ").append(ansiQuote(body));
        }

        // URL al final (igual que Burp)
        sb.append(" \\\n  ").append(ansiQuote(requestHeader.getURI().toString()));

        return sb.toString();
    }

    /**
     * Envuelve un string en ANSI-C quoting: $'...'
     * Escapes: \ → \\  ' → \'  " → \"  \n \r \t  bytes fuera de ASCII → \xHH
     */
    private static String ansiQuote(String s) {
        StringBuilder sb = new StringBuilder("$'");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '\'': sb.append("\\'");  break;
                case '"':  sb.append("\\\""); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20 || c > 0x7e) {
                        sb.append(String.format("\\x%02x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        sb.append("'");
        return sb.toString();
    }
}
