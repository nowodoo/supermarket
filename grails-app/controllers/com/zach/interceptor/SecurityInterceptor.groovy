package com.zach.interceptor


class SecurityInterceptor {
    def securityUtilService
    SecurityInterceptor(){
        matchAll()
    }
    boolean before() {
//        def value = securityUtilService.getValue()
//        def headerValue =  request.getHeader("headerValue")
//        def result = value.contains(headerValue+'')
//        result
        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
