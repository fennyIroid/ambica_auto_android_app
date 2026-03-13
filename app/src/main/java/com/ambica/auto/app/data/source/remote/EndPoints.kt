package com.ambica.auto.app.data.source.remote

import com.ambica.auto.app.BuildConfig

object EndPoints {
    object URLs {
        const val BASE_URL: String = BuildConfig.BASE_URL
    }

    object Auth {
        const val LOGIN = "api/auth/login/"
        const val PROFILE = "api/auth/profile/"
        const val LOGOUT = "api/auth/logout/"
        const val CHANGE_PASSWORD = "api/auth/change-password/"
        const val FORGOT_PASSWORD = "api/auth/forgot-password/"
        const val RESET_PASSWORD = "api/auth/reset-password/"
        const val SET_PASSWORD = "api/auth/set-password/"
        const val VERIFY_OTP = "api/auth/verify-otp/"
    }

    object Branches {
        const val LIST = "api/branches/"
        const val CREATE = "api/branches/"
        const val UPDATE = "api/branches/update/"
        const val DELETE = "api/branches/delete/"
        const val DETAIL = "api/branches/detail/"
    }

    object DocumentChecklists {
        const val LIST = "api/document-checklists/"
    }

    object Misc {
        const val PING = "api/ping"
    }
}
