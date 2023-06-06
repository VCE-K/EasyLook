package cn.vce.easylook.feature_ai.presentation.ai_list

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.vce.easylook.feature_ai.data.entites.Ai
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.seimicrawler.xpath.JXDocument
import org.seimicrawler.xpath.JXNode
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject


@HiltViewModel
class AiListViewModel @Inject constructor(
    private val context: Context
) : ViewModel() {


    private val _ais = MutableLiveData<MutableList<Ai>>()
    val ais = _ais
    init {
        _ais.value = ArrayList()
        initRVModels()
    }


    private fun initRVModels() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //网络爬取的成功的话拿网络的，不成功的话拿本地html的
                val inputStream = context?.assets?.open("html.html")
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()
                var line: String?
                while ( bufferedReader.readLine() != null) {
                    line = bufferedReader.readLine()
                    stringBuilder.append(line)
                }
                val fileContent = stringBuilder.toString()

                if (fileContent != null) {
                    val doc: Document = Jsoup.connect("https://github.com/lzwme/chatgpt-sites").get()
                    val liElements = doc.selectXpath("//ol[@dir=\"auto\"]/li")

                    val aiList = mutableListOf<Ai>()
                    for (liElement in liElements) {
                        val itemDoc: Document = Jsoup.parse(liElement.html())

                        val href: String = itemDoc.select("a").attr("href")
                        //val emoji: String = itemDoc.select("a, g-emoji").text()
                        var name: String? = itemDoc.selectXpath("//strong").text()
                        var desc: String? = itemDoc.selectXpath("//text()[normalize-space()] | //code/text()").text()

                        if (name == null && desc != null) {
                            if(desc.toString().startsWith(",")) {
                                desc = desc.toString().replaceFirst(",".toRegex(), "")
                            }
                            name = desc.toString()
                        }
                        if(name!!.isNotEmpty()){
                            aiList.add( Ai(name.toString(), href) )
                        }
                    }
                    _ais.postValue(aiList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
