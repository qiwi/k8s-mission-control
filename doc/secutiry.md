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
```
participant Web
participant Controller
participant AuthManager
participant TokenService
participant CookieFactory

Web->Controller: POST /api/sessions\n{ userName, password }

Controller->AuthManager: authenticate\n{ userName, password }


AuthManager->Controller: { auth object }

alt not authenticated

Controller->Web: { 400 BAD REQUEST\ninvalid-credentials }

end

Controller->TokenService: issue signed token
TokenService->Controller: { token }

alt cookie is required

Controller->CookieFactory: create cookie\n{ token }
CookieFactory->Controller: { cookie }
Controller->Controller: set cookie to response

end

Controller->Web: 200 OK\n { userName, displayName, roles,\ncsrfToken, token (only if cookie is not required) }
```

### Request Authorization Flow
```
participant Web
participant API
participant "JWTAuthenticationFilter" as JWTFilter
participant "AnonymousAuthenticationFilter" as AnonFilter

participant TokenService
participant CookieFactory
participant UserDetailsService

Web->API: filter request

API->JWTFilter:

JWTFilter->JWTFilter: extract token and csrf

JWTFilter->TokenService: verify token and csrf

JWTFilter->UserDetailsService: check user

JWTFilter->API:

API->AnonFilter: filter request

alt if there is authentication

AnonFilter->API: ok

else if there is anon token

AnonFilter->AnonFilter: put authentication\nto context
AnonFilter->API: ok

else

AnonFilter->TokenService: issue and sign anon auth
TokenService->AnonFilter: { anon token }

AnonFilter->CookieFactory: create cookie
CookieFactory->AnonFilter: { cookie }
AnonFilter->AnonFilter: put cookie and \nauthentication\nto context

AnonFilter->API: ok
end



API->Web:
```