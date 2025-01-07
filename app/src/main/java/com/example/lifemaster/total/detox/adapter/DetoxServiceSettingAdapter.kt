package com.example.lifemaster.total.detox.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lifemaster.databinding.ItemDetoxTargetAppSettingBinding
import com.example.lifemaster.total.detox.model.DetoxTargetApp
import com.example.lifemaster.total.detox.viewmodel.DetoxRepeatLockViewModel
import com.example.lifemaster.total.detox.viewmodel.DetoxTimeLockViewModel

// 반복 잠금과 시간 잠금 둘 다 공유 가능한 어댑터
class DetoxServiceSettingAdapter(
    private val items: ArrayList<DetoxTargetApp>,
    private val repeatLockViewModel: DetoxRepeatLockViewModel,
    private val timeLockViewModel: DetoxTimeLockViewModel
) : RecyclerView.Adapter<DetoxServiceSettingAdapter.DetoxAllowServiceViewHolder>() {
    inner class DetoxAllowServiceViewHolder(private val binding: ItemDetoxTargetAppSettingBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DetoxTargetApp) {
            binding.ivAppLogo.setImageDrawable(item.appIcon)
            binding.ivAppLogo.alpha = if (item.isClicked) 1.0f else 0.4f
        }

        init {
            binding.ivAppLogo.setOnClickListener {
                val position = adapterPosition
                val item = items[position]
                val updateItem = item.copy(isClicked = !item.isClicked)

                items[position] = updateItem // 문제 추정 코드. 실제 뷰모델의 데이터가 변경된다.
                Log.d("tt blockServiceApplications", ""+ repeatLockViewModel.blockServiceApplications[position].isClicked)
                Log.d("tt repeatLockTargetApplications", ""+ repeatLockViewModel.repeatLockTargetApplications[position].isClicked)
                Log.d("tt allowServiceApplications", ""+ timeLockViewModel.allowServiceApplications[position].isClicked)

                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetoxServiceSettingAdapter.DetoxAllowServiceViewHolder {
        return DetoxAllowServiceViewHolder(
            ItemDetoxTargetAppSettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(
        holder: DetoxServiceSettingAdapter.DetoxAllowServiceViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }
}