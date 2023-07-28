package cn.vce.easylook.feature_music.presentation.music_search

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.core.view.isVisible
import cn.vce.easylook.MainEvent
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentMusicSearchBinding
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.Status
import com.bumptech.glide.RequestManager
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicSearchFragment : BaseVmFragment<FragmentMusicSearchBinding>() {

    private lateinit var searchView: SearchView
    private lateinit var viewModel: MusicSearchVM

    @Inject
    lateinit var glide: RequestManager


    private var page = 0

    override fun init(savedInstanceState: Bundle?) {
        binding.m = viewModel
        initView()
    }

    override fun initView() {
        setHasOptionsMenu(true)
        setupRecyclerView()
    }

    override fun initFragmentViewModel() {
        mainVM = getActivityViewModel()
        viewModel = getFragmentViewModel()
    }



    override fun getLayoutId(): Int? = R.layout.fragment_music_search

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_searchfrag, menu)
        val searchItem: MenuItem = menu.findItem(R.id.search)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView

        searchItem.expandActionView() // 将 SearchView 直接扩展展开。

        /*searchView.setIconifiedByDefault(false)
        searchView.setOnCloseListener{
            nav().navigateUp()
        }*/
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 处理搜索逻辑
                query?.apply {
                    viewModel.refreshQuery(this)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 处理搜索逻辑
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() = binding.apply {
        page.onRefresh {
            this@MusicSearchFragment.page = 0
            viewModel.onEvent(MusicSearchEvent.RefreshSearchEvent(this@MusicSearchFragment.page))
        }.onLoadMore {
            viewModel.onEvent(MusicSearchEvent.RefreshSearchEvent(++this@MusicSearchFragment.page))
        }

        searchRv.apply {
            linear().setup {
                addType<MusicInfo>(R.layout.list_music_item)
                onClick(R.id.songItemLayout) {
                    val musicInfo = getModel<MusicInfo>()
                    (searchRv.bindingAdapter.models as List<MusicInfo>)?.let {
                        mainVM.onEvent(MainEvent.ClickPlay(it, musicInfo))
                    }
                }
            }
        }
    }

}