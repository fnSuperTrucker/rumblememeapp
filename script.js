(function() {
    console.log("Rumble Memer 1.4: Online");

    function processChat() {
        // Targets the Rumble chat links specifically
        const chatLinks = document.querySelectorAll('.chat-history--message-text a:not(.processed), .chat-item--text a:not(.processed)');
        
        chatLinks.forEach(link => {
            const url = link.href.toLowerCase();
            // Look for image extensions
            if (url.match(/\.(jpeg|jpg|gif|png|webp)$/) != null) {
                link.classList.add('processed');
                
                const img = document.createElement('img');
                img.src = link.href;
                img.style.display = "block";
                img.style.maxWidth = "250px";
                img.style.maxHeight = "300px";
                img.style.borderRadius = "12px";
                img.style.marginTop = "8px";
                img.style.border = "2px solid #333";
                
                link.after(img);
            }
        });
    }

    // Runs every 1000ms (1 second) to catch new messages
    setInterval(processChat, 1000);
})();
