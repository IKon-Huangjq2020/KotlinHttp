package com.hjq.kotlinhttp.net

import java.io.IOException

class HttpServerException(val httpCode: Int, var errorMsg: String) : IOException("$errorMsg")