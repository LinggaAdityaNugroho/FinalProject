package com.example.finalproject.home

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.createIntent
import com.crocodic.core.extension.openActivity
import com.example.finalproject.edit.EditUserActivity
import com.example.finalproject.R
import com.example.finalproject.settings.SettingsActivity
import com.example.finalproject.activity.BaseActivity
import com.example.finalproject.constant.Const
import com.example.finalproject.databinding.ActivityHomeBinding
import com.example.finalproject.databinding.ItemAllFriendBinding
import com.example.finalproject.detail.DetailFriendActivity
import com.example.finalproject.room.UserEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {

    private var friends = ArrayList<UserEntity?>()
    private var friendsAll = ArrayList<UserEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutRes(R.layout.activity_home)

        //tollbar
        setSupportActionBar(binding.toolbarHome)

        //account saved check
        viewModel.user.observe(this) {
            Log.d("HomeActivity", "CheckAccount : $it")
            binding.user = it
        }

        //set Adapter
        Log.d("CheckFriends", "thisMyFriends : $friends")
        binding.rvRecyclerviewFriendHome.adapter =
            CoreListAdapter<ItemAllFriendBinding, UserEntity>(R.layout.item_all_friend).initItem(
                friends
            ) { position, data ->
                activityLauncher.launch(createIntent<DetailFriendActivity> {
                    putExtra(Const.DFRIEND.DFriend, data)
                }) {
                    if (it.resultCode == 4) {
                        getDataFriends()
                    }
                }
            }

        //function
        observe()
        getDataFriends()
        setSearch()

    }

    private fun setSearch() {
        binding.edtSearchFriend.imeOptions = EditorInfo.IME_ACTION_SEARCH
        binding.edtSearchFriend.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText?.isNotEmpty() == true) {

                    val filter = friendsAll.filter { it?.name?.contains("$newText", true) == true }
                    Log.d("CheckSearch", "SearchFriendCek : $filter")
                    friends.clear()
                    binding.rvRecyclerviewFriendHome.adapter?.notifyDataSetChanged()
                    friends.addAll(filter)
                    binding.rvRecyclerviewFriendHome.adapter?.notifyItemInserted(0)

                } else if (newText?.isEmpty() == true) {

                    friends.clear()
                    binding.rvRecyclerviewFriendHome.adapter?.notifyDataSetChanged()
                    friends.addAll(friendsAll)
                    binding.rvRecyclerviewFriendHome.adapter?.notifyItemInserted(0)

                }

                return false
            }

        })
    }

    private fun observe() {
        viewModel.friends.observe(this) {
            friends.clear()
            friendsAll.clear()
            binding.rvRecyclerviewFriendHome.adapter?.notifyDataSetChanged()


            friends.addAll(it)
            friendsAll.addAll(it)
            binding.rvRecyclerviewFriendHome.adapter?.notifyItemInserted(0)

        }
    }


    private fun getDataFriends() {
        viewModel.list()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.icon_account -> openActivity<EditUserActivity>()
            R.id.icon_settings -> openActivity<SettingsActivity>()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.home_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

}
