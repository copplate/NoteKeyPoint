package com.exaiio.secondproject.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.exaiio.secondproject.R
import com.exaiio.secondproject.adapterrlv.MemoAdapter
import com.exaiio.secondproject.databinding.FragmentNoteTwoBinding
import com.exaiio.secondproject.room.entity.Memo
import kotlinx.coroutines.launch


class NoteFragmentTwo : Fragment() {
    private var _binding:FragmentNoteTwoBinding? = null
    private val binding get() = _binding!!
    // 用 viewModels 委托,Fragment 销毁时自动清理
    private val viewModel: MemoViewModel by viewModels()

    private val adapter = MemoAdapter(
        onItemClick = { memo -> showEditDialog(memo) },
        onItemLongClick = { memo -> showDeleteDialog(memo) }
    )

            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNoteTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeMemos()
        setupAddButton()



    }

    private fun setupRecyclerView() {
        binding.rlvMemo.layoutManager = LinearLayoutManager(requireContext())
        binding.rlvMemo.adapter = adapter
    }

    // 关键:收集 Flow
    private fun observeMemos() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allMemos.collect { memos ->
                    adapter.submitList(memos)
                }
            }
        }
    }

    private fun setupAddButton() {
        /*binding.fabAdd.setOnClickListener {
            showEditDialog(null)   // null 表示新建
        }*/
    }

    // 新建 / 编辑对话框
    private fun showEditDialog(memo: Memo?) {
        val container = requireContext().let {
            android.widget.LinearLayout(it).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(48, 24, 48, 0)
            }
        }
        val etTitle = EditText(requireContext()).apply {
            hint = "标题"
            setText(memo?.title ?: "")
        }
        val etContent = EditText(requireContext()).apply {
            hint = "内容"
            setText(memo?.content ?: "")
        }
        container.addView(etTitle)
        container.addView(etContent)

        AlertDialog.Builder(requireContext())
            .setTitle(if (memo == null) "新建备忘录" else "编辑备忘录")
            .setView(container)
            .setPositiveButton("保存") { _, _ ->
                val title = etTitle.text.toString().trim()
                val content = etContent.text.toString().trim()
                if (title.isEmpty()) return@setPositiveButton

                if (memo == null) {
                    viewModel.addMemo(title, content)
                } else {
                    viewModel.updateMemo(memo, title, content)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDeleteDialog(memo: Memo) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除")
            .setMessage("确定删除「${memo.title}」吗?")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteMemo(memo)   // 或 viewModel.deleteMemoById(memo.id)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rlvMemo.adapter = null   // 防内存泄漏
        _binding = null
    }

    companion object {

    }
}