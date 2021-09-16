package com.example.onlineTest

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineTest.databinding.ActivityMainBinding
import com.kaopiz.kprogresshud.KProgressHUD


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var userAdapter: UserAdapter
    lateinit var mLayoutManager: LinearLayoutManager
    var loading: Boolean = true
    var users = ArrayList<UserModel>()
    var hud: KProgressHUD? = null
    var currentPage: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSearchLayout()
    }

    private fun setupSearchLayout() {
        binding.searchLayout.visibility = View.VISIBLE
        binding.resultLayout.visibility = View.GONE
        binding.backButton.visibility = View.GONE
        binding.submitButton.setOnClickListener(this)
        binding.loginEditText.imeOptions = EditorInfo.IME_ACTION_DONE
    }

    private fun setupResultLayout() {
        binding.searchLayout.visibility = View.GONE
        binding.resultLayout.visibility = View.VISIBLE
        binding.backButton.visibility = View.VISIBLE
        binding.backButton.setOnClickListener(this)

        userAdapter = UserAdapter(users, this)
        mLayoutManager = object : LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically(): Boolean = true
            override fun canScrollHorizontally(): Boolean = false
            override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
                return super.scrollVerticallyBy(dy, recycler, state)
            }
        }
        with(binding.loginListRecyclerView) {
            adapter = userAdapter
            layoutManager = mLayoutManager
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        if (loading && mLayoutManager.childCount + mLayoutManager.findFirstVisibleItemPosition() >= mLayoutManager.itemCount) {
                            loading = false
                            currentPage++
                            searchLogin()
                        }
                    }
                }
            })
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            binding.submitButton.id -> {
                if (validateLogin()) {
                    searchLogin()
                } else {
                    showAlert("Please input login.")
                }
            }
            binding.backButton.id -> {
                users.clear()
                binding.loginEditText.text.clear()
                setupSearchLayout()
            }
            else -> print("Unknown Action")
        }
    }

    private fun validateLogin(): Boolean {
        return binding.loginEditText.text.isNotEmpty()
    }

    private fun showAlert(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    /*
    Fetch User Data
    */
    private fun searchLogin() {
        showLoading(true)
        val query = binding.loginEditText.text.toString()
        ApiManager.shared.fetchAllUsers(query, 9, currentPage) { isSuccess, data ->
            showLoading(false)
            if (isSuccess) {
                users.addAll(data.sortedWith(compareBy { it.userName }))
                runOnUiThread {
                    if (binding.resultLayout.visibility == View.GONE) {
                        setupResultLayout()
                    } else {
                        userAdapter.notifyDataSetChanged()
                        binding.loginListRecyclerView.clearFocus()
                        loading = true
                    }
                }
            } else {
                runOnUiThread {
                    showAlert("Encountered error during fetch data. Please try again")
                    currentPage--
                    loading = true
                }
            }
        }
    }

    /*
    KProgressHUD
    */
    private fun showLoading(show: Boolean) {
        runOnUiThread {
            if (show) {
                showHUD("Loading...")
            } else {
                hideHUD()
            }
        }
    }

    private fun showHUD(label: String?) {
        if (hud != null) hideHUD()
        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel(label)
            .setCancellable(false)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
            .show()
    }

    private fun hideHUD() {
        if (hud != null) hud!!.dismiss()
        hud = null
    }
}
