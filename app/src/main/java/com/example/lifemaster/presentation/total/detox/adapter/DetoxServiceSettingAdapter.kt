package com.example.lifemaster.presentation.total.detox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lifemaster.databinding.ItemDetoxTargetAppSettingBinding
import com.example.lifemaster.presentation.total.detox.model.DetoxTargetApp

// 반복 잠금과 시간 잠금 둘 다 공유 가능한 어댑터
class DetoxServiceSettingAdapter(
    private val items: ArrayList<DetoxTargetApp>
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
                items[position] = updateItem // 실제 뷰모델의 데이터가 변경된다
                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetoxAllowServiceViewHolder {
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
        holder: DetoxAllowServiceViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }
}