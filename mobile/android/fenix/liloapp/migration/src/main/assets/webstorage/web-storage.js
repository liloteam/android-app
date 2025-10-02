
const message = { type: "Log", value: "web storage js" };
browser.runtime.sendNativeMessage("lilobrowser", message);

// Establish connection with app
let port = browser.runtime.connectNative("lilobrowser");
port.onMessage.addListener(response => {

    // Local storage injection
    if (response.type === "INJECT_LOCALSTORAGE") {
        try {
            for (const [key, value] of Object.entries(response.items || {})) {
                localStorage.setItem(key, value);
            }
            port.postMessage({ success: true, type: response.type, message: "Local storage injected" });
        } catch(err) {
            port.postMessage({ success: false, type: response.type, error: err.message });
        }

    }
    // Cookies injection
    else {
        try {
            const url = new URL(response.url);
            console.log("LILO:JS: URL is valid: ", url);

            browser.cookies.set({
                url: url.toString(),
                name: response.name,
                value: response.value,
            })
            .then((cookie) => console.log(`LILO:JS: Cookie set: ${JSON.stringify(cookie)}`))
            .catch((err) => console.log("LILO:JS: could not set cookie: ", err));

        } catch (err) {
            console.log("LILO:JS: error with cookie", err);
        }

        // Let's just echo the message back
        port.postMessage({ type: "Log", value: `Received cookie set request: ${JSON.stringify(response)}` });
    }

});
port.postMessage({ type: "Log", value: "web-storage port is ready." });
