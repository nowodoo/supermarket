package com.zach.goods

import com.zach.util.SqlUtilService
import grails.converters.JSON
import grails.transaction.Transactional

import java.sql.Timestamp
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
        //分店的逻辑
        if("分部" == sqlUtilService.department){
            //只要是商品的number添加了就不再添加管条形码了
            if(number){
                dbInstance.eachRow("select * from Goods where incode = '"+number+"'",{
                    def value = [:]
                    def inprc = it.inprc
                    value.goodsName = it.fname  //商品名称
                    value.incode = it.incode
                    value.barcode = it.barcode
                    value.packnum = it.packnum
                    value.specs = (it.specs==null)?"":it.specs  //规格
                    value.unit = it.unit        //单价
                    value.inprc = it.inprc      //进价
                    value.snprc = it.snprc      //售价
                    def numberRepos = dbInstance.rows("select * from ware_sr where incode='"+it.incode+"'")      //获取总的数量
                    value.suppliers = dbInstance.rows("select a.incode, a.custno, a.Iprc, a.jxqty, a.dxqty, a.lxqty, b.custname, b.shortname, b.dtype, b.addr, b.tel, b.faxnum from Ware_sr a left join Customer b on a.custno = b.custno where a.incode = '"+it.incode+"'")    //获取这个商品的供应商

                    //将库存里面的数值添加进相应的供应商里面(方法就是通过两次数组的遍历将数据添加，每一个数组代表着选数据库中选出来的行)
//                    def numberRepoSum = 0;
//                    numberRepos.each{
//                        for(int i = 0; i < value.suppliers.size(); i++){
//                            if(it.custno == value.suppliers[i].custno){
//                                value.suppliers[i].numberRepo = it.jxqty+it.dxqty+it.lxqty
//                                numberRepoSum+=value.suppliers[i].numberRepo;
//                            }
//                        }
//                    }

                    //获取所有的库存总数
                    def numberRepoSum = 0;

                    //将每一个供应商拥有的商品计算总量
                    value.suppliers.each{
                        it.numberRepo = it.jxqty+it.dxqty+it.lxqty
                        it.iprc = inprc
                        numberRepoSum += it.numberRepo;
                    }

                    //获取同一件商品的所有供应商的数量
                    value.numberRepoSum = numberRepoSum


                    goods << value
                })
            }else{
                dbInstance.eachRow("select * from Goods where barcode = '"+barcode+"'",{
                    def value = [:]
                    def inprc = it.inprc   //获取这个商品的价格
                    value.goodsName = it.fname  //商品名称
                    value.incode = it.incode
                    value.barcode = it.barcode
                    value.packnum = it.packnum
                    value.specs = (it.specs==null)?"":it.specs  //规格
                    value.unit = it.unit        //单价
                    value.inprc = it.inprc      //进价
                    value.snprc = it.snprc      //售价
                    def numberRepos = dbInstance.rows("select * from ware_sr where incode='"+it.incode+"'")      //获取总的数量
                    value.suppliers = dbInstance.rows("select a.incode, a.custno, a.Iprc, a.jxqty, a.dxqty, a.lxqty, b.custname, b.shortname, b.dtype, b.addr, b.tel, b.faxnum from Ware_sr a left join Customer b on a.custno = b.custno where a.incode = '"+it.incode+"'")    //获取这个商品的供应商

                    //将库存里面的数值添加进相应的供应商里面(方法就是通过两次数组的遍历将数据添加，每一个数组代表着选数据库中选出来的行)
                    //获取所有的库存总数
                    def numberRepoSum = 0;

                    //将每一个供应商拥有的商品计算总量
                    value.suppliers.each{
                        it.numberRepo = it.jxqty+it.dxqty+it.lxqty
                        it.iprc = inprc
                        numberRepoSum += it.numberRepo;
                    }
                    //获取同一件商品的所有供应商的数量
                    value.numberRepoSum = numberRepoSum


                    goods << value
                })
            }
        }else if("总部" == sqlUtilService.department){
            //只要是商品的number添加了就不再添加管条形码了
            if(number){
                dbInstance.eachRow("select * from Goods where incode = '"+number+"'",{
                    def value = [:]   //每一个value表示一个商品
                    def inprc = it.inprc   //获取这个商品的价格
                    value.goodsName = it.fname  //商品名称
                    value.incode = it.incode
                    value.barcode = it.barcode
                    value.packnum = it.packnum
                    value.specs = (it.specs==null)?"":it.specs  //规格
                    value.unit = it.unit        //单价
                    value.inprc = it.inprc      //进价
                    value.snprc = it.snprc      //售价
                    def numberRepos = dbInstance.rows("select * from ware_sr where incode='"+it.incode+"'")      //获取总的数量  获取ware_sr表里面的所有行
                    value.suppliers = dbInstance.rows("select a.qty, a.incode, a.custno, a.Iprc, b.custname, b.shortname, b.dtype, b.addr, b.tel, b.faxnum from Ware_sr a left join Customer b on a.custno = b.custno where a.incode = '"+it.incode+"'")    //获取这个商品的供应商

                    //获取所有的库存总数
                    def numberRepoSum = 0;
                    numberRepos.each{  //遍历这个商品的每一次库存
                        numberRepoSum+=it.qty
                    }

                    //将每一个供应商拥有的商品计算总量
                    value.suppliers.each{
                        it.numberRepo = it.qty
                        it.iprc = inprc
                    }

                    //获取同一件商品的所有供应商的数量
                    value.numberRepoSum = numberRepoSum


                    goods << value
                })
            }else{
                dbInstance.eachRow("select * from Goods where barcode = '"+barcode+"'",{
                    def value = [:]   //每一个value表示一个商品
                    def inprc = it.inprc   //获取这个商品的价格
                    value.goodsName = it.fname  //商品名称
                    value.incode = it.incode
                    value.barcode = it.barcode
                    value.packnum = it.packnum
                    value.specs = (it.specs==null)?"":it.specs  //规格
                    value.unit = it.unit        //单价
                    value.inprc = it.inprc      //进价
                    value.snprc = it.snprc      //售价
                    def numberRepos = dbInstance.rows("select * from ware_sr where incode='"+it.incode+"'")      //获取总的数量  获取ware_sr表里面的所有行
                    value.suppliers = dbInstance.rows("select a.qty, a.incode, a.custno, a.Iprc, b.custname, b.shortname, b.dtype, b.addr, b.tel, b.faxnum from Ware_sr a left join Customer b on a.custno = b.custno where a.incode = '"+it.incode+"'")    //获取这个商品的供应商

                    //获取所有的库存总数
                    def numberRepoSum = 0;
                    numberRepos.each{  //遍历这个商品的每一次库存
                        numberRepoSum+=it.qty
                    }

                    //将每一个供应商拥有的商品计算总量
                    value.suppliers.each{
                        it.numberRepo = it.qty
                        it.iprc = inprc
                    }

                    //获取同一件商品的所有供应商的数量
                    value.numberRepoSum = numberRepoSum

                    goods << value
                })
            }
        }


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

        //判断是不是存在盘点单号的字段,要是不存在，那么就建立一个，并且将初始值设为1
        def orderNoStoredExist = dbInstance.rows("select NextNo from NextFormNo where FormType = 'sppandian'")  //取出盘点单的下一个数字 标准
        if(orderNoStoredExist.size() == 0){
            dbInstance.execute("insert into NextFormNo (FormType, NextNo, Tag) values(?,?,?)", ["sppandian", 1,"*"])
        }

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
        def currentTimeString = sqlTime.format("yyyy-MM-dd")
        def currentTimeStringDetail = sqlTime.format("yyyy-MM-dd HH:mm:ss")

        //获取上传的用户信息
        def user = dbInstance.rows("select * from muser where username='"+all.checkPeople+"'")

        //获取手机端传过来的所有商品
        def goods = []
        def goods_int = 0
        all.data.each{
            //先将商品从数据库里面拿出来放到商品的表里面，然后将用户输入的信息合并到里面就好了。
            goods << dbInstance.rows("select * from goods where incode='"+it.incode+"'")[0]  //因为这里返回的每一个都是数组，所以要将第一个取出来
            goods[goods_int].total = it.total;
            goods[goods_int].amount = (goods[goods_int].total/goods[goods_int].packnum) as int;   //需要处理要是等于1
            goods[goods_int].remainder = (goods[goods_int].total % (goods[goods_int].packnum as int)) as int;
            goods[goods_int].price = it.price    //将前台传递的进价拿过来

            goods_int++;
        }

        //将所有的商品的有税的进价合计 有税的售价合计  无税的进价合计 无税的售价合计
        def iamt_hj = 0
        def ramt_hj = 0
        def wiamt_hj = 0
        def wramt_hj = 0
        def qty_hj = 0   //合计所有的数量
        goods.each{
            //将每一个商品的四个价格全部算出来(进价 售价 无税进价 无税售价)
            iamt_hj +=(it.iamt = it.total*it.inprc)  //捆 * 一捆多少个 * 一个的单价
            ramt_hj +=(it.ramt = it.total*it.snprc)
            wiamt_hj +=(it.wiamt = (it.total*it.inprc)/(1+it.taxrate/100))
            wramt_hj +=(it.wramt = (it.total*it.snprc)/(1+it.taxrate/100))
        }

        //总店和分店的不同就是在sppandian表里有一个qty_hj的字段，但是经过测试这里没有需要的文件
        //写入sppandian表
        dbInstance.execute("insert into sppandian (orderno, grpno, pddate, sdate, zdpep, xgpep, fzpep, zddate, xgdate, stat, iamt_hj, ramt_hj, wiamt_hj, wramt_hj, remark, yspzno, yspzdate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                [orderNoString, deptNumber, checkDate, checkDate, user.userscrip[0], user.userscrip[0], user.userscrip[0], checkDate, checkDate, '0', iamt_hj, ramt_hj, wiamt_hj, wramt_hj, remark, '', checkDate]);


        //将数据全部存入sppandianitem表
        goods.each {
            dbInstance.execute("insert into sppandianitem (orderno, incode, barcode, fname, specs, unit, packunit, packnum, stype, iprc, rprc, qty0, qty1, qty, iamt, ramt, wiamt, wramt, taxrate, jxtaxrate, jord, addtime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    [orderNoString, it.incode, it.barcode, it.fname, it.specs, it.unit, it.packunit, it.packnum, it.stype, it.inprc, it.snprc, it.amount, it.remainder, it.total, it.iamt, it.ramt, it.wiamt, it.wramt, it.taxrate, it.jxtaxrate, it.jord, currentTimeStringDetail])
        }

        return "success"
    }

    /**
     * 返回盘点的需要的参数 日期 部门 盘点单号码
     * @return
     */
    def getCheckSubmitParameter(){
        def value = [:]             //声明一个map
        def dbInstance = sqlUtilService.getInstance()   //获取数据库实例
        def depts =  dbInstance.rows("select * from dept where dtype='S' or dtype='C' order by code");  //取出所有的部门,标准
//        def number = dbInstance.rows("select NextNo from NextFormNo where FormType = 'sppandian'")  //取出盘点单的下一个数字 标准

        //这里需要判断是不是总部
        if(sqlUtilService.department == "总部"){
            //总部无法取得盘点号码
        }else{
            //分部的话需要将数值加1
//            dbInstance.execute("update NextFormNo set NextNo ="+(number.NextNo[0]+1)+" where FormType = 'sppandian'"); //只要的取出来了，不管你有没有使用，这里都要把数值加1 标准
        }


        //获取所有盘点日期
        def timeObject = dbInstance.rows("select pdDate from pandiandate order by pdDate desc,Sn desc")       //取出所有的盘点的日期进行选择，这里设置了排序 标准
        def time = []
        timeObject.each{
            time << it.pdDate.format("yyyy-MM-dd")
        }

        def chargePeople = dbInstance.rows("select * from muser")  //取出盘点单的下一个数字 标准
        value.depts = depts
//        value.number = number
        value.time = time
        value.chargePeople = chargePeople;
        return value
    }

    /**
     * 返回收货需要的参数 日期 部门 经营方式 负责人 单据类型 供应商(号码和名称)
     * @return
     */
    def getReceiveParameter(){
        def value = [:]             //声明一个map
        def dbInstance = sqlUtilService.getInstance()   //获取数据库实例

        //取出所有的部门,标准
        def depts =  dbInstance.rows("select * from dept where (dtype='S' or dtype='C') and code<>'Z00909'");

        //获取所有的供应商号码
        def suppliers = dbInstance.rows("select * from customer where flag='Y'")

        //这里的时间不需要取出来，用户自己输入就好了。

        //获取经营方式
        def sellWay = [[code:"J",name:"经销"],[code:"D",name:"代销"]]

        //获取负责人
        def chargePeople = dbInstance.rows("select * from muser");

        //获取单据类型
        def receiptType = [[code:"0", name:"自购验收"],[code:"1", name:"协配验收"],[code:"2", name:"直配验收"]]


        value.depts = depts
        value.suppliers = suppliers
        value.sellWay = sellWay
        value.chargePeople = chargePeople
        value.receiptType = receiptType
        return value
    }

    /**
     * 商品验收提交
     * @return
     */
    def receiveSubmit(all){
        def dbInstance = sqlUtilService.getInstance()

        //判断是不是存在单号的字段,要是不存在，那么就建立一个，并且将初始值设为1
        def orderNoStoredExist = dbInstance.rows("select NextNo from NextFormNo where FormType = 'sptoxs'")  //取出盘点单的下一个数字 标准
        if(orderNoStoredExist.size() == 0){
            dbInstance.execute("insert into NextFormNo (FormType, NextNo, Tag) values(?,?,?)", ["sptoxs", 1,"*"])
        }

        //首先获取商品验收单的最新号码
        def orderNoStored = dbInstance.rows("select * from NextFormNo where FormType='sptoxs'")  //取出盘点单的下一个数字 标准
        dbInstance.execute("update NextFormNo set NextNo ="+(orderNoStored.NextNo[0]+1)+" where FormType = 'sptoxs'"); //只要的取出来了，不管你有没有使用，这里都要把数值加1 标准
        def orderNoString = '0' + (orderNoStored.NextNo[0] + 100000000)

        //获取提交人
        def submitPeople = all.submitPeople

        //获取负责人号码
        def chargePeople = all.chargePeople


        //获取供应商的号码
        def supplierNumber = all.supplierNumber

        //获取部门号码
        def deptNumber = all.deptNumber

        //获取评论
        def remark = all.remark

        //获取盘点时间
        def checkDate = all.checkDate;

        //获取折扣率 折扣额 结算金额 税额
        def discountRate = all.discountRate
        def discountMoney = all.discountMoney
        def accountMoney = all.accountMoney

        //获取当前时间
        java.sql.Date sqlTime = new java.sql.Date(System.currentTimeMillis())
        def currentTimeString = sqlTime.format("yyyy-MM-dd")
        def currentTimeStringDetail = sqlTime.format("yyyy-MM-dd HH:mm:ss")

        //获取上传的用户信息
        def user = dbInstance.rows("select * from muser where username='"+all.submitPeople+"'")

        //获取手机端传过来的所有商品
        def goods = []
        def goods_int = 0
        all.data.each{
            //先将商品从数据库里面拿出来放到商品的表里面，然后将用户输入的信息合并到里面就好了。
            goods << dbInstance.rows("select * from goods where incode='"+it.incode+"'")[0]  //因为这里返回的每一个都是数组，所以要将第一个取出来
            goods[goods_int].total = it.total;
            goods[goods_int].amount = (goods[goods_int].total/goods[goods_int].packnum) as int;   //需要处理要是等于1
            goods[goods_int].remainder = (goods[goods_int].total % (goods[goods_int].packnum as int)) as int;
            goods[goods_int].price = it.price    //将前台传递的进价拿过来
            //用商品的总量去获取商品的总件数和零数

            goods_int++;
        }

        //将所有的商品的有税的进价合计 有税的售价合计  无税的进价合计 无税的售价合计
        def iamt_hj = 0
        def ramt_hj = 0
        def wiamt_hj = 0
        def wramt_hj = 0
        def Qty_hj = 0
        goods.each{
            //首先将商品的上次的价格保存起来
            it.lastiprc = it.inprc;
            //将每一个商品的四个价格全部算出来(进价 售价 无税进价 无税售价)
//            iamt_hj +=(it.iamt = it.total*it.price*(1+it.taxrate/100))  //捆 * 一捆多少个 * 一个的单价  bug 需要加上零数
            iamt_hj +=(it.iamt = it.total*it.price)  //捆 * 一捆多少个 * 一个的单价  bug 需要加上零数
            ramt_hj +=(it.ramt = it.total*it.snprc)
            wiamt_hj +=(it.wiamt = (it.total*it.price)/(1+it.taxrate/100))
            wramt_hj +=(it.wramt = (it.total*it.snprc)/(1+it.taxrate/100))
            Qty_hj += it.total  //所有的商品的总重量
        }

        //计算优惠额度
        def yhiamt      //总价优惠了多少钱
        def jsiamt      //优惠之后的总价
        yhiamt = iamt_hj * (all.discountRate/100)
        jsiamt = iamt_hj - yhiamt

        //由于总店和分店的表是不同的，这里需要判断之后进行，分店和总店需要分别操作
        if("分部" == sqlUtilService.department){
            //写入sptox表
            dbInstance.execute("insert into sptoxs (orderno, grpno, custno, sdate, zdpep, xgpep, fzpep, zddate, xgdate, jord, stat, iamt_hj, ramt_hj, wiamt_hj, wramt_hj, yhiamt, jsiamt, TaxTotal, Qty_hj, mxzflag, DingDanNo, remark, yspzno, yspzdate, JzDate, pType, sFlag, psOrderno) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    [orderNoString, deptNumber, supplierNumber, checkDate, user.userscrip[0], user.userscrip[0], user.userscrip[0], checkDate, checkDate, 'J', '0', iamt_hj, ramt_hj, wiamt_hj, wramt_hj, yhiamt, jsiamt, 0, Qty_hj, '', null, null, null, null, '', '0', null, null]);

            //将数据全部存入sptoxsitem表
            goods.each {
                dbInstance.execute("insert into sptoxsitem (orderno, incode, barcode, fname, specs, unit, packunit, packnum, qty0, qty1, qty, Lastiprc, iprc, rprc, iamt, ramt, wiamt, wramt, taxrate, jxtaxrate, giftqty, date1, date2, remark, addtime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        [orderNoString, it.incode, it.barcode, it.fname, it.specs, it.unit, it.packunit, it.packnum, it.amount, it.remainder, it.total, it.lastiprc, it.price, it.snprc, it.iamt, it.ramt, it.wiamt, it.wramt, it.taxrate, it.jxtaxrate, '0', null, null, null, currentTimeStringDetail])
            }
        } else if("总部" == sqlUtilService.department){
            //写入sptox表
            dbInstance.execute("insert into sptoxs (orderno, grpno, custno, sdate, zdpep, xgpep, fzpep, zddate, xgdate, jord, stat, iamt_hj, ramt_hj, wiamt_hj, wramt_hj, yhiamt, jsiamt, TaxTotal, Qty_hj, mxzflag, DingDanNo, remark, yspzno, yspzdate, JzDate, sFlag, zqday) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    [orderNoString, deptNumber, supplierNumber, checkDate, user.userscrip[0], user.userscrip[0], user.userscrip[0], checkDate, checkDate, 'J', '0', iamt_hj, ramt_hj, wiamt_hj, wramt_hj, yhiamt, jsiamt, 0, Qty_hj, '', null, null, null, null, '', null, null]);

            //将数据全部存入sptoxsitem表
            goods.each {
                dbInstance.execute("insert into sptoxsitem (orderno, incode, barcode, fname, specs, unit, packunit, packnum, qty0, qty1, qty, Lastiprc, iprc, rprc, iamt, ramt, wiamt, wramt, taxrate, jxtaxrate, giftqty, date1, date2, remark, addtime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        [orderNoString, it.incode, it.barcode, it.fname, it.specs, it.unit, it.packunit, it.packnum, it.amount, it.remainder, it.total, it.lastiprc, it.price, it.snprc, it.iamt, it.ramt, it.wiamt, it.wramt, it.taxrate, it.jxtaxrate, '0', null, null, null, currentTimeStringDetail])
            }
        }


        return "ok"
    }

    /**
     * 打印价签
     * @return
     * mName nvarchar(50) not null,	--机器名称
     * sdate datetime not null,	--采集日期
     * orderno nvarchar(100) not null,	--单号：日期+时间+机器号，格式YYYYMMDDhhmmss001，如20151031075959001
     * incode nvarchar(13) not null,	--商品编码
     * barcode nvarchar(30),		--商品条码
     * ctime datetime)			--采集时间
     */
    def printPriceTag(all){
        def dbInstance = sqlUtilService.getInstance()

        //获取当前时间
        java.sql.Date sqlTime = new java.sql.Date(System.currentTimeMillis())
        def currentTimeString = sqlTime.format("yyyy-MM-dd")
        def currentTimeStringDetail = sqlTime.format("yyyy-MM-dd HH:mm:ss")


        //遍历所有的商品 bug 时间的处理应该添加毫秒
        all.data.each{
            //获取单号：日期+时间+机器号，格式YYYYMMDDhhmmss001，如20151031075959001
            def currentTimeOrderNumber = sqlTime.format("YYYYMMddhhmmss") + it.mName+""

            //首先使用incode去查找商品要是有了就直接去忽略二维码
            def goods
            def goodsByIncode = dbInstance.rows("select * from goods where incode = '" + it.incode+"'");
            def goodsByBarcode = dbInstance.rows("select * from goods where barcode = '" + it.barcode+"'");
            if (goodsByIncode.size() > 0){
                goods = goodsByIncode[0]
            }else if(goodsByBarcode.size() > 0){
                goods = goodsByBarcode[0]
            }else{
                goods = null;
            }

            if(goods){
                //获取timestamp, 格式 2015-11-10 10:32:51.978
                Timestamp timestamp = new Timestamp(System.currentTimeMillis())
                dbInstance.execute("insert into prnlistcj(mName, sdate, orderno, incode, barcode, ctime) values (?,?,?,?,?,?)",[it.mName, currentTimeString, currentTimeOrderNumber, goods.incode, goods.barcode, timestamp.toString()]);
            }
        }



        return "ok"
    }

}
