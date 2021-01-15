# Security

## Components

### SessionController

Performs login and logout operations, stores issued token to request's cookies. Delegates authentication and issuing of tokens to corresponding services.

### AuthenticationManager (spring)

Encapsulates logic of authentication. There is two different authentication managers - LDAP and InMemory. Both are configured in `config/security` package.

### TokenService

Used for issuing, signing and verifying of JWT tokens.

### CookieFactory

Used for creation of cookies.

### AnonymousAuthenticationFilter

Applies only for requests without any authentication. Also, it isn't doing anything if there is already one anonymous token stored in cookies.

Creates new anonymous token with roles stored in configuration. Stores this anonymous token to cookies and to the Spring's security context. 

### JWTAuthenticationFilter

Extracts authentication from requests, performs verification and it to the Spring's security context.

### LogoutFilter

Used to clear cookies after logout.

## Where is authentication stored

Client-side stores authentication data in cookies as JWT. Besides, there is сsrf token stored in local storage.

Server-side doesn't store authentication data anywhere.

#### CSRF

If CSRF is enabled `TokenService` pushes it to the issued JWT. In this case client gets CSRF in response after authentication. CSRF token must be stored in local storage and must be present in every request in `X-CSRF-TOKEN` header.

`JWTAuthenticationFilter` compares CSRF token from `X-CSRF-TOKEN` header with token stored in JWT.

## Flows

### Authentication (Login) Flow
![Authentication](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/qiwi/k8s-mission-control/master/doc/security/authentication.iuml)

### Request Authorization Flow
![Authorization](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/qiwi/k8s-mission-control/master/doc/security/authorization.iuml)
