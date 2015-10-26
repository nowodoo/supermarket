package com.zach.goods

import com.zach.util.SqlUtilService
import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class GoodsService {
    def sqlUtilService

    def saveGoods() {
        return "save"
    }

    def deleteGoods() {
        return "delete"
    }

    def queryGoods() {
        def goods = []
        def dbInstance = sqlUtilService.getInstance();
        dbInstance.eachRow("select * from Goods",{
            goods << it.fname
        })
        return goods
    }

    def queryGoodsByNumberOrBarcode(number, barcode) {
        def goods = []

        def dbInstance = sqlUtilService.getInstance();
        if(number){
            dbInstance.eachRow("select * from Goods where incode = '"+number+"'",{
                def value = [:]
                value.goodsName = it.fname  //商品名称
                value.incode = it.incode
                value.barcode = it.barcode
                value.specs = (it.specs==null)?"":it.specs //规格
                value.unit = it.unit        //单价
                value.inprc = it.inprc  //进价
                value.snprc = it.snprc  //售价
                value.supplier = dbInstance.rows("select * from Ware_sr where incode = '"+it.incode+"'")    //获取这个商品的供应商
                goods << value
            })
        }else{
            dbInstance.eachRow("select * from Goods where barcode = '"+barcode+"'",{
                def value = [:]
                value.goodsName = it.fname  //商品名称
                value.barcode = it.barcode
                value.specs = (it.specs==null)?"":it.specs  //规格
                value.unit = it.unit //单价
                value.inprc = it.inprc   //进价
                value.snprc = it.snprc      //售价
                value.supplier = dbInstance.rows("select * from Ware_sr where incode = '"+it.incode+"'")    //获取这个商品的供应商
                goods << value
            })
        }

        return goods
        return goods

    }

    def modifyGoods() {
        return "modify"
    }

    /**
     * 获取商品盘点日期
     * @return
     */
    def getCheckDate() {
        def dbInstance = sqlUtilService.getInstance()
        def value = dbInstance.rows("select pdDate from PanDianDate order by pdDate desc,Sn desc")
        return value
    }

    /**
     *  获取盘点的新的单号
     * @return
     */
    def getCheckNextNo(){
        def dbInstance = sqlUtilService.getInstance()
        def value = dbInstance.rows("select NextNo from NextFormNo where FormType = 'sppandian'")
        return value
    }

    /**
     *  获取收货的新的单号
     * @return
     */
    def getReceiveNextNo(){
        def dbInstance = sqlUtilService.getInstance()
        def value = dbInstance.rows("select NextNo from NextFormNo where FormType = 'sptoxs'")
        return value
    }

    /**
     * 盘点单进行提交
     */
    def checkSubmit(){
        def dbInstance = sqlUtilService.getInstance()
        return "checkSubmit"
    }

}
