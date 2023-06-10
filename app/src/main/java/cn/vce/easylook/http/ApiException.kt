package cn.vce.easylook.http

class ApiException(val errorMessage: String, val errorCode: Int) :
    Throwable()
