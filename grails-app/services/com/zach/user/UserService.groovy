package com.zach.user

import grails.converters.JSON
import grails.transaction.Transactional
import java.lang.*

@Transactional
class UserService {
    def sqlUtilService
    def pswUtil
    def desUtil

    def login(username, password, mac) {
        def dbInstance = sqlUtilService.getInstance()

        //获取密码加密之后的密文
        def encodedPassword = pswUtil.getEncodedPsw(password);


        def rows = dbInstance.rows("select * from Muser where username = '"+username+"' and mpassword = '"+encodedPassword+"'")

        //获取用户的mac地址
        def macValidated = false
        def macs   //原先的mac地址
        def macsDes = []  //解密之后的mac地址
        def file = new File("C:\\设备配置\\07用户.txt")
        if(file.exists()){
            macs =  file.text.split(",")
        }
        //将所有的字符串解密
        macs.each{
            def r = desUtil.decrypt(it, "macdizhi")
            macsDes << r
        }

        macsDes.each{
            if(it == mac){
                macValidated = true;
            }
        }

        //要是mac地址正确就让用户登陆
        if(false == macValidated){
            rows = []
        }

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
