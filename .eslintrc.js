module.exports = {
    "env": {
        "es6": true,
        "node": true,
        "react-native/react-native": true
    },
    "extends": [
        "eslint:recommended",
        "plugin:react/recommended"
    ],
    "parser": "babel-eslint",
    "parserOptions": {
        "ecmaFeatures": {
            "experimentalObjectRestSpread": true,
            "jsx": true
        },
        "sourceType": "module"
    },
    "plugins": [
        "react",
        "react-native",
        "react-hooks"
    ],
    "rules": {
        "indent": [
            "error",
            2,
            { "SwitchCase": 1 }
        ],
        "linebreak-style": [
            "error",
            "unix"
        ],
        "quotes": [
            "error",
            "single"
        ],
        "semi": [
            "error",
            "always"
        ],
        "comma-style": [
            "error",
            "last"
        ],
        "comma-dangle": [
            "error", "only-multiline"
        ],
        "no-console": "off",
        "no-undef": "off",
        "react-native/no-unused-styles": 2,
        "react-native/split-platform-components": 2,
        // "react-native/no-color-literals": 2,
        "react-hooks/exhaustive-deps": 'warn'
    }
};