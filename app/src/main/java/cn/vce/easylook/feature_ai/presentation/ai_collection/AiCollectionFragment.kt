package cn.vce.easylook.feature_ai.presentation.ai_collection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.vce.easylook.R
import cn.vce.easylook.base.BaseFragment
import cn.vce.easylook.databinding.FragmentHomeAiBinding
import com.google.android.material.tabs.TabLayoutMediator

class AiCollectionFragment : BaseFragment() {
    private  val binding: FragmentHomeAiBinding by lazy {
        FragmentHomeAiBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}