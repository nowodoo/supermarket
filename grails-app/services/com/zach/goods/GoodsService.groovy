package com.zach.goods

import com.zach.util.SqlUtilService
import grails.converters.JSON
import grails.transaction.Transactional

import java.text.SimpleDateFormat

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
                value.packnum = it.packnum
                value.specs = (it.specs==null)?"":it.specs  //规格
                value.unit = it.unit        //单价
                value.inprc = it.inprc      //进价
                value.snprc = it.snprc      //售价
                def numberRepos = dbInstance.rows("select * from ware_sr where incode='"+number+"'")      //获取总的数量
                value.suppliers = dbInstance.rows("select a.incode, a.custno, a.Iprc, b.custname, b.shortname, b.dtype, b.addr, b.tel, b.faxnum from Ware_sr a left join Customer b on a.custno = b.custno where a.incode = '"+it.incode+"'")    //获取这个商品的供应商

                //将库存里面的数值添加进相应的供应商里面(方法就是通过两次数组的遍历将数据添加，每一个数组代表着选数据库中选出来的行)
                def numberRepoSum = 0;
                numberRepos.each{
                    for(int i = 0; i < value.suppliers.size(); i++){
                        if(it.custno == value.suppliers[i].custno){
                            value.suppliers[i].numberRepo = it.jxqty+it.dxqty+it.lxqty
                            numberRepoSum+=value.suppliers[i].numberRepo;
                        }
                    }
                }
                //获取同一件商品的所有供应商的数量
                value.numberRepoSum = numberRepoSum


                goods << value
            })
        }else{
            dbInstance.eachRow("select * from Goods where barcode = '"+barcode+"'",{
                def value = [:]
                value.goodsName = it.fname  //商品名称
                value.incode = it.incode
                value.barcode = it.barcode
                value.packnum = it.packnum
                value.specs = (it.specs==null)?"":it.specs  //规格
                value.unit = it.unit        //单价
                value.inprc = it.inprc      //进价
                value.snprc = it.snprc      //售价
                def numberRepos = dbInstance.rows("select * from ware_sr where incode='"+number+"'")      //获取总的数量
                value.suppliers = dbInstance.rows("select a.incode, a.custno, a.Iprc, b.custname, b.shortname, b.dtype, b.addr, b.tel, b.faxnum from Ware_sr a left join Customer b on a.custno = b.custno where a.incode = '"+it.incode+"'")    //获取这个商品的供应商

                //将库存里面的数值添加进相应的供应商里面(方法就是通过两次数组的遍历将数据添加，每一个数组代表着选数据库中选出来的行)
                def numberRepoSum = 0;
                numberRepos.each{
                    for(int i = 0; i < value.suppliers.size(); i++){
                        if(it.custno == value.suppliers[i].custno){
                            value.suppliers[i].numberRepo = it.jxqty+it.dxqty+it.lxqty
                            numberRepoSum+=value.suppliers[i].numberRepo;
                        }
                    }
                }
                //获取同一件商品的所有供应商的数量
                value.numberRepoSum = numberRepoSum


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
     * bug 所有的人都是 超级用户
     */
    def getReceiveNextNo(){
        def dbInstance = sqlUtilService.getInstance()
        def value = dbInstance.rows("select NextNo from NextFormNo where FormType = 'sptoxs'")
        return value
    }

    /**
     * 盘点单进行提交
     */
    def checkSubmit(all){
        def dbInstance = sqlUtilService.getInstance()

        //首先获取盘点订单的最新号码
        def orderNoStored = dbInstance.rows("select NextNo from NextFormNo where FormType = 'sppandian'")  //取出盘点单的下一个数字 标准
        dbInstance.execute("update NextFormNo set NextNo ="+(orderNoStored.NextNo[0]+1)+" where FormType = 'sppandian'"); //只要的取出来了，不管你有没有使用，这里都要把数值加1 标准
        def orderNoString = '0' + (orderNoStored.NextNo[0] + 100000000)

        //获取负责人
        def chargePeople = all.chargePeople

        //获取部门号码
        def deptNumber = all.deptNumber

        //获取评论
        def remark = all.remark

        //获取盘点时间
        def checkDate = all.checkDate;

        //获取当前时间
        java.sql.Date sqlTime = new java.sql.Date(System.currentTimeMillis())
        def currentTimeString = sqlTime.format("yyyy-mm-dd")
        def currentTimeStringDetail = sqlTime.format("yyyy-MM-dd HH:mm:ss")

        //获取上传的用户信息
        def user = dbInstance.rows("select * from muser where username='"+all.checkPeople+"'")

        //获取手机端传过来的所有商品
        def goods = []
        def goods_int = 0
        all.data.each{
            //先将商品从数据库里面拿出来放到商品的表里面，然后将用户输入的信息合并到里面就好了。
            goods << dbInstance.rows("select * from goods where incode='"+it.incode+"'")[0]  //因为这里返回的每一个都是数组，所以要将第一个取出来
            goods[goods_int].amount = it.amount;
            goods[goods_int].remainder = it.remainder;
            goods[goods_int].total = goods[goods_int].packnum*it.amount+it.remainder;
            goods_int++;
        }

        //将所有的商品的有税的进价合计 有税的售价合计  无税的进价合计 无税的售价合计
        def iamt_hj = 0
        def ramt_hj = 0
        def wiamt_hj = 0
        def wramt_hj = 0
        goods.each{
            //将每一个商品的四个价格全部算出来(进价 售价 无税进价 无税售价)
            iamt_hj +=(it.iamt = it.amount*it.packnum*it.inprc*(1+it.taxrate/100))  //捆 * 一捆多少个 * 一个的单价
            ramt_hj +=(it.ramt = it.amount*it.packnum*it.snprc*(1+it.taxrate/100))
            wiamt_hj +=(it.wiamt = it.amount*it.packnum*it.inprc)
            wramt_hj +=(it.wramt = it.amount*it.packnum*it.snprc)
        }
        println goods

        //写入sppandian表
        dbInstance.execute("insert into sppandian (orderno, grpno, pddate, sdate, zdpep, xgpep, fzpep, zddate, xgdate, stat, iamt_hj, ramt_hj, wiamt_hj, wramt_hj, remark, yspzno, yspzdate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
        [orderNoString, deptNumber, checkDate, checkDate, user.userscrip[0], user.userscrip[0], user.userscrip[0], checkDate, checkDate, '1', iamt_hj, ramt_hj, wiamt_hj, wramt_hj, remark, '', checkDate]);

        //将数据全部存入sppandianitem表
        goods.each {
            dbInstance.execute("insert into sppandianitem (orderno, incode, barcode, fname, specs, unit, packunit, packnum, stype, iprc, rprc, qty0, qty1, qty, iamt, ramt, wiamt, wramt, taxrate, jxtaxrate, jord, addtime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    [orderNoString, it.incode, it.barcode, it.fname, it.specs, it.unit, it.packunit, it.packnum, it.stype, it.inprc, it.snprc, it.amount, it.remainder, it.total, it.iamt, it.ramt, it.wiamt, it.wramt, it.taxrate, it.jxtaxrate, it.jord, currentTimeStringDetail])
        }

        return "checkSubmit"
    }

    /**
     * 返回盘点的需要的参数 日期 部门 盘点单号码
     * @return
     */
    def getCheckSubmitParameter(){
        def value = [:]             //声明一个map
        def dbInstance = sqlUtilService.getInstance()   //获取数据库实例
        def depts =  dbInstance.rows("select * from dept where dtype='S' or dtype='C' order by code");  //取出所有的部门,标准
        def number = dbInstance.rows("select NextNo from NextFormNo where FormType = 'sppandian'")  //取出盘点单的下一个数字 标准
        dbInstance.execute("update NextFormNo set NextNo ="+(number.NextNo[0]+1)+" where FormType = 'sppandian'"); //只要的取出来了，不管你有没有使用，这里都要把数值加1 标准

        //获取所有盘点日期
        def timeObject = dbInstance.rows("select pdDate from pandiandate order by pdDate desc,Sn desc")       //取出所有的盘点的日期进行选择，这里设置了排序 标准
        def time = []
        timeObject.each{
            time << it.pdDate.format("yyyy-MM-dd")
        }

        def chargePeople = dbInstance.rows("select * from muser")  //取出盘点单的下一个数字 标准
        value.depts = depts
        value.number = number
        value.time = time
        value.chargePeople = chargePeople;
        return value
    }

}