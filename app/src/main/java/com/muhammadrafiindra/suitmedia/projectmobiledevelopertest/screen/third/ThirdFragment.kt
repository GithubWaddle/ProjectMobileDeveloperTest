package com.muhammadrafiindra.suitmedia.projectmobiledevelopertest.screen.third

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.muhammadrafiindra.suitmedia.projectmobiledevelopertest.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ThirdFragment : Fragment() {

    interface UserSelectionListener {
        fun onUserSelected(user: User)
    }

    private var listener: UserSelectionListener? = null

    fun setUserSelectionListener(listener: UserSelectionListener) {
        this.listener = listener
    }

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rvUsers: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var adapter: UserAdapter

    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private val perPage = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        rvUsers = view.findViewById(R.id.rvUsers)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)

        setupRecyclerView()
        setupBackPressHandler()
        loadUsers(reset = true)

        swipeRefresh.setOnRefreshListener {
            loadUsers(reset = true)
        }
        btnBack.setOnClickListener {
            closeFragment()
        }

        return view
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter { user ->
            listener?.onUserSelected(user)
        }

        rvUsers.layoutManager = LinearLayoutManager(requireContext())
        rvUsers.adapter = adapter

        rvUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val isAtBottom = (firstVisibleItemPosition + visibleItemCount) >= totalItemCount
                val shouldPaginate = !isLoading && !isLastPage && isAtBottom

                if (shouldPaginate) {
                    loadUsers()
                }
            }
        })
    }

    private fun loadUsers(reset: Boolean = false) {
        if (reset) {
            currentPage = 1
            isLastPage = false
            adapter.clearUsers()
            tvEmptyState.visibility = View.GONE
        }

        isLoading = true
        swipeRefresh.isRefreshing = true

        ApiClient.apiService.getUsers(currentPage, perPage).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                swipeRefresh.isRefreshing = false
                isLoading = false

                response.body()?.let {
                    val users = it.data
                    if (users.isEmpty() && adapter.itemCount == 0) {
                        tvEmptyState.visibility = View.VISIBLE
                    } else {
                        adapter.addUsers(users)
                        currentPage++
                        isLastPage = currentPage > it.total_pages
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                isLoading = false
                Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun closeFragment() {
        parentFragmentManager.beginTransaction()
            .remove(this@ThirdFragment)
            .commit()
        activity?.findViewById<FrameLayout>(R.id.fragmentContainer)?.visibility = View.GONE
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    closeFragment()
                }
            }
        )
    }
}