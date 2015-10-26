package com.zach.goods

import grails.converters.JSON

class GoodsController {
    def goodsService

    def index() {
        render "index"
    }

    def saveGoods() {
        println goodsService.saveGoods()
        render "save"
    }

    def deleteGoods() {
        println goodsService.deleteGoods()
        render "delete"
    }

    def queryGoods() {
        def  number = request.JSON.number
        def  barcode = request.JSON.barcode

        def statusMsg = "操作成功"
        def statusCode = "200"

        def result  = ""    //没有数据默认返回空串
        def value =  goodsService.queryGoodsByNumberOrBarcode(number, barcode)


        if(value.size() == 0){
            statusMsg = "无数据"
            statusCode = "400"
        }else{
            result = value[0]
        }
        def responseData = ["statusCode":statusCode,"statusMsg":statusMsg,"result":result]

        render responseData as JSON
    }

    def modifyGoods() {
        println goodsService.modifyGoods()
        render "modify"
    }
}
