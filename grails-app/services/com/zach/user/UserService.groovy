package com.zach.user

import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class UserService {
    def sqlUtilService

    def login(username, password) {
        def dbInstance = sqlUtilService.getInstance()
        def rows = dbInstance.rows("select * from Muser where username = '"+username+"' and mpassword = '"+password+"'")
        return rows
    }

    def test(){
        def dbInstance = sqlUtilService.getInstance()
//        def value = dbInstance.rows("select * from Goods where fname = '胡萝卜'")

//        def value = dbInstance.call("select dbo.EncryptStr('123', 0822)")
        dbInstance.call("{? = call dbo.EncryptStr(?, 0822)}", [Sql.VARCHAR, '123']){
            println it
        }
        return value
    }
}
