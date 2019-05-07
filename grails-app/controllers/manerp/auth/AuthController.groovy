package manerp.auth

import grails.converters.JSON
import org.grails.web.json.JSONObject
import tr.com.manerp.auth.AuthenticationToken

class AuthController {

    static namespace = "v1"

    AuthService authService

    def login() {

        def signInResult = authService.login(request.JSON.username.toString(),request.JSON.password.toString())

        if(signInResult.data){

            def tokenValue = authService.generateToken()
            authService.saveAuthInfo(tokenValue,request.JSON.username)
            signInResult.data.token = tokenValue.toString()
        }
        render signInResult as JSON

    }
    def logout() {

        def signInResult = authService.logout(request.getHeaders("Authorization").next.toString())
        render signInResult as JSON

    }

    def forgetPassword(){

    }

    def getUserAllAuthInfo(){

        render authService.getUserAuthInfo(request.JSON.username,
        request.JSON.organizationId) as JSON
    }

    def getAllUserList(){
        render authService.getAllUserList((JSONObject)request.JSON) as JSON
    }
    def addUser(){
        render authService.addUser((JSONObject)request.JSON) as JSON
    }
    def updateUser(){
        render authService.updateUser((JSONObject)request.JSON) as JSON
    }
    def deleteUser(){
        render authService.deleteUser((JSONObject)request.JSON) as JSON
    }
    def getAllRoleList(){
        render authService.getAllRoleList((JSONObject)request.JSON) as JSON
    }
    def addRole(){
        render authService.addRole((JSONObject)request.JSON) as JSON
    }
    def updateRole(){
        render authService.updateRole((JSONObject)request.JSON) as JSON
    }

    def deleteRole(){
        render authService.deleteRole((JSONObject)request.JSON) as JSON
    }

    def getAllRolePermissionList(){

        render authService.getAllRolePermissionList((JSONObject)request.JSON) as JSON
    }
    def addRolePermission(){

        render authService.addRolePermission((JSONObject)request.JSON) as JSON
    }
    def deleteRolePermission(){

        render authService.deleteRolePermission((JSONObject)request.JSON) as JSON
    }
    def getAllSecuritySubjectPermissionList(){

        render authService.getAllSecuritySubjectPermissionList() as JSON
    }

}
