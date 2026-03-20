# ZAP Copy Request Extension

ZAP add-on that adds two right-click context menu options to copy HTTP requests from the History/Sites panel.

## Features

### COPY URL
Copies the raw URL of the selected request to the clipboard.

```
https://example.com/api/v1/users?id=42
```

### COPY CURL
Copies the full request as a ready-to-run `curl` command — same format as Burp Suite.

```bash
curl --path-as-is -i -s -k -X POST \
  -H $'Host: example.com' \
  -H $'Content-Type: application/json' \
  -H $'Authorization: Bearer eyJ...' \
  -b $'session=abc123; cf_clearance=xyz' \
  --data-binary $'{"username":"admin","password":"test"}' \
  $'https://example.com/api/v1/login'
```

**Flags used:**

| Flag | Reason |
|---|---|
| `--path-as-is` | Prevents curl from normalizing the path |
| `-i` | Includes response headers in output |
| `-s` | Silent mode (no progress bar) |
| `-k` | Skips SSL certificate verification |
| `$'...'` | ANSI-C quoting — safely handles `'`, `"`, `\`, binary chars |

**Headers automatically handled:**

| Header | Behavior |
|---|---|
| `Cookie` | Extracted to `-b` flag |
| `Content-Length` | Omitted — curl recalculates it from the body |
| `Content-Encoding` | Omitted — ZAP decompresses the body before display; keeping this header would cause a 400 error |

## Requirements

- OWASP ZAP 2.17.0 or later

## Installation

1. Build the add-on:
   ```bash
   ./gradlew :addOns:copyurl:jarZapAddOn
   ```
2. In ZAP: **File → Load Add-on File** → select the `.zap` file from `addOns/copyurl/build/`.

## Usage

Right-click any request in the **History**, **Sites**, or **Active Scan** panel:

```
COPY URL
COPY CURL
```

The command is copied to your clipboard instantly.

## Project Structure

```
src/main/java/org/zaproxy/addon/copyurl/
├── ExtensionCopyRequest.java   # Main extension, registers menu items
├── PopupMenuCopyUrl.java       # "COPY URL" menu item
└── PopupMenuCopyCurl.java      # "COPY CURL" menu item

src/main/resources/org/zaproxy/addon/copyurl/resources/
├── Messages.properties         # Menu labels
├── copyurl.png                 # Icon for COPY URL
└── copycurl.png                # Icon for COPY CURL
```

## License

Apache License 2.0
