let keySequence = [];
const eggCode = ['e', 'c', 'y', 'c', 'l', 'e', 'n', '1'];

document.addEventListener('keydown', function(e) {
    keySequence.push(e.key.toLowerCase());
    
    if (keySequence.length > eggCode.length) {
        keySequence.shift();
    }
    
    if (keySequence.join('') === eggCode.join('')) {
        triggerEasterEgg();
        keySequence = [];
    }
});

function triggerEasterEgg() {
    const duration = 5000;
    const imgCount = 30;
    
    const eggContainer = document.createElement('div');
    eggContainer.className = 'easter-egg-container';
    document.body.appendChild(eggContainer);
    
    for (let i = 0; i < imgCount; i++) {
        setTimeout(() => {
            const img = document.createElement('img');
            img.src = '/images/logos/logoIcon.png';
            img.className = 'falling-egg';
            img.style.left = `${Math.random() * 100}%`;
            img.style.animationDuration = `${Math.random() * 2 + 2}s`;
            img.style.width = `${Math.random() * 30 + 30}px`;
            
            eggContainer.appendChild(img);
            
            img.addEventListener('animationend', () => {
                img.remove();
            });
        }, Math.random() * 1000);
    }
    
    setTimeout(() => {
        eggContainer.remove();
    }, duration);
}
