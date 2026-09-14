package com.exaiio.secondproject.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.exaiio.secondproject.R
import com.exaiio.secondproject.databinding.FragmentNoteOneBinding
import com.exaiio.secondproject.room.entity.Memo
import kotlinx.coroutines.launch

/**
 * Shows the memo editor and the complete memo list.
 */
class NoteFragmentOne : Fragment() {
    private var _binding: FragmentNoteOneBinding? = null
    private val binding get() = _binding!!

    private val memoViewModel: MemoViewModel by viewModels()
    private var editingMemo: Memo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSaveMemo.setOnClickListener {
            saveMemo()
        }
        binding.buttonClearMemo.setOnClickListener {
            clearEditor()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                memoViewModel.allMemos.collect { memos ->
                    renderMemos(memos)
                }
            }
        }
    }

    private fun saveMemo() {
        val title = binding.editMemoTitle.text.toString().trim()
        val content = binding.editMemoContent.text.toString().trim()

        if (title.isBlank() && content.isBlank()) {
            Toast.makeText(requireContext(), R.string.memo_input_required, Toast.LENGTH_SHORT).show()
            return
        }

        val memo = editingMemo
        if (memo == null) {
            memoViewModel.addMemo(title, content)
        } else {
            memoViewModel.updateMemo(memo, title, content)
        }
        clearEditor()
    }

    private fun renderMemos(memos: List<Memo>) {
        binding.layoutMemoList.removeAllViews()

        if (memos.isEmpty()) {
            val emptyView = TextView(requireContext()).apply {
                text = getString(R.string.memo_empty)
                setPadding(0, dp(16), 0, dp(16))
            }
            binding.layoutMemoList.addView(emptyView)
            return
        }

        memos.forEach { memo ->
            binding.layoutMemoList.addView(createMemoRow(memo))
        }
    }

    private fun createMemoRow(memo: Memo): View {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(12), 0, dp(12))

            addView(TextView(context).apply {
                text = memo.title.ifBlank { getString(R.string.memo_title_hint) }
                textSize = 18f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            })

            addView(TextView(context).apply {
                text = memo.content
                setPadding(0, dp(6), 0, dp(8))
            })

            addView(LinearLayout(context).apply {
                gravity = android.view.Gravity.END
                orientation = LinearLayout.HORIZONTAL

                addView(Button(context).apply {
                    text = getString(R.string.memo_edit)
                    setOnClickListener {
                        startEditing(memo)
                    }
                })

                addView(Button(context).apply {
                    text = getString(R.string.memo_delete)
                    setOnClickListener {
                        memoViewModel.deleteMemo(memo)
                        if (editingMemo?.id == memo.id) {
                            clearEditor()
                        }
                    }
                })
            })
        }
    }

    private fun startEditing(memo: Memo) {
        editingMemo = memo
        binding.editMemoTitle.setText(memo.title)
        binding.editMemoContent.setText(memo.content)
        binding.buttonSaveMemo.setText(R.string.memo_update)
    }

    private fun clearEditor() {
        editingMemo = null
        binding.editMemoTitle.text?.clear()
        binding.editMemoContent.text?.clear()
        binding.buttonSaveMemo.setText(R.string.memo_add)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
