package com.zach

import grails.transaction.Transactional
import groovy.sql.*

@Transactional
class BookService {
    def sqlUtilService

    def getBook() {
        println 'get'
        def sql = Sql.newInstance("jdbc:jtds:sqlserver://192.168.208.63:1400/pubs","sa","sa","net.sourceforge.jtds.jdbc.Driver");
        println 'connected'
        sql.eachRow("select * from authors",
                {
                    println it
                }
        )
    }

    def saveBook() {
        sqlUtilService.save()
    }

    def deleteBook() {
        sqlUtilService.delete()
    }

    def queryBook() {
        return sqlUtilService.query("authors","zip","94025","contract")
    }

    def modifyBook() {
        sqlUtilService.modify()
    }
}
