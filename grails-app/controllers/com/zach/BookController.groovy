package com.zach

import grails.converters.JSON

class BookController {

    def bookService

    def index() {

    }

    def getBook() {
        bookService.getBook()
        render 'ok'
    }

    def saveBook() {
        bookService.saveBook()
        render 'ok'
    }

    def deleteBook() {
        bookService.deleteBook()
        render 'ok'

    }

    def queryBook() {
        def result = bookService.queryBook()
        render result
    }

    def modifyBook() {
        bookService.modifyBook()
        render 'ok'
    }
}
