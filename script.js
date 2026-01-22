(function() {
    console.log("Script Alive");

    function createProof() {
        if (!document.getElementById('java-proof-box')) {
            const box = document.createElement('div');
            box.id = 'java-proof-box';
            box.innerHTML = 'JAVA RUNNING';
            box.style = "position:fixed; top:10px; left:10px; background:red; color:white; font-weight:bold; padding:10px; z-index:999999; border:2px solid white;";
            document.body.appendChild(box);
        }
    }

    // Runs the proof and the meme scanner
    setInterval(() => {
        createProof();
        
        // Meme scanner
        const links = document.querySelectorAll('a:not(.processed)');
        links.forEach(link => {
            if (link.href.match(/\.(jpg|jpeg|png|gif|webp)$/i)) {
                link.classList.add('processed');
                const img = document.createElement('img');
                img.src = link.href;
                img.style = "display:block; max-width:200px; border:1px solid lime;";
                link.after(img);
            }
        });
    }, 1000);
})();
