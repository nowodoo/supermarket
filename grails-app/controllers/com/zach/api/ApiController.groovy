package com.zach.api

import grails.converters.JSON

class ApiController {

    def userService
    def goodsService

    def index() {
        render "ok"
    }

    /**
     *用户登陆的接口
     * @return
     */
    def login() {
        def username = request.JSON.username
        def password = request.JSON.password

        def statusMsg = "操作成功"
        def statusCode = "200"
        def result  = ""    //无数据的时候就是空字符串，因为已经有一个错误码400的判断了，所以这里不需要太担心。


        def value = userService.login(username, password);

        if(value.size() == 0){
            statusMsg = "无数据"
            statusCode = "400"
        }else{
            result = value[0]
        }
        def responseData = ["statusCode":statusCode,"statusMsg":statusMsg,"result":result]

        render responseData as JSON
    }

    /**
     *   获取盘点的日期
     */
    def getCheckDate(){
        def value = goodsService.getCheckDate()
        render value as JSON
    }

    /**
     * 获取盘点的新单号
     */
    def getCheckNextNo(){
        def statusMsg = "操作成功"
        def statusCode = "200"
        def result  = ""    //无数据的时候就是空字符串，因为已经有一个错误码400的判断了，所以这里不需要太担心。

        def value = goodsService.getCheckNextNo()

        if(value.size() == 0){
            statusMsg = "无数据"
            statusCode = "400"
        }else{
            result = value[0]
        }
        def responseData = ["statusCode":statusCode,"statusMsg":statusMsg,"result":result]

        render responseData as JSON
    }


    /**
     * 获取收货的新单号
     */
    def getReceiveNextNo(){
        def statusMsg = "操作成功"
        def statusCode = "200"
        def result  = ""    //无数据的时候就是空字符串，因为已经有一个错误码400的判断了，所以这里不需要太担心。

        def value = goodsService.getReceiveNextNo()

        if(value.size() == 0){
            statusMsg = "无数据"
            statusCode = "400"
        }else{
            result = value[0]
        }
        def responseData = ["statusCode":statusCode,"statusMsg":statusMsg,"result":result]

        render responseData as JSON
    }

    /**
     * 盘点单进行提交
     */
    def checkSubmit(){
        //获取前台传递过来的参数
        def checkDate = params.JSON.checkDate

        def statusMsg = "操作成功"
        def statusCode = "200"
        def result  = ""    //无数据的时候就是空字符串，因为已经有一个错误码400的判断了，所以这里不需要太担心。

        def value = goodsService.checkSubmit()
        //进行自定义返回结果的逻辑判断
//        if(value.size() == 0){
//            statusMsg = "无数据"
//            statusCode = "400"
//        }else{
//            result = value
//        }
        result = value
        def responseData = ["statusCode":statusCode,"statusMsg":statusMsg,"result":result]

        render responseData as JSON
    }

    /**
     * 查询商品
     */
    def queryGoods(){
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



    def test(){
        def value = userService.test()
        println value.size()
        render "test"
    }
}
