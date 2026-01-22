(function() {
    console.log("Rumble Memer 1.7: INJECTED");

    // 1. THE PROOF: Create a box that sits on top of Rumble
    const proof = document.createElement('div');
    proof.id = 'java-proof-box';
    proof.innerHTML = 'JAVA RUNNING';
    proof.style = "position:fixed; top:20px; left:50%; transform:translateX(-50%); background:lime; color:black; font-weight:bold; padding:15px 30px; border:3px solid white; z-index:999999; border-radius:10px; font-family:sans-serif; box-shadow:0 5px 15px rgba(0,0,0,0.5);";
    document.body.appendChild(proof);

    // Make proof box disappear after 5 seconds so it's not in the way
    setTimeout(() => { proof.style.display = 'none'; }, 50000);

    // 2. THE MEME SCANNER: Converts links to images
    function scanChat() {
        const links = document.querySelectorAll('.chat-history--message-text a:not(.processed), .chat-item--text a:not(.processed)');
        links.forEach(link => {
            const url = link.href.toLowerCase();
            if (url.match(/\.(jpeg|jpg|gif|png|webp)$/) != null) {
                link.classList.add('processed');
                const img = document.createElement('img');
                img.src = link.href;
                img.style = "display:block; max-width:250px; border-radius:10px; margin-top:5px; border:1px solid #444;";
                link.after(img);
            }
        });
    }
    setInterval(scanChat, 1000);
})();
