package com.exaiio.secondproject.fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exaiio.secondproject.room.AppDatabase
import com.exaiio.secondproject.room.entity.Memo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MemoViewModel(application: Application) : AndroidViewModel(application) {

    private val memoDao = AppDatabase.getDatabase(application).memoDao()

    // 暴露给 UI 的备忘录列表数据流
    val allMemos: Flow<List<Memo>> = memoDao.getAllMemos()

    // 【增加】添加一条备忘录
    fun addMemo(title: String, content: String) {
        viewModelScope.launch {
            val newMemo = Memo(title = title, content = content)
            memoDao.insertMemo(newMemo)
        }
    }

    // 【删除】删除指定的备忘录对象
    fun deleteMemo(memo: Memo) {
        viewModelScope.launch {
            memoDao.deleteMemo(memo)
        }
    }

    // 【删除】更安全的做法：通过 id 删除
    fun deleteMemoById(memoId: Long) {
        viewModelScope.launch {
            memoDao.deleteMemoById(memoId)
        }
    }
}