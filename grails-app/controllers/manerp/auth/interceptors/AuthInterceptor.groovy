package manerp.auth.interceptors

import groovy.transform.CompileStatic
import manerp.auth.AuthService

class AuthInterceptor {
    AuthService authService

    AuthInterceptor(){

        def matcher = matchAll()

        matcher
    }

    boolean before() {

        if(controllerName != "auth" && controllerName != "redis" && controllerName != "rest" &&  actionName != "isValidUser") {

            if(request.getHeaders("Authorization").next == null){
                response.sendError(403, "Yetkisiz erişim.")
                return false
            }

            log.info("before:   controllerName:" + controllerName + " | actionName:" + actionName)

            boolean authResult = authService.validateToken(request.getHeaders("Authorization").next.toString())

            log.info("Auth Result for " + controllerName + " : " + actionName + " : " + authResult)

            if (authResult) {

                return authResult
            }
            response.sendError(403, "Yetkisiz erişim.")
            return false
        }
        return true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
