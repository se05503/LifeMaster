package com.example.lifemaster_xml.total.pomodoro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.lifemaster_xml.R
import com.example.lifemaster_xml.data.Datas
import com.example.lifemaster_xml.databinding.FragmentPomodoroBinding
import java.util.*
import kotlin.concurrent.timer

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PomodoroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PomodoroFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentPomodoroBinding
    private val sharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(PomodoroViewModel::class.java) // https://black-jin0427.tistory.com/389
    }
    private var totalSecond = 0

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPomodoroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    fun setupListeners() {
        binding.ivOpenTodoDialog.setOnClickListener {
            val dialog = PomodoroDialog()
            dialog.isCancelable = false
            dialog.show(activity?.supportFragmentManager!!, "PomodoroDialog")
        }
        binding.rb25Minutes.setOnClickListener {
            binding.tvPomodoroTimer.text = getString(R.string.tv_pomodoro_timer_25)
        }
        binding.rb50Minutes.setOnClickListener {
            binding.tvPomodoroTimer.text = getString(R.string.tv_pomodoro_timer_50)
        }
        binding.btnStartPomodoro.setOnClickListener {
            if(binding.tvSelectTodoItem.text == getString(R.string.tv_select_todo_item)) {
                Toast.makeText(context, "할일을 선택해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                when (binding.tvPomodoroTimer.text) {
                    getString(R.string.tv_pomodoro_timer_25) -> {
                        binding.rb25Minutes.isClickable = false
                        binding.rb50Minutes.isClickable = false // [refactor] binding 도 많이 중복되니 scope function 으로 빼서 중복 피하기
                        totalSecond = 5  // 25분 × 60초
                        // worker thread
                        timer = timer(
                            initialDelay = 0,
                            period = 1000
                        ) { // period 단위 = millisecond ( 1000 millisecond = 1 second )
                            if (totalSecond > 0) {
                                totalSecond -= 1
                                val currentHour = totalSecond / 3600
                                val currentMinute = totalSecond / 60
                                val currentSecond = totalSecond % 60
                                requireActivity().runOnUiThread {
                                    // worker thread 는 ui 에 접근할 수 없다. → Activity.runOnUiThread(Runnable) 이용
                                    binding.tvPomodoroTimer.text =
                                        String.format(
                                            "%02d:%02d:%02d",
                                            currentHour,
                                            currentMinute,
                                            currentSecond
                                        )
                                }
                            } else {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(context, "시간이 종료되었습니다!", Toast.LENGTH_SHORT)
                                        .show()
                                    binding.rb25Minutes.isChecked = false
                                    binding.rb25Minutes.isClickable = true
                                    binding.rb50Minutes.isClickable = true
                                } // [?] 왜 25분을 다시 클릭했을 때 ui 색상이 안변하지?
                                timer?.cancel()
                                timer = null
                            }
                        }
                    }
                    getString(R.string.tv_pomodoro_timer_50) -> {
                        binding.rb25Minutes.isClickable = false
                        binding.rb50Minutes.isClickable = false
                        totalSecond = 50 * 60
                        timer = timer(initialDelay = 0, period = 1000) { // worker thread
                            if (totalSecond > 0) { // [refactor] 반복되는 부분 함수로 빼기
                                totalSecond -= 1
                                val currentHour = totalSecond / 3600
                                val currentMinute = totalSecond / 60
                                val currentSecond = totalSecond % 60
                                requireActivity().runOnUiThread {
                                    binding.tvPomodoroTimer.text =
                                        String.format(
                                            "%02d:%02d:%02d",
                                            currentHour,
                                            currentMinute,
                                            currentSecond
                                        )
                                }
                            } else {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(context, "시간이 종료되었습니다!", Toast.LENGTH_SHORT)
                                        .show()
                                    binding.rb50Minutes.isChecked = false
                                    binding.rb25Minutes.isClickable = true
                                    binding.rb50Minutes.isClickable = true
                                }
                                timer?.cancel()
                                timer = null
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(context, "시간을 설정해주세요!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            }
    }

    fun observeViewModel() {
        sharedViewModel.selectedPosition.observe(viewLifecycleOwner) { selectedPosition ->
            binding.tvSelectTodoItem.text = Datas.todoItems[selectedPosition]
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PomodoroFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PomodoroFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
