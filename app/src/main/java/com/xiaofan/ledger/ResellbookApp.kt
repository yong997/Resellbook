package com.xiaofan.ledger

import android.app.Application
import com.xiaofan.ledger.data.AppDatabase
import com.xiaofan.ledger.data.OrderRepository

class ResellbookApp : Application() {

    /** 极简手动依赖注入：全局唯一的 Repository。 */
    val repository: OrderRepository by lazy {
        OrderRepository(AppDatabase.get(this).orderDao())
    }
}
