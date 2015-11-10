package com.zach.util

import grails.transaction.Transactional

@Transactional
class SecurityUtilService {
    def static value = new File("C:\\设备配置\\07用户.txt").text
    def getValue() {
        return this.value
    }
}
