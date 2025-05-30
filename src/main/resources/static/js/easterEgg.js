// Easter egg code - activated by typing "ecycle"
let keySequence = [];
const eggCode = ['e', 'c', 'y', 'c', 'l', 'e', 'n', '1'];

document.addEventListener('keydown', function(e) {
    keySequence.push(e.key.toLowerCase());
    
    // Keep only the last 6 keys
    if (keySequence.length > eggCode.length) {
        keySequence.shift();
    }
    
    // Check if the sequence matches
    if (keySequence.join('') === eggCode.join('')) {
        triggerEasterEgg();
        keySequence = []; // Reset after triggering
    }
});

function triggerEasterEgg() {
    const duration = 5000; // 5 seconds
    const imgCount = 30; // Number of falling images
    
    // Create container for the eggs
    const eggContainer = document.createElement('div');
    eggContainer.className = 'easter-egg-container';
    document.body.appendChild(eggContainer);
    
    // Create multiple falling eggs
    for (let i = 0; i < imgCount; i++) {
        setTimeout(() => {
            const img = document.createElement('img');
            img.src = '/images/logos/logoIcon.png';
            img.className = 'falling-egg';
            img.style.left = `${Math.random() * 100}%`;
            img.style.animationDuration = `${Math.random() * 2 + 2}s`; // Between 2-4s
            img.style.width = `${Math.random() * 30 + 30}px`; // Random size
            
            eggContainer.appendChild(img);
            
            // Remove the image after it finishes falling
            img.addEventListener('animationend', () => {
                img.remove();
            });
        }, Math.random() * 1000); // Stagger the creation time
    }
    
    // Remove container after duration
    setTimeout(() => {
        eggContainer.remove();
    }, duration);
}
