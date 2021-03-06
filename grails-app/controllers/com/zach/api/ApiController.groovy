package com.zach.api

import grails.converters.JSON

class ApiController {

    def userService
    def goodsService
    def desUtil

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
        def mac = request.JSON.token

        def statusMsg = "操作成功"
        def statusCode = "200"
        def result  = ""    //无数据的时候就是空字符串，因为已经有一个错误码400的判断了，所以这里不需要太担心。


        def value = userService.login(username, password, mac);

        if(value.size() == 0){
            statusMsg = "登陆失败"
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
        def checkDate = request.JSON.checkDate
        def checkNumber = request.JSON.checkNumber
        def checkPeople = request.JSON.checkPeople
        def departmentNumber  = request.JSON.departmentNumber
        def data = request.JSON.data

        def all =  request.JSON


        def statusMsg = "操作成功"
        def statusCode = "200"
        def result  = ""    //无数据的时候就是空字符串，因为已经有一个错误码400的判断了，所以这里不需要太担心。

        def value = goodsService.checkSubmit(all)
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
     * 获取盘点单的参数
     */
    def getCheckSubmitParameter(){
        //获取前台传递过来的参数
//        def checkDate = params.JSON.checkDate

        def statusMsg = "操作成功"
        def statusCode = "200"
        def result  = ""    //无数据的时候就是空字符串，因为已经有一个错误码400的判断了，所以这里不需要太担心。

        def value = goodsService.getCheckSubmitParameter()
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
     * 获取收货的参数
     */
    def getReceiveParameter(){
        //获取前台传递过来的参数
//        def checkDate = params.JSON.checkDate

        def statusMsg = "操作成功"
        def statusCode = "200"
        def result  = ""    //无数据的时候就是空字符串，因为已经有一个错误码400的判断了，所以这里不需要太担心。

        def value = goodsService.getReceiveParameter()
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
    def queryGoodsByNumberOrBarcode(){
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

    /**
     * 提交商品验收 接收的参数： 日期 部门 经营方式 负责人 单据类型 供应商 折扣率 折扣额 结算金额 税额
     */
    def receiveSubmit(){
        def all = request.JSON

        def statusMsg = "操作成功"
        def statusCode = "200"

        def result  = ""    //没有数据默认返回空串
        def value =  goodsService.receiveSubmit(all)

        //中间数据处理


        result = value
        def responseData = ["statusCode":statusCode,"statusMsg":statusMsg,"result":result]

        render responseData as JSON
    }

    /**
     * 打印价签
     */
    def printPriceTag(){
        def all = request.JSON

        def statusMsg = "操作成功"
        def statusCode = "200"

        def result  = ""    //没有数据默认返回空串
        def value =  goodsService.printPriceTag(all)

        //中间数据处理


        result = value
        def responseData = ["statusCode":statusCode,"statusMsg":statusMsg,"result":result]

        render responseData as JSON
    }



    def mac(){
        def mac = params.mac
        def key = params.key

        def result = desUtil.encrypt(mac, key);

        render result
    }
}
