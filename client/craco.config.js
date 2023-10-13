const path = require("path")
const { CracoAliasPlugin } = require('react-app-alias')

module.exports = {
    plugins: [
        {
            plugin: CracoAliasPlugin,
            options: {}
        }
    ],
    webpack: {
        alias: {
            '@components': path.resolve(__dirname, 'src/components'),
            '@hooks': path.resolve(__dirname, 'src/hooks'), 
            '@contexts': path.resolve(__dirname, 'src/contexts'), 
            '@views': path.resolve(__dirname, 'src/views'),
            '@services': path.resolve(__dirname, 'src/services'),
        }
    }
}