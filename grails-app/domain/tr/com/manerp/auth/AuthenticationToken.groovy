package tr.com.manerp.auth

import grails.databinding.BindingFormat

class AuthenticationToken {

    String id
    String tokenValue
    String username
    @BindingFormat('dd/MM/yyyy')
    Date tokenExpiredDate
    @BindingFormat('dd/MM/yyyy')
    Date tokenCreatedDate

    static constraints = {

        tokenValue blank: false, unique: true
        username nullable:false,blank:false
        tokenExpiredDate nullable: false
        tokenCreatedDate nullable: false
    }

    static mapping = {
        version false
        id column: 'id', generator: 'uuid', length: 32
        table schema: 'auth'
    }
}
