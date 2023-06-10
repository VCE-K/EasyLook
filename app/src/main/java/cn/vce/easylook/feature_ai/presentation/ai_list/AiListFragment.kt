package cn.vce.easylook.feature_ai.presentation.ai_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseFragment
import cn.vce.easylook.databinding.FragmentAiListBinding
import cn.vce.easylook.databinding.FragmentAiListItemBinding
import cn.vce.easylook.feature_ai.data.entites.Ai
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AiListFragment : BaseFragment() {

    private lateinit var binding: FragmentAiListBinding
    private val viewModel by viewModels<AiListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAiListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() = binding.apply {
        viewModel.ais.observe(viewLifecycleOwner){
            when(val adapter = aiRv.adapter) {
                is BindingAdapter -> {
                    adapter.models = it
                }
            }
        }
        aiRv.grid(2).setup {
            addType<Ai>(R.layout.fragment_ai_list_item)
            onBind {
                val itemBinding = getBinding<FragmentAiListItemBinding>()
                val model = getModel<Ai>()
                itemBinding.aiText.text = model.name
            }
            R.id.aiText.onClick {
                val model = getModel<Ai>()
                val bundle = Bundle().apply {
                    putString("aiUrl", model.aiUrl)
                    putString("title", model.name)
                }
                findNavController().navigate(R.id.globalActionToAiWebFragment, bundle)
            }
        }

    }


}