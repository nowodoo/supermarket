package com.zach.util

import grails.transaction.Transactional
import groovy.sql.Sql

@Transactional
class SqlUtilService {
    def static department = new File("C:\\设备配置\\01部门.txt").text
    def static headIP = new File("C:\\设备配置\\02总部数据库IP.txt").text
    def static branchIP = new File("C:\\设备配置\\03分部数据库IP.txt").text
    def static port = new File("C:\\设备配置\\04数据库端口.txt").text
    def static username = new File("C:\\设备配置\\05用户名.txt").text
    def static password = new File("C:\\设备配置\\06密码.txt").text
    def static sqlInstance = (department=='总部')?(Sql.newInstance("jdbc:jtds:sqlserver://"+headIP+":"+port+"/head_kchainii;CharacterSet=UTF-8",username,password,"net.sourceforge.jtds.jdbc.Driver")):(Sql.newInstance("jdbc:jtds:sqlserver://"+branchIP+":"+port+"/branch_kjxcii;CharacterSet=UTF-8",username,password,"net.sourceforge.jtds.jdbc.Driver"))

    static def  getInstance(){
        return sqlInstance;
    }

    def serviceMethod() {
        println "test"
    }

    def save() {
        println ipAddress
        println "saved"
    }

    def delete() {
        println "delete"
    }

    /**
     * 根据表名字字段名字查询需要的数据
     * 根据tablename, column, 和 具体的value需要取出哪一个数值(selectedValue)，
     */
    def query(tableName, column, columnValue, selectedValue) {
        def list = []
        def sql = "select "+selectedValue+" from "+tableName+" where " + column +" = '" +columnValue+"'"
        println sql

        sqlInstance.eachRow(sql,
                {
                    list << it[selectedValue]
                }
        )

        println list.size()
        return list;
    }


    def modify() {
        println "modify"
    }
}
