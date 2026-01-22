package com.trucker.memer;
import android.os.Bundle;
import org.apache.cordova.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.util.Log;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.CookieManager;
import android.os.Handler;
import android.webkit.WebResourceResponse;
import java.io.ByteArrayInputStream;
import android.view.WindowManager;

public class MainActivity extends CordovaActivity {
    private static final String TAG = "MainActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep the screen on so the phone doesn't sleep while watching
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Keep the WebView running in the background (audio playback, etc.)
        this.keepRunning = true;

        // Configure WebView immediately to ensure all navigation stays in-app
        try {
            if (appView != null) {
                Object engineView = appView.getEngine().getView();
                if (engineView instanceof WebView) {
                    final WebView webView = (WebView) engineView;

                    WebSettings s = webView.getSettings();
                    s.setJavaScriptEnabled(true);
                    s.setDomStorageEnabled(true);
                    // Spoof User-Agent to remove "wv" so it looks like a real browser
                    String ua = s.getUserAgentString();
                    if (ua != null) {
                        s.setUserAgentString(ua.replace("; wv", ""));
                    }
                    // Allow mixed content to ensure all resources load
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                    }

                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            // Inject the chat-memer script every time a page loads so it persists across navigation
                            String js = "(function(){" +
                                "function init(){" +
                                    "try{" +
                                        "if(document.getElementById('memer-indicator'))return;" +
                                        "var indicator=document.createElement('div');indicator.id='memer-indicator';indicator.style.cssText='position:fixed;top:5px;left:5px;background:transparent;color:green;z-index:2147483647;font-size:12px;font-weight:bold;pointer-events:none;';indicator.textContent='J';document.body.appendChild(indicator);" +
                                        "var style=document.createElement('style');style.textContent='.ad-container, .ad-box, .video-ad-label, .ad-overlay, [id^=\"google_ads\"], [id^=\"div-gpt-ad\"] { display: none !important; }';document.head.appendChild(style);" +
                                    "}catch(e){console.error(e);}" +
                                    "function isImageUrl(url){if(url.match(/\\.(jpeg|jpg|gif|png|webp)$/i))return true;if(url.includes('imgur.com')&&!url.includes('/a/'))return true;if(url.includes('cdn.discordapp.com'))return true;if(url.match(/pbs\\.twimg\\.com.*format=(jpg|png|gif)/i))return true;return false;}" +
                                    "function isVideoUrl(url){if(url.match(/\\.(mp4|webm|ogg)$/i))return true;return false;}" +
                                    "function createImagePreview(url){var img=document.createElement('img');img.src=url;img.style.maxWidth='300px';img.style.maxHeight='400px';img.style.display='block';img.style.marginTop='4px';img.style.borderRadius='4px';img.onload=function(){requestAnimationFrame(function(){var c=document.getElementById('chat-history-list');if(c)c.scrollTop=c.scrollHeight;});};return img;}" +
                                    "function createVideoPreview(url){var v=document.createElement('video');v.src=url;v.style.maxWidth='300px';v.style.maxHeight='400px';v.style.display='block';v.style.marginTop='4px';v.style.borderRadius='4px';v.controls=true;v.autoplay=true;v.muted=true;v.onloadedmetadata=function(){requestAnimationFrame(function(){var c=document.getElementById('chat-history-list');if(c)c.scrollTop=c.scrollHeight;});};return v;}" +
                                    "function watchForLinks(){var c=document.getElementById('chat-history-list');if(!c){console.log('Chat container not found yet, retrying in 1 second...');setTimeout(watchForLinks,1000);return;}console.log('Found chat container, starting to watch for links...');var observer=new MutationObserver(function(mutations){mutations.forEach(function(m){m.addedNodes.forEach(function(n){if(n.nodeType===Node.ELEMENT_NODE){var links=n.querySelectorAll('a, [href]');for(var i=0;i<links.length;i++){var link=links[i];var url=link.href||link.getAttribute('data-url');if(url){if(isImageUrl(url)){link.replaceWith(createImagePreview(url));}else if(isVideoUrl(url)){link.replaceWith(createVideoPreview(url));}}}}});});});observer.observe(c,{childList:true,subtree:true});}" +
                                    "watchForLinks();setTimeout(watchForLinks,2000);" +
                                "}" +
                                "if(document.body)init();else document.addEventListener('DOMContentLoaded',init);" +
                                "})();";
                            
                            view.evaluateJavascript(js, null);
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                            // CRITICAL FIX: Only hijack navigation for the main page.
                            // If we hijack iframes, the app will try to load ads/trackers as the main page.
                            if (!request.isForMainFrame()) return false;

                            String url = request.getUrl().toString();
                            if (url.startsWith("http") || url.startsWith("https")) {
                                return false;
                            }
                            return false;
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            if (url.startsWith("http") || url.startsWith("https")) {
                                return false;
                            }
                            return false;
                        }

                        @Override
                        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                            String url = request.getUrl().toString().toLowerCase();
                            // Block common ad networks to stop video ads and trackers
                            if (url.contains("doubleclick.net") ||
                                url.contains("googleadservices.com") ||
                                url.contains("googlesyndication.com") ||
                                url.contains("adsystem.com") ||
                                url.contains("adnxs.com") ||
                                url.contains("criteo.com") ||
                                url.contains("pubmatic.com") ||
                                url.contains("rubiconproject.com") ||
                                url.contains("amazon-adsystem.com") ||
                                url.contains("moatads.com") ||
                                url.contains("ima3.js") ||
                                url.contains("imasdk") ||
                                url.contains("googleads") ||
                                url.contains("adservice") ||
                                url.contains("pagead") ||
                                url.contains("spotxchange") ||
                                url.contains("springserve")) {
                                return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
                            }
                            return super.shouldInterceptRequest(view, request);
                        }
                    });
                } else {
                    Log.w(TAG, "Engine view is not a WebView; cannot set WebViewClient");
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error setting WebViewClient", e);
        }

        // Load the Cordova WebView URL normally.
        loadUrl("https://rumble.com");

        // Robust Injection: Use a recurring task to ensure the script is injected
        // even if onPageFinished doesn't fire (e.g. SPA navigation) or is overwritten.
        final Handler handler = new Handler();
        final Runnable checkAndInject = new Runnable() {
            @Override
            public void run() {
                try {
                    if (appView != null) {
                        Object engineView = appView.getEngine().getView();
                        if (engineView instanceof WebView) {
                            WebView webView = (WebView) engineView;
                            // Same JS as above, checks for existence before adding
                            webView.evaluateJavascript(getInjectionJs(), null);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Injection failed", e);
                }
                // Check again in 2 seconds
                handler.postDelayed(this, 2000);
            }
        };
        // Start the loop
        handler.postDelayed(checkAndInject, 3000);
    }

    private String getInjectionJs() {
        return "(function(){" +
            "function init(){" +
                "try{" +
                    "if(document.getElementById('memer-indicator'))return;" +
                    "var indicator=document.createElement('div');indicator.id='memer-indicator';indicator.style.cssText='position:fixed;top:5px;left:5px;background:transparent;color:green;z-index:2147483647;font-size:12px;font-weight:bold;pointer-events:none;';indicator.textContent='J';document.body.appendChild(indicator);" +
                    "var style=document.createElement('style');style.textContent='.ad-container, .ad-box, .video-ad-label, .ad-overlay, [id^=\"google_ads\"], [id^=\"div-gpt-ad\"] { display: none !important; }';document.head.appendChild(style);" +
                "}catch(e){console.error(e);}" +
                "function isImageUrl(url){if(url.match(/\\.(jpeg|jpg|gif|png|webp)$/i))return true;if(url.includes('imgur.com')&&!url.includes('/a/'))return true;if(url.includes('cdn.discordapp.com'))return true;if(url.match(/pbs\\.twimg\\.com.*format=(jpg|png|gif)/i))return true;return false;}" +
                "function isVideoUrl(url){if(url.match(/\\.(mp4|webm|ogg)$/i))return true;return false;}" +
                "function createImagePreview(url){var img=document.createElement('img');img.src=url;img.style.maxWidth='300px';img.style.maxHeight='400px';img.style.display='block';img.style.marginTop='4px';img.style.borderRadius='4px';img.onload=function(){requestAnimationFrame(function(){var c=document.getElementById('chat-history-list');if(c)c.scrollTop=c.scrollHeight;});};return img;}" +
                "function createVideoPreview(url){var v=document.createElement('video');v.src=url;v.style.maxWidth='300px';v.style.maxHeight='400px';v.style.display='block';v.style.marginTop='4px';v.style.borderRadius='4px';v.controls=true;v.autoplay=true;v.muted=true;v.onloadedmetadata=function(){requestAnimationFrame(function(){var c=document.getElementById('chat-history-list');if(c)c.scrollTop=c.scrollHeight;});};return v;}" +
                "function watchForLinks(){var c=document.getElementById('chat-history-list');if(!c){console.log('Chat container not found yet, retrying in 1 second...');setTimeout(watchForLinks,1000);return;}console.log('Found chat container, starting to watch for links...');var observer=new MutationObserver(function(mutations){mutations.forEach(function(m){m.addedNodes.forEach(function(n){if(n.nodeType===Node.ELEMENT_NODE){var links=n.querySelectorAll('a, [href]');for(var i=0;i<links.length;i++){var link=links[i];var url=link.href||link.getAttribute('data-url');if(url){if(isImageUrl(url)){link.replaceWith(createImagePreview(url));}else if(isVideoUrl(url)){link.replaceWith(createVideoPreview(url));}}}}});});});observer.observe(c,{childList:true,subtree:true});}watchForLinks();setTimeout(watchForLinks,2000);" +
            "}" +
            "if(document.body)init();else document.addEventListener('DOMContentLoaded',init);" +
            "})();";
    }
}