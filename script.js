
(function() {
    console.log("Rumble Memer 1.2 Active");

    // Function to scan chat for links and turn them into images
    function showMemes() {
        // Target Rumble's chat message links
        const links = document.querySelectorAll('.chat-history--message-text a:not(.meme-processed)');
        
        links.forEach(link => {
            const url = link.href.toLowerCase();
            
            // Check if the link is a common image format
            if (url.match(/\.(jpeg|jpg|gif|png|webp)$/) != null) {
                // Mark as processed so we don't loop forever
                link.classList.add('meme-processed');

                // Create the image element
                const img = document.createElement('img');
                img.src = link.href;
                img.style.display = 'block';
                img.style.maxWidth = '100%';
                img.style.maxHeight = '300px';
                img.style.borderRadius = '8px';
                img.style.marginTop = '5px';
                img.style.border = '1px solid #444';

                // Insert the image directly after the link
                link.parentNode.insertBefore(img, link.nextSibling);
            }
        });
    }

    // Run every 1 second to catch new messages as they arrive
    setInterval(showMemes, 1000);
})();
