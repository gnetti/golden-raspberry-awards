function isMobile() {
    return window.innerWidth <= 768;
}

function loadIntervalsModal() {
    if (isMobile()) {
        return false;
    }

    const modal = new bootstrap.Modal(document.getElementById('intervalsModal'));
    const modalBody = document.getElementById('intervalsModalBody');
    
    modalBody.innerHTML = '<div class="text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div></div>';
    
    fetch('/intervals?modal=true')
        .then(response => response.text())
        .then(html => {
            modalBody.innerHTML = html;
        })
        .catch(error => {
            console.error('Error loading intervals:', error);
            modalBody.innerHTML = '<div class="alert alert-danger">Error loading intervals data.</div>';
        });
    
    modal.show();
    return true;
}

function loadSwaggerModal() {
    if (isMobile()) {
        return false;
    }

    const modal = new bootstrap.Modal(document.getElementById('swaggerModal'));
    const iframe = document.getElementById('swaggerIframe');
    
    iframe.src = '/swagger-ui.html';
    modal.show();
    return true;
}

function loadJavadocModal() {
    if (isMobile()) {
        return false;
    }

    const modal = new bootstrap.Modal(document.getElementById('javadocModal'));
    const iframe = document.getElementById('javadocIframe');
    
    iframe.src = '/docs/index.html';
    modal.show();
    return true;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function loadOpenApiModal() {
    if (isMobile()) {
        return false;
    }

    const modal = new bootstrap.Modal(document.getElementById('openapiModal'));
    const content = document.getElementById('openapiContent');
    
    content.innerHTML = '<code class="language-json">Loading...</code>';
    
    fetch('/api-docs')
        .then(response => response.json())
        .then(data => {
            const jsonText = JSON.stringify(data, null, 2);
            content.innerHTML = '<code class="language-json">' + escapeHtml(jsonText) + '</code>';
        })
        .catch(error => {
            console.error('Error loading OpenAPI spec:', error);
            content.innerHTML = '<code class="text-danger">Error loading OpenAPI specification.</code>';
        });
    
    modal.show();
    return true;
}

function loadMoviesApiModal() {
    if (isMobile()) {
        return false;
    }

    const modal = new bootstrap.Modal(document.getElementById('moviesApiModal'));
    const content = document.getElementById('moviesApiContent');
    
    content.innerHTML = '<code class="language-json">Loading...</code>';
    
    fetch('/api/movies')
        .then(response => response.json())
        .then(data => {
            const jsonText = JSON.stringify(data, null, 2);
            content.innerHTML = '<code class="language-json">' + escapeHtml(jsonText) + '</code>';
        })
        .catch(error => {
            console.error('Error loading movies API:', error);
            content.innerHTML = '<code class="text-danger">Error loading movies data.</code>';
        });
    
    modal.show();
    return true;
}

function loadH2ConsoleModal() {
    if (isMobile()) {
        return false;
    }

    const modal = new bootstrap.Modal(document.getElementById('h2ConsoleModal'));
    const iframe = document.getElementById('h2ConsoleIframe');
    
    iframe.src = '/h2-console';
    
    iframe.onload = function() {
        try {
            const iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
            const iframeHead = iframeDoc.head || iframeDoc.getElementsByTagName('head')[0];
            
            const link = iframeDoc.createElement('link');
            link.rel = 'stylesheet';
            link.type = 'text/css';
            link.href = '/css/h2-console-dark.css';
            iframeHead.appendChild(link);
            
            fetch('/css/h2-console-inline.css')
                .then(response => response.text())
                .then(cssText => {
                    const style = iframeDoc.createElement('style');
                    style.textContent = cssText;
                    iframeHead.appendChild(style);
                })
                .catch(error => {
                    console.warn('Could not load H2 Console inline CSS:', error);
                });
        } catch (e) {
            console.warn('Could not inject CSS into H2 Console iframe:', e);
        }
    };
    
    modal.show();
    return true;
}

document.addEventListener('DOMContentLoaded', function() {
    const intervalsLinks = document.querySelectorAll('a[href="/intervals"], a[href*="/intervals"]');
    intervalsLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!isMobile() && !this.hasAttribute('data-force-page')) {
                e.preventDefault();
                loadIntervalsModal();
            }
        });
    });

    const swaggerLinks = document.querySelectorAll('a[href="/swagger-ui.html"], a[href*="swagger-ui"]');
    swaggerLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!isMobile() && !this.hasAttribute('data-force-page') && !this.hasAttribute('target')) {
                e.preventDefault();
                loadSwaggerModal();
            }
        });
    });

    const javadocLinks = document.querySelectorAll('a[href="/docs/index.html"], a[href*="/docs/"]');
    javadocLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!isMobile() && !this.hasAttribute('data-force-page') && !this.hasAttribute('target')) {
                e.preventDefault();
                loadJavadocModal();
            }
        });
    });

    const openapiLinks = document.querySelectorAll('a[href="/api-docs"], a[href*="api-docs"]');
    openapiLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!isMobile() && !this.hasAttribute('data-force-page') && !this.hasAttribute('target')) {
                e.preventDefault();
                loadOpenApiModal();
            }
        });
    });

    const moviesApiLinks = document.querySelectorAll('a[href="/api/movies"], a[href*="/api/movies"]');
    moviesApiLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!isMobile() && !this.hasAttribute('data-force-page') && !this.hasAttribute('target')) {
                e.preventDefault();
                loadMoviesApiModal();
            }
        });
    });

    const h2ConsoleLinks = document.querySelectorAll('a[href="/h2-console"], a[href*="h2-console"]');
    h2ConsoleLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!isMobile() && !this.hasAttribute('data-force-page') && !this.hasAttribute('target')) {
                e.preventDefault();
                loadH2ConsoleModal();
            }
        });
    });
});

