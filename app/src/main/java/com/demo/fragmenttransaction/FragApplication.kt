package com.demo.fragmenttransaction

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class FragApplication: Application() {

    companion object {
        const val TAG = "FRAG_APP"
    }

    val observer = MainLifecycleObserver()








    class MainLifecycleObserver: DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            logState("ON_CREATE", owner)
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            logState("ON_RESUME", owner)
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            logState("ON_START", owner)
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            logState("ON_PAUSE", owner)
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            logState("ON_STOP", owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            logState("ON_DESTROY", owner)
        }

        private fun logState(state: String, owner: LifecycleOwner) {
            Log.d(TAG, "$state: $owner ${owner.lifecycle.currentState}")
        }

    }
}