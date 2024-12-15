export default [
    {
        languageOptions: {
            ecmaVersion: 'latest',
            sourceType: 'module',
            globals: {
                window: 'readonly',
                document: 'readonly',
                console: 'readonly',
                fetch: 'readonly',
                setTimeout: 'readonly',
                clearTimeout: 'readonly',
                setInterval: 'readonly',
                clearInterval: 'readonly',
                localStorage: 'readonly',
                sessionStorage: 'readonly',
                location: 'readonly',
                history: 'readonly',
                navigator: 'readonly',
                URL: 'readonly',
                URLSearchParams: 'readonly',
                MutationObserver: 'readonly',
                getComputedStyle: 'readonly',
                bootstrap: 'readonly',
                Bootstrap: 'readonly',
                isMobile: 'readonly',
                loadMoviesModal: 'readonly',
                loadManualModal: 'readonly',
                loadMoviesApiModal: 'readonly',
            },
        },
        rules: {
            'no-unused-vars': [
                'warn',
                {
                    argsIgnorePattern: '^_',
                    varsIgnorePattern: '^_',
                },
            ],
            'no-console': 'off',
            'no-undef': 'warn',
        },
    },
];

