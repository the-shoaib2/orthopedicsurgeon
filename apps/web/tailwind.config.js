/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./src/**/*.{html,ts}",
        "../../packages/*/src/**/*.{html,ts}"
    ],
    theme: {
        extend: {
            colors: {
                primary: {
                    500: '#298afaff',
                    600: '#1249c0ff',
                },
                secondary: {
                    50: '#f8fafc',
                    900: '#050e39ff',
                }
            },
            fontFamily: {
                inter: ['Inter', 'sans-serif'],
            }
        },
    },
    plugins: [],
}
