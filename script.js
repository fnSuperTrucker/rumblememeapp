(function() {
    console.log("Meme Script Loaded");

    function injectImages() {
        // Updated selectors for 2026 Rumble Chat
        const messages = document.querySelectorAll('.chat-history--message-text, .chat-item--text');
        
        messages.forEach(msg => {
            const links = msg.querySelectorAll('a:not(.processed)');
            links.forEach(link => {
                const url = link.href.toLowerCase();
                if (url.match(/\.(jpeg|jpg|gif|png|webp)$/) != null) {
                    link.classList.add('processed');
                    const img = document.createElement('img');
                    img.src = link.href;
                    img.style = "display:block; max-width:250px; border-radius:10px; margin-top:5px; border:1px solid #555;";
                    link.after(img);
                }
            });
        });
    }

    // Runs every second to catch new messages
    setInterval(injectImages, 1000);
})();
