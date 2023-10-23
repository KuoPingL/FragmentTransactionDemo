package com.demo.fragmenttransaction

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.fragmenttransaction.R

class MainViewModel: ViewModel() {

    val datas: LiveData<Array<FragmentAdapter.Data>>
        get() = _datas

    private val _datas = MutableLiveData<Array<FragmentAdapter.Data>>(emptyArray())

    val transactionOp: LiveData<Op>
        get() = _transactionOp

    private val _transactionOp = MutableLiveData(Op.UNDECIDED)

    val selectedData: LiveData<FragmentAdapter.Data?>
        get() = _selectedData

    private var _selectedData = MutableLiveData<FragmentAdapter.Data?>(null)

    val hiddenTag: LiveData<String?>
        get() = _hiddenTag

    private var _hiddenTag = MutableLiveData<String?>(null)

    val detachedTag: LiveData<String?>
        get() = _detachedTag

    private var _detachedTag = MutableLiveData<String?>(null)

    fun refreshStackData(fragmentManager: FragmentManager, activeFragmentName: String?) {
        val n = fragmentManager.backStackEntryCount
        val newDatas = mutableListOf<FragmentAdapter.Data>()
        for (i in n - 1 downTo 0) {
            fragmentManager.getBackStackEntryAt(i).also {
                if (activeFragmentName == it.name) {
                    newDatas.add(
                        FragmentAdapter.Data(
                            it.name ?: "UNKNOWN",
                            it.id,
                            R.color.color_active_frag
                        )
                    )
                } else {
                    newDatas.add(FragmentAdapter.Data(it.name ?: "UNKNOWN", it.id))
                }
            }
        }
        _datas.value = newDatas.toTypedArray()
    }

    fun performOp(op: Op) {
        when(op) {
            Op.UNDECIDED -> {
                donePerformOp()
            }
            else -> {
                _transactionOp.value = op
            }
        }
    }

    fun donePerformOp() {
        _transactionOp.value = Op.UNDECIDED
    }

    fun attachData(data: FragmentAdapter.Data, fm: FragmentManager) {
        setSelectedData(null)
        val n = fm.backStackEntryCount
        val newDatas = mutableListOf<FragmentAdapter.Data>()
        for (i in n - 1 downTo 0) {
            val stackEntry = fm.getBackStackEntryAt(i)

            if (stackEntry.name == data.name) {
                newDatas.add(data)
            } else {
                newDatas.add(FragmentAdapter.Data(stackEntry.name ?: "UNKNOWN", stackEntry.id))
            }
        }
        _datas.value = newDatas.toTypedArray()
    }

    fun setSelectedData(data: FragmentAdapter.Data?) {
        _selectedData.value = data
    }

    fun attachFrag(fm: FragmentManager, tag: String?) {
        _detachedTag.value = null
        setSelectedData(null)

        val n = fm.backStackEntryCount
        val newDatas = mutableListOf<FragmentAdapter.Data>()
        for (i in n - 1 downTo 0) {
            fm.getBackStackEntryAt(i).also {
                if (tag == it.name) {
                    newDatas.add(
                        FragmentAdapter.Data(
                            it.name ?: "UNKNOWN",
                            it.id,
                            R.color.color_attach
                        )
                    )
                } else {
                    newDatas.add(FragmentAdapter.Data(it.name ?: "UNKNOWN", it.id))
                }
            }
        }
        _datas.value = newDatas.toTypedArray()
    }

    fun hideFrag(fm: FragmentManager, tag: String?) {
        _hiddenTag.value = tag
        setSelectedData(null)

        val n = fm.backStackEntryCount
        val newDatas = mutableListOf<FragmentAdapter.Data>()
        for (i in n - 1 downTo 0) {
            fm.getBackStackEntryAt(i).also {
                if (tag == it.name) {
                    newDatas.add(
                        FragmentAdapter.Data(
                            it.name ?: "UNKNOWN",
                            it.id,
                            R.color.color_hide
                        )
                    )
                } else {
                    newDatas.add(FragmentAdapter.Data(it.name ?: "UNKNOWN", it.id))
                }
            }
        }
        _datas.value = newDatas.toTypedArray()
    }

    fun detachFrag(fm: FragmentManager, tag: String?) {
        _detachedTag.value = tag
        setSelectedData(null)

        val n = fm.backStackEntryCount
        val newDatas = mutableListOf<FragmentAdapter.Data>()
        for (i in n - 1 downTo 0) {
            fm.getBackStackEntryAt(i).also {
                if (tag == it.name) {
                    newDatas.add(
                        FragmentAdapter.Data(
                            it.name ?: "UNKNOWN",
                            it.id,
                            R.color.color_detach
                        )
                    )
                } else {
                    newDatas.add(FragmentAdapter.Data(it.name ?: "UNKNOWN", it.id))
                }
            }
        }
        _datas.value = newDatas.toTypedArray()
    }

    fun showFrag(fm: FragmentManager, tag: String?) {
        _hiddenTag.value = null
        setSelectedData(null)

        val n = fm.backStackEntryCount
        val newDatas = mutableListOf<FragmentAdapter.Data>()
        for (i in n - 1 downTo 0) {
            fm.getBackStackEntryAt(i).also {
                if (tag == it.name) {
                    newDatas.add(
                        FragmentAdapter.Data(
                            it.name ?: "UNKNOWN",
                            it.id,
                            R.color.color_show
                        )
                    )
                } else {
                    newDatas.add(FragmentAdapter.Data(it.name ?: "UNKNOWN", it.id))
                }
            }
        }
        _datas.value = newDatas.toTypedArray()
    }

}