package com.example.lifemaster_xml.total.detox.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.lifemaster_xml.R
import com.example.lifemaster_xml.databinding.FragmentDetoxBinding
import com.example.lifemaster_xml.total.detox.adapter.DetoxTimeLockAdapter
import com.example.lifemaster_xml.total.detox.dialog.DetoxRepeatLockDialog
import com.example.lifemaster_xml.total.detox.dialog.DetoxTimeLockDialog
import com.example.lifemaster_xml.total.detox.model.DetoxTimeLockItem

class DetoxFragment : Fragment(R.layout.fragment_detox) {

    lateinit var binding: FragmentDetoxBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetoxBinding.bind(view)
        setupViews()
        setupListeners()
    }

    private fun setupViews() {

        // 라디오 버튼 관련 ui 뷰
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.rb_repeat_lock -> {
                    binding.llRepeatLock.visibility = View.VISIBLE
                    binding.llTimeLock.visibility = View.GONE
                }
                R.id.rb_time_lock -> {
                    binding.llTimeLock.visibility = View.VISIBLE
                    binding.llRepeatLock.visibility = View.GONE
                }
            }
        }

        // 시간 잠금의 리스트 관련 뷰
        val dummyData = arrayListOf(
            DetoxTimeLockItem(0, "매주", "월요일", "01:00PM - 04:00PM"),
            DetoxTimeLockItem(1, "매일", "화요일", "04:00PM - 05:00PM"),
            DetoxTimeLockItem(2, "매일", "금요일", "02:00PM - 08:00PM"),
            DetoxTimeLockItem(3, "매주", "수요일", "09:00PM - 10:00PM"),
            DetoxTimeLockItem(4, "격주", "토요일", "03:00PM - 04:00PM"),
            DetoxTimeLockItem(5, "격주", "토요일", "03:00PM - 04:00PM"),
            DetoxTimeLockItem(6, "격주", "토요일", "03:00PM - 04:00PM"),
            DetoxTimeLockItem(7, "격주", "토요일", "03:00PM - 04:00PM"),
            DetoxTimeLockItem(8, "격주", "토요일", "03:00PM - 04:00PM")
        )
        val detoxTimeLockAdapter = DetoxTimeLockAdapter()
        binding.recyclerview.adapter = detoxTimeLockAdapter
        detoxTimeLockAdapter.submitList(dummyData)
    }

    private fun setupListeners() {
        // 반복 잠금
        binding.btnAddRepeatLockApp.setOnClickListener {
            val dialog = DetoxRepeatLockDialog()
            dialog.isCancelable = false
            dialog.show(childFragmentManager, DetoxRepeatLockDialog.TAG)
        }

        // 시간 잠금
        binding.btnTimeLockSetting.setOnClickListener {
            val dialog = DetoxTimeLockDialog()
            dialog.isCancelable = false
            dialog.show(childFragmentManager, DetoxTimeLockDialog.TAG)
        }
    }
}