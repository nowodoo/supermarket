package com.zach.user

import grails.converters.JSON
import grails.transaction.Transactional
import java.lang.*

@Transactional
class UserService {
    def sqlUtilService
    def pswUtil

    def login(username, password) {
        def dbInstance = sqlUtilService.getInstance()

        //获取密码加密之后的密文
        def encodedPassword = pswUtil.getEncodedPsw(password);
        println(encodedPassword);


        def rows = dbInstance.rows("select * from Muser where username = '"+username+"' and mpassword = '"+encodedPassword+"'")
        return rows
    }

    def getCheckSubmitParameter(){
        return []
    }

    def test(){
        def dbInstance = sqlUtilService.getInstance()
        dbInstance.call("{? = call dbo.EncryptStr(?, 0822)}", [Sql.VARCHAR, '123']){
            println it
        }
        return value
    }
}
