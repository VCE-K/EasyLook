package cn.vce.easylook.feature_ai.presentation.ai_web


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebSettingsCompat.DARK_STRATEGY_PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature
import cn.vce.easylook.base.BaseFragment
import cn.vce.easylook.databinding.FragmentAiWebBinding

class AiWebFragment : BaseFragment() {

    private lateinit var binding: FragmentAiWebBinding

    private val viewModel by viewModels<AiWebViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAiWebBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Load the content
        settingWebView()
        viewModel.aiUrl.observe(viewLifecycleOwner) {
            binding.webview.loadUrl(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webview.destroy()
    }



    private fun invokeShareIntent(message: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        activity?.let { ContextCompat.startActivity(it, shareIntent, null) }
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun settingWebView(){
        val jsObjName = "jsObject"
        val allowedOriginRules = setOf("https://raw.githubusercontent.com")


        val nightModeFlag = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlag == Configuration.UI_MODE_NIGHT_YES) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(
                    binding.webview.settings,
                    WebSettingsCompat.FORCE_DARK_ON
                )
            }

            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                WebSettingsCompat.setForceDarkStrategy(
                    binding.webview.settings,
                    DARK_STRATEGY_PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING
                )
            }
        }

        val assetLoader = WebViewAssetLoader.Builder()
            .setDomain("raw.githubusercontent.com")
            .addPathHandler(
                "/views-widgets-samples/assets/",
                WebViewAssetLoader.AssetsPathHandler(requireContext())
            )
            .addPathHandler(
                "/views-widgets-samples/res/",
                WebViewAssetLoader.ResourcesPathHandler(requireContext())
            )
            .build()

        // Set clients
        binding.webview.webViewClient = MyWebViewClient()


        activity?.let {
            if (it.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }

        // Enable Javascript
        binding.webview.settings.javaScriptEnabled = true

        // Create a JS object to be injected into frames; Determines if WebMessageListener
        // or WebAppInterface should be used
        /*createJsObject(
            binding.webview,
            jsObjName,
            allowedOriginRules
        ) { message -> invokeShareIntent(message) }*/
    }

    inner class MyWebViewClient() :
        WebViewClientCompat() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.let {
                url?.let {
                    view.loadUrl(url)
                    return true
                }
            }
            return false
        }
    }
}