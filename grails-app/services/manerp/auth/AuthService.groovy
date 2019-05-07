package manerp.auth

import grails.gorm.transactions.Transactional
import grails.plugins.redis.RedisService
import grails.util.Holders
import groovy.util.logging.Slf4j
import manerp.auth.enums.RedisSyncType
import manerp.auth.util.RestUtil
import org.apache.commons.lang.time.DateUtils
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import redis.clients.jedis.Jedis
import tr.com.manerp.auth.AuthenticationToken

@Slf4j
class AuthService {

    RedisService redisService

    final static String AUTH_REST_URL = Holders.config.manerp.rest.manerpPortal.url.toString()

    def login(String username, String password) {

        JSONObject jsonObject = new JSONObject(username:username, password:password as String)

        def restResponse = RestUtil.callRestService(AUTH_REST_URL+"/api/v1/user/isValidUser",jsonObject)

        return restResponse

    }

    def logout(String tokenValue){
        AuthenticationToken token = AuthenticationToken.findByTokenValue(tokenValue)
        token.delete(flush:true, failOnError: true)
    }

    def saveAuthInfo(UUID tokenValue, String username){

        AuthenticationToken authenticationToken = new AuthenticationToken()
        authenticationToken.username = username
        authenticationToken.tokenValue = tokenValue
        Date tokenCreatedDate = new Date()
        authenticationToken.tokenCreatedDate = tokenCreatedDate
        Date tokenExpiredDate = DateUtils.addHours(tokenCreatedDate, 2);
        authenticationToken.tokenExpiredDate = tokenExpiredDate

        authenticationToken.save(flush: true, failOnError: true)
    }

    def getUserAuthInfo(String username, String organizationId) {

        HashMap jsonMap = new HashMap()

            jsonMap.permissionList = readFromRedis(username,organizationId,RedisSyncType.PERMISSION)

            jsonMap.menuItemList = readFromRedis(username,organizationId,RedisSyncType.MENU)


        return jsonMap

    }

    def getAllUserList(JSONObject requestParams){
        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.user.getAllUserList.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONArray
        }

        null

    }
    def addUser(JSONObject requestParams){
        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.user.addUser.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONObject
        }

        null
    }
    def updateUser(JSONObject requestParams){
        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.user.updateUser.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONObject
        }

        null
    }
    def deleteUser(JSONObject requestParams){
        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.user.deleteUser.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONObject
        }

        null
    }

    def getAllRoleList(JSONObject requestParams){

        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.role.getAllRoleList.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONArray
        }

        null
    }

    def addRole(JSONObject requestParams){
        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.role.addRole.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONObject
        }

        null
    }

    def updateRole(JSONObject requestParams){
        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.role.updateRole.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONObject
        }

        null
    }
    def deleteRole(JSONObject requestParams){
        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.role.deleteRole.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONObject
        }

        null
    }

    def getAllRolePermissionList(JSONObject requestParams){

        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.rolePermission.getAllRolePermissionList.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONArray
        }

        null
    }
    def addRolePermission(JSONObject requestParams){

        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.rolePermission.addRolePermission.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONObject
        }

        null
    }
    def deleteRolePermission(JSONObject requestParams){

        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.rolePermission.deleteRolePermission.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONObject
        }

        null
    }
    def getAllSecuritySubjectPermissionList(JSONObject requestParams = null){
        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
        .append(Holders.config.manerp.auth.restMethod.securitySubjectPermission.getAllSecuritySubjectPermissionList.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONArray
        }

        null
    }

    @Transactional
    JSONArray readFromRedis(String username, String organizationId, RedisSyncType redisSyncType) {

        String key
        JSONArray authResultList = new JSONArray()

        redisService.withRedis { Jedis jedis->

            if(redisSyncType.equals(RedisSyncType.PERMISSION) || redisSyncType.equals(RedisSyncType.MENU)){

                key = organizationId+":"+username+":"
            }

            key = key + redisSyncType.toString()

            log.info("readFromRedis   :    redisKey= "+key)

            if(jedis.exists(key)){

                jedis.hgetAll(key).values().each {it->

                    authResultList.add(new JSONObject(it.toString()))
                }
            }

            else{

                log.warn("readFromRedis:   "+username+" kullanicisi icin "+redisSyncType.toString()+" kategorisinde redis-db senkronizasyonu yoktur!!!")
                authResultList = synchronizeRedisWithDB(new JSONObject(orgId:organizationId,username:username,redisSyncType:redisSyncType.name(),key:key))

                log.info("readFromRedis:   "+username+" kullanicisi icin "+redisSyncType.toString()+" kategorisinde redis-db senkronizasyonu tamamlandi.")
            }
        }

        return authResultList
    }

    JSONArray synchronizeRedisWithDB(JSONObject requestParams){

        String fullAuthRestUrl = new StringBuilder().append(AUTH_REST_URL)
                .append(Holders.config.manerp.auth.restMethod.synchronizeRedisWithDB.toString()).toString()

        JSONObject resultJSON = RestUtil.callRestService(fullAuthRestUrl,requestParams)

        if(resultJSON?.data){

            return resultJSON?.data as JSONArray
        }

        null
    }

    boolean validateToken(String token){

        boolean validation = false
        AuthenticationToken authToken = AuthenticationToken.findByTokenValue(token)
        if(authToken){
            if(authToken.tokenExpiredDate > new Date()){
                validation = true
            }
            else{
                authToken.tokenCreatedDate = new Date()
                authToken.tokenExpiredDate = DateUtils.addHours(tokenCreatedDate, 2)
            }
        }
        else{
            validation = false
        }

        return validation

    }

    UUID generateToken(){

        UUID uuid = UUID.randomUUID()

        return uuid
    }
}
