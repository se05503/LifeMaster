package com.example.lifemaster.presentation.total.detox.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lifemaster.R
import com.example.lifemaster.databinding.DialogDetoxTimeLockAllowServiceBinding
import com.example.lifemaster.presentation.total.detox.adapter.DetoxServiceSettingAdapter
import com.example.lifemaster.presentation.total.detox.viewmodel.DetoxTimeLockViewModel

class DetoxTimeLockAllowServiceDialog: DialogFragment(R.layout.dialog_detox_time_lock_allow_service) {

    private lateinit var binding: DialogDetoxTimeLockAllowServiceBinding
    private val timeLockViewModel: DetoxTimeLockViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogDetoxTimeLockAllowServiceBinding.bind(view)
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        binding.recyclerview.layoutManager = GridLayoutManager(context, 5)
        binding.recyclerview.adapter = DetoxServiceSettingAdapter(timeLockViewModel.allowServiceApplications)
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "DetoxRepeatLockBlockServiceDialog"
    }
}