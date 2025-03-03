package com.example.lifemaster.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lifemaster.presentation.data.SharedData
import com.example.lifemaster.databinding.FragmentHomeBinding
import com.example.lifemaster.presentation.home.todo.ToDoAdapter
import com.example.lifemaster.presentation.home.todo.ToDoDialog
import com.example.lifemaster.presentation.home.todo.ToDoDialogInterface

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), ToDoDialogInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentHomeBinding

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // [?] fragment 생명주기 공부하기. 일단 잘 몰라서 onViewCreated 에 몰빵
        // [?] requireContext vs context
        binding.recyclerview.adapter = ToDoAdapter(SharedData.todoItems, requireContext(), requireActivity()) // [?] activity vs requireActivity
        binding.btnAddTodoItem.setOnClickListener {
            // 다이얼로그 띄우기
            val dialog = ToDoDialog(this)
            dialog.isCancelable = false
            dialog.show(activity?.supportFragmentManager!!, "ToDoDialog")
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun registerToDoItem() {
        binding.recyclerview.adapter?.notifyDataSetChanged()
    }
}