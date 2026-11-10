package com.xiaofan.ledger.data

import kotlinx.coroutines.flow.Flow

class OrderRepository(private val dao: OrderDao) {

    fun observeOrders(): Flow<List<OrderEntity>> = dao.observeAll()

    suspend fun get(id: Long): OrderEntity? = dao.getById(id)

    suspend fun add(order: OrderEntity): Long = dao.insert(order)

    suspend fun update(order: OrderEntity) = dao.update(order)

    suspend fun delete(id: Long) = dao.deleteById(id)

    /** 未结 <-> 已结 切换 */
    suspend fun toggleStatus(id: Long, currentStatus: Int) {
        val next = if (currentStatus == OrderEntity.STATUS_SETTLED) {
            OrderEntity.STATUS_UNSETTLED
        } else {
            OrderEntity.STATUS_SETTLED
        }
        dao.updateStatus(id, next)
    }
}
