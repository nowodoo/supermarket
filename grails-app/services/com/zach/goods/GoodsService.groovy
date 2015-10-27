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
        //只要是商品的number添加了就不再添加管条形码了
        if(number){
            dbInstance.eachRow("select * from Goods where incode = '"+number+"'",{
                def value = [:]
                value.goodsName = it.fname  //商品名称
                value.incode = it.incode
                value.barcode = it.barcode
                value.specs = (it.specs==null)?"":it.specs  //规格
                value.unit = it.unit        //单价
                value.inprc = it.inprc      //进价
                value.snprc = it.snprc      //售价
                value.suppliers = dbInstance.rows("select a.incode, a.custno, a.Iprc, b.custname, b.shortname, b.dtype, b.addr, b.tel, b.faxnum from Ware_sr a left join Customer b on a.custno = b.custno where a.incode = '"+it.incode+"'")    //获取这个商品的供应商
                goods << value
            })
        }else{
            dbInstance.eachRow("select * from Goods where barcode = '"+barcode+"'",{
                def value = [:]
                value.goodsName = it.fname      //商品名称
                value.barcode = it.barcode
                value.specs = (it.specs==null)?"":it.specs  //规格
                value.unit = it.unit        //单价
                value.inprc = it.inprc      //进价
                value.snprc = it.snprc      //售价
                value.suppliers = dbInstance.rows("select * from Customer where custno in (select custno from Ware_sr where incode = '"+it.incode+"')")    //获取这个商品的供应商
//                value.suppliers = dbInstance.rows("select * from Customer where custno = '10001'")    //获取这个商品的供应商
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
