package com.exaiio.secondproject.room.dao

import androidx.room.*
import com.exaiio.secondproject.room.entity.Memo
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {

    // 查询所有备忘录，返回 Flow 以便 UI 能响应数据变化
    @Query("SELECT * FROM memos ORDER BY id DESC")
    fun getAllMemos(): Flow<List<Memo>>

    // 插入一条备忘录。返回新插入行的 id
    @Insert
    suspend fun insertMemo(memo: Memo): Long

    // 更新指定的备忘录
    @Update
    suspend fun updateMemo(memo: Memo)

    // 删除指定的备忘录
    @Delete
    suspend fun deleteMemo(memo: Memo)

    // 或者，你也可以通过 id 来删除（更常用）
    @Query("DELETE FROM memos WHERE id = :memoId")
    suspend fun deleteMemoById(memoId: Long)
}
