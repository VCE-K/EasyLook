package cn.vce.easylook.feature_music.presentation.music_search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.FragmentNavigatorExtras
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseVmFragment
import cn.vce.easylook.databinding.FragmentMusicSearchBinding
import cn.vce.easylook.databinding.ListItemBinding
import cn.vce.easylook.feature_music.models.MusicInfo
import cn.vce.easylook.feature_music.other.Status
import cn.vce.easylook.MainEvent
import cn.vce.easylook.MainViewModel
import com.bumptech.glide.RequestManager
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicSearchFragment : BaseVmFragment<FragmentMusicSearchBinding>() {

    private lateinit var searchView: SearchView
    private lateinit var viewModel: MusicSearchVM

    private lateinit var mainVM: MainViewModel

    @Inject
    lateinit var glide: RequestManager

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    override fun initView() {
        this.setHasOptionsMenu(true)
        setupRecyclerView()
    }

    override fun initFragmentViewModel() {
        mainVM = getActivityViewModel()
        viewModel = getFragmentViewModel()
    }

    override fun observe() {
        super.observe()
        viewModel.songs.observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    binding.apply {
                        allProgressBar.isVisible = false
                        result.data?.let { songs ->
                            when (val adapter = searchRv.adapter) {
                                is BindingAdapter -> adapter.models = songs
                            }
                        }
                    }
                }
                Status.ERROR -> Unit
                Status.LOADING -> binding.allProgressBar.isVisible = true
            }
        }
    }

    override fun getLayoutId(): Int? = R.layout.fragment_music_search

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_searchfrag, menu)
        val searchItem: MenuItem = menu.findItem(R.id.menu_search)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        //searchView.mCloseButton.visibility = false
        val extras = FragmentNavigatorExtras(searchView to "hero_image", searchView to "hero_image")
        //searchView.setIconifiedByDefault(false)
        //searchView.isIconified = false
        searchItem.expandActionView() // 将 SearchView 直接扩展展开。
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

        return when (item.itemId) {
            R.id.menu_search -> {
                true
            }
            else -> false
        }
    }

    private fun setupRecyclerView() = binding.apply {
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }
        searchRv.apply {
            linear().setup {
                addType<MusicInfo>(R.layout.list_item)
                onBind {
                    val bind = getBinding<ListItemBinding>()
                    val musicInfo = getModel<MusicInfo>()
                    bind.apply {
                        tvPrimary.text = musicInfo.name
                        var artistIds = ""
                        var artistNames = ""
                        musicInfo.artists?.let {
                            artistIds = it[0].id
                            artistNames = it[0].name
                            for (j in 1 until it.size - 1) {
                                artistIds += ",${it[j].id}"
                                artistNames += ",${it[j].name}"
                            }
                        }
                        tvSecondary.text = artistNames+ " - " + (musicInfo.album?.name ?: "")
                        glide.load(musicInfo.album?.cover).into(ivItemImage)
                    }
                }
                onClick(R.id.songItemLayout) {
                    val musicInfo = getModel<MusicInfo>()
                    viewModel.songs.value?.data?.let {
                        mainVM.onEvent(MainEvent.ClickPlay(it, musicInfo))
                    }
                }
            }
        }
    }

}