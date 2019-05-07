package manerp.auth.util

import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.grails.web.json.JSONObject

class RestUtil {

    static JSONObject callRestService(String restUrl, JSONObject params = null) {

        RestBuilder restBuilder = new RestBuilder()
        Map<String, Object> urlVariables = new HashMap()

        //log.info("callRestService:   restUrl: "+restUrl)

        if(params!=null){

            Iterator<?> keys = params.keys()

            while(keys.hasNext()){

                String key = keys.next().toString()
                urlVariables[key] = params.get(key)
            }

            //log.info("callRestService:   params: "+urlVariables.toString())
        }

        RestResponse restResponse = restBuilder.post(restUrl) {
            contentType('application/json;charset=UTF-8')
            json(urlVariables)
        }

        //Islem basarili ise response'dan gelen body donulecek.
        if(restResponse.status == 200){

            //log.info(restUrl + " servisi basarili bir sekilde dondu.")
            //log.info("Rest Response ::: "+restResponse.responseEntity.body.toString())
            return new JSONObject(restResponse.responseEntity.body.toString())
        }

        else{

            //log.info(restUrl + " servisinde hata olustu !!!")
            throw new Exception((String)new JSONObject(restResponse.responseEntity.body.toString()).get("errors")[0].detailKey)
        }
    }
}
