package com.example.lifemaster.presentation.home.todo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.lifemaster.R
import com.example.lifemaster.databinding.FragmentHomeBinding
import com.example.lifemaster.model.PomodoroItem
import com.example.lifemaster.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ToDoFragment : Fragment(R.layout.fragment_home) {

    lateinit var binding: FragmentHomeBinding
    private val toDoViewModel: ToDoViewModel by activityViewModels()
    private var userToken: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        initViews()
        initListeners()
        initObservers()
    }

    private fun initViews() = with(binding) {
        val sharedPreference =
            requireContext().getSharedPreferences("USER_TABLE", Context.MODE_PRIVATE)
        userToken = sharedPreference.getString("token", "null")

        recyclerview.adapter =
            ToDoAdapter(requireContext(), toDoViewModel, childFragmentManager, userToken)

        RetrofitInstance.networkService.getTodoItems(token = "Bearer $userToken")
            .enqueue(object : Callback<List<TodoItem>> {
                override fun onResponse(
                    call: Call<List<TodoItem>>,
                    response: Response<List<TodoItem>>
                ) {
                    if (response.isSuccessful) {
                        val todoItems = response.body() as ArrayList<TodoItem>
                        RetrofitInstance.networkService.getPomodoroItems(token = "Bearer $userToken")
                            .enqueue(object : Callback<List<PomodoroItem>> {
                                override fun onResponse(
                                    call: Call<List<PomodoroItem>?>,
                                    response: Response<List<PomodoroItem>?>
                                ) {
                                    if (response.isSuccessful) {
                                        val response = response.body()
                                        val filterData1 = response?.groupBy { it.taskName }
                                        val filterData2 = filterData1?.mapValues { (_, list) ->
                                            val pomodoro25 =
                                                list.count { it.focusTime == 20 } // 25분
                                            val pomodoro50 =
                                                list.count { it.focusTime == 40 } // 50분
                                            Pair(pomodoro25, pomodoro50)
                                        }
                                        Log.d("ttest1", ""+todoItems)
                                        Log.d("ttest2", "" + filterData2)
                                        todoItems.forEach { todoItem ->
                                            val pair = filterData2?.get(todoItem.title)
                                            if(pair != null) {
                                                todoItem.timer25Number = pair.first
                                                todoItem.timer50Number = pair.second
                                            }
                                        }
                                        Log.d("ttest3", ""+todoItems)
                                        toDoViewModel.getTodoItems(todoItems)
                                    }

                                }

                                override fun onFailure(
                                    call: Call<List<PomodoroItem>?>,
                                    t: Throwable
                                ) {
                                    TODO("Not yet implemented")
                                }

                            })
                        toDoViewModel.getTodoItems(todoItems)
                    } else {
                        Log.d("server success", "else")
                    }
                }

                override fun onFailure(call: Call<List<TodoItem>>, t: Throwable) {
                    Log.d("server error", "" + t.message)
                }
            })
    }

    private fun initListeners() {
        binding.btnAddTodoItem.setOnClickListener {
            val dialog = ToDoDialog(caller = TODO.ADD, userToken = userToken)
            dialog.show(childFragmentManager, ToDoDialog.TAG)
        }
    }

    private fun initObservers() {
        toDoViewModel.todoItems.observe(viewLifecycleOwner) { updateItems ->
            updateItems?.let { (binding.recyclerview.adapter as ToDoAdapter).submitList(it.toList()) }
        }
    }
}