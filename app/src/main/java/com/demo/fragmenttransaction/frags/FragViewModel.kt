package com.demo.fragmenttransaction.frags

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.fragmenttransaction.Op
import com.demo.fragmenttransaction.OpSelect

class FragViewModel: ViewModel() {

    val performOp: LiveData<Op>
        get() = _performOp

    private val _performOp = MutableLiveData<Op>(Op.UNDECIDED)

    val isItemSelected: LiveData<Boolean>
        get() = _isItemSelected

    private val _isItemSelected = MutableLiveData<Boolean>(false)

    val hasHiddenData: LiveData<Boolean>
        get() = _hasHiddenData

    private val _hasHiddenData = MutableLiveData<Boolean>(false)

    fun selectOp(op: Op) {

        when(op) {
            Op.UNDECIDED -> {
                donePerformance()
            }

            else -> {
                _performOp.value = op
            }
        }
    }

    fun donePerformance() {
        _performOp.value = Op.UNDECIDED
    }

    fun doneShowFragment() {
        _hasHiddenData.value = false
    }

    fun setItemIsSelected(isSelected: Boolean) {
        _isItemSelected.value = isSelected
    }

    fun setHasHiddenData(hasHiddenData: Boolean) {
        _hasHiddenData.value = hasHiddenData
    }
}