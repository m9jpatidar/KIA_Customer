package com.knowitall.customer.data.model

import java.util.HashMap

class UserLoginLog {

    var id: String? = null
    var deviceType: String? = null
    var timestamp: Long? = null

    constructor() {}

    constructor(id: String, deviceType: String, timestamp: Long) {
        this.id = id
        this.deviceType = deviceType
        this.timestamp = timestamp
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result["deviceType"] = deviceType!!
        result["timestamp"] = timestamp!!
        return result
    }
}