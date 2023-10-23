package com.demo.fragmenttransaction.frags

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.demo.fragmenttransaction.FragApplication
import com.demo.fragmenttransaction.MainViewModel
import com.demo.fragmenttransaction.Op
import com.demo.fragmenttransaction.ViewModelFactory
import com.demo.fragmenttransaction.databinding.FragmentButtonTextBinding

class ButtonFragment : Fragment() {

    private lateinit var _binding: FragmentButtonTextBinding
    private val _mainVm by activityViewModels<MainViewModel> { ViewModelFactory }
    private val _vm by viewModels<FragViewModel> { ViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentButtonTextBinding.inflate(inflater)

        lifecycle.addObserver((requireActivity().application as FragApplication).observer)

        arguments?.let {
            _binding.title = it.getString(NAME)
        }

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "ON_VIEW_CREATED: ${getFragmentString()}")

        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.vm = _vm

        _vm.performOp.observe(viewLifecycleOwner) {
            if (it != Op.UNDECIDED) {
                _mainVm.performOp(it)
                _vm.donePerformance()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "ON_DESTROY_VIEW: ${getFragmentString()}")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "ON_ATTACH: ${getFragmentString()}")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "ON_DETACH: ${getFragmentString()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver((requireActivity().application as FragApplication).observer)
    }

    private fun getFragmentString(): String {
        val sb = StringBuilder(128)
        val cls: Class<*> = javaClass
        sb.append(cls.simpleName)
        sb.append("{")
        sb.append(Integer.toHexString(System.identityHashCode(this)))
        sb.append("}")
        return sb.toString()
    }

    companion object {
        const val NAME = "NAME"
        const val TAG = "FRAG_BUTTON"
    }
}