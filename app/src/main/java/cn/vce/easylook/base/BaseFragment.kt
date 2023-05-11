package cn.vce.noteapp.feature_note.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

open class BaseFragment(@LayoutRes id: Int): Fragment(id) {
    companion object {
        const val TAG = "BaseFragment"
    }


    private val className: String = javaClass.simpleName
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "$className-onAttach")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "$className-onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "$className-onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "$className-onActivityCreated")
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "$className-onStart")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "$className-onResume")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "$className-onPause")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "$className-onStop")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "$className-onDestroyView")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "$className-onDestroy")
    }
    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "$className-onDetach")
    }
}