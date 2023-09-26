export const oktaConfig = {
    clientId: '0oab8knbrrRUJcVy65d7',
    issuer: 'https://dev-72564527.okta.com/oauth2/default',
    redirectUri: 'https://localhost:3000/login/callback',
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    disableHttpsCheck: true,
}