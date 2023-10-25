package com.demo.fragmenttransaction

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.fragmenttransaction.R
import com.demo.fragmenttransaction.databinding.ActivityMainBinding
import com.demo.fragmenttransaction.frags.ButtonFragment

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val _vm by viewModels<MainViewModel> { ViewModelFactory }

    private var counter = 0
    private lateinit var _adapter: FragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            counter = it.getInt(COUNTER_TAG)
        }

        lifecycle.addObserver((application as FragApplication).observer)

        _binding = ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MainActivity
        }

        setContentView(_binding.root)

        _adapter = FragmentAdapter(object : FragmentAdapter.SelectListener {
            override fun onSelect(data: FragmentAdapter.Data?) {

                if (data == FragmentAdapter.baseData) {
                    _vm.performOp(Op.REPLACE)
                } else {
                    _vm.setSelectedData(data)
                }
            }
        }).also {
            it.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
                override fun onChanged() {
                    _binding.rv.scrollToPosition(0)
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    _binding.rv.scrollToPosition(0)
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
//                    super.onItemRangeChanged(positionStart, itemCount, payload)
                    _binding.rv.scrollToPosition(0)
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                    super.onItemRangeInserted(positionStart, itemCount)
                    _binding.rv.scrollToPosition(0)
                }

                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
//                    super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                    _binding.rv.scrollToPosition(0)
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
//                    super.onItemRangeRemoved(positionStart, itemCount)
                    _binding.rv.scrollToPosition(0)
                }
            })
        }

        _binding.rv.adapter = _adapter
        _binding.rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
//                    val name = supportFragmentManager.getBackStackEntryAt(1).name
//                    Log.d("FRAGMENT at 1", "FRAGMENT AT 1 is $name")
//                    supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                    supportFragmentManager.fragments.first().childFragmentManager.popBackStack()
//                    supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    try {
                        supportFragmentManager.popBackStack()
                        supportFragmentManager.executePendingTransactions()
                    } catch (e: Exception) {
                        supportFragmentManager.beginTransaction().apply {
                            for (frag in supportFragmentManager.fragments) {
                                remove(frag)
                            }
                            commitNow()
                        }
                        supportFragmentManager.executePendingTransactions()

                        showExplanationDialog(Op.UNDECIDED, e.message)
                    } finally {
                        _vm.refreshStackData(supportFragmentManager, getCurrentFragmentTag())
                    }

                    return
                }

                remove()
                _vm.refreshStackData(supportFragmentManager, getCurrentFragmentTag())
                supportFragmentManager.executePendingTransactions()
                onBackPressedDispatcher.onBackPressed()
            }

        })

        _vm.transactionOp.observe(this) {
            it?.let { op ->

                val transaction = supportFragmentManager.beginTransaction()
                var data: FragmentAdapter.Data? = null
                var color = R.color.color_undecided
                val name = op.getString() + " $counter"

                _adapter.clearSelectedData()

                when(op) {
                    Op.ADD, Op.ADD_WITHOUT_BACKSTACK, Op.ADD_WITHOUT_ID -> {
                        _adapter.clearSelectedData()
                        counter++
                        color = R.color.color_add
                        val frag = generateFragment(name)
                        val entry = transaction.run {
                            when(op) {
                                Op.ADD_WITHOUT_ID -> {
                                    add(frag, name)
                                    addToBackStack(name)
                                }
                                Op.ADD -> {
                                    add(_binding.fcv.id, frag, name)
                                    addToBackStack(name)
                                }
                                else -> {
                                    add(_binding.fcv.id, frag, name)
                                }
                            }
                            commit()
                        }

                        supportFragmentManager.executePendingTransactions()
                        data = FragmentAdapter.Data(name, entry, color)
                        _vm.attachData(data, supportFragmentManager)
                    }

                    Op.REPLACE, Op.REPLACE_WITHOUT_BACKSTACK -> {
                        _adapter.clearSelectedData()
                        counter++
                        color = R.color.color_replace
                        val frag = generateFragment(name)
                        val entry = transaction
                            .replace(_binding.fcv.id, frag, name).run {
                                if (op == Op.REPLACE) {
                                    addToBackStack(name)
                                }
                                commit()
                            }

                        supportFragmentManager.executePendingTransactions()
                        data = FragmentAdapter.Data(name, entry, color)
                        _vm.attachData(data, supportFragmentManager)
                    }

                    Op.SHOW -> {
                        _vm.hiddenTag.value?.let { hiddenTag ->
                            val frag = supportFragmentManager.findFragmentByTag(hiddenTag)
                            frag?.let {
                                transaction.show(it).commit()
                                supportFragmentManager.executePendingTransactions()
                                _vm.showFrag(supportFragmentManager, hiddenTag)
                            }
                        } ?: showExplanationDialog(op)
                    }

                    Op.HIDE -> {
                        _vm.selectedData.value?.let {
                            val frag = supportFragmentManager.findFragmentByTag(it.name)

                            frag?.let {
                                transaction.hide(it)
                                transaction.commit()
                                supportFragmentManager.executePendingTransactions()
                                _vm.hideFrag(supportFragmentManager, frag.tag)
                            } ?: Toast.makeText(this, "Fragment Not Found", Toast.LENGTH_SHORT).show()

                        } ?: showExplanationDialog(op)
                    }

                    Op.ATTACH -> {
                        _vm.detachedTag.value?.let {

                            val frag = supportFragmentManager.findFragmentByTag(it)

                            frag?.let {
                                transaction.attach(it).commit()
                                supportFragmentManager.executePendingTransactions()
                                _vm.attachFrag(supportFragmentManager, it.tag)
                            } ?: Toast.makeText(this, "Fragment Not Found", Toast.LENGTH_SHORT).show()

                        } ?: showExplanationDialog(op)
                    }

                    Op.DETACH -> {
                        _vm.selectedData.value?.let {
                            supportFragmentManager.findFragmentByTag(it.name)?.let {frag ->
                                transaction.detach(frag).commit()
                                supportFragmentManager.executePendingTransactions()
                                _vm.setSelectedData(null)
                                _vm.detachFrag(supportFragmentManager, it.name)
                            } ?: Toast.makeText(this, "Fragment Not Found", Toast.LENGTH_SHORT).show()
                        } ?: showExplanationDialog(op)
                    }

                    Op.SET_PRIMARY -> {
                        // as to be an active fragment
                        _vm.selectedData.value?.let {
                            supportFragmentManager.findFragmentByTag(it.name)?.let {frag ->
                                transaction.setPrimaryNavigationFragment(frag).commit()
                                supportFragmentManager.executePendingTransactions()
                                clearSelectedAndRefresh()
                            } ?: Toast.makeText(this, "Fragment Not Found", Toast.LENGTH_SHORT).show()
                        } ?: showExplanationDialog(op)
                    }

                    Op.UNDECIDED -> {

                    }
                }
            }
        }

        _vm.datas.observe(this) {
            _adapter.submitList(it.toMutableList())
        }


        _vm.performOp(Op.REPLACE)

//        val title = "Primary $counter"
//        val transaction = supportFragmentManager.beginTransaction()
//        val bundle = Bundle().also {
//            it.putString(ButtonFragment.BUNDLE_NAME, title)
//        }
//        val frag = ButtonFragment().also {
//            it.arguments = bundle
//        }
//
//        transaction.replace(_binding.fcv.id, frag)
//        transaction.addToBackStack(title)
//        transaction.commit()
    }

//    override fun onRestoreInstanceState(
//        savedInstanceState: Bundle?,
//        persistentState: PersistableBundle?
//    ) {
//        super.onRestoreInstanceState(savedInstanceState, persistentState)
//        counter = savedInstanceState?.getInt(COUNTER_TAG) ?: 0
//    }

//    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
//        outState.putInt(COUNTER_TAG, counter)
//        super.onSaveInstanceState(outState, outPersistentState)
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(COUNTER_TAG, counter)
        super.onSaveInstanceState(outState)
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        counter = savedInstanceState.getInt(COUNTER_TAG)
//        super.onRestoreInstanceState(savedInstanceState)
//    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver((application as FragApplication).observer)
    }

    fun getCurrentFragmentTag(): String? {
        val frag = supportFragmentManager.findFragmentById(R.id.fcv)
        if (frag?.isVisible == true) {
            return frag.tag
        }
        return null
    }

    fun clearSelectedAndRefresh() {
        _vm.setSelectedData(null)
        _vm.refreshStackData(supportFragmentManager, getCurrentFragmentTag())
    }

    private fun generateFragment(name: String): Fragment {
        return ButtonFragment().also {
            it.arguments = Bundle().apply { putString(ButtonFragment.NAME, name)}
        }
    }

    private fun showExplanationDialog(op: Op, custMsg: String? = null) {
        val msg = when(op) {

            Op.SHOW -> {
                // you can only show a fragment if it was previously hidden
                // please hide a fragment first.
                "You can only show a fragment if it was previously hidden.\n\nPlease hide a fragment first."
            }

            Op.HIDE -> {
                // you can only hide a fragment that is currently inside the backstack.
                // please select a fragment
                "You can only hide a fragment that is currently inside the backstack.\n\nPlease select a fragment first."
            }

            Op.SET_PRIMARY -> {
                // you can only set a fragment primary that is currently inside the backstack.
                // please select a fragment
                "You can only set a fragment primary that is currently inside the backstack. \n\nPlease select a fragment first."
            }

            Op.DETACH -> {
                // you can only detach a fragment that is currently inside the backstack.
                // please select a fragment
                "You can only detach a fragment that is currently inside the backstack.\n\nPlease select a fragment first."
            }

            Op.ATTACH -> {
                // you can only attach when the fragment was previously detached.
                // the end result is attaching the fragment and display it on the screen
                // please detach a fragmen first
                "You can only attach when the fragment was previously detached.\n\nPlease detach a fragment first."
            }

            else -> {
                custMsg
            }
        }

        msg?.let {
            AlertDialog.Builder(this).apply {
                setTitle(if(op == Op.UNDECIDED) "Exception" else "Hint")
                setMessage(it)
            }.show()
        }
    }

    companion object {
        const val COUNTER_TAG = "COUNTER"
    }
}