package com.demo.fragmenttransaction

enum class Op {
    ADD, ADD_WITHOUT_BACKSTACK, ADD_WITHOUT_ID, REPLACE, REPLACE_WITHOUT_BACKSTACK, SHOW, HIDE, ATTACH, DETACH, SET_PRIMARY, UNDECIDED;

    fun getString(): String {
        return when(this) {
            ADD -> "ADD"
            ADD_WITHOUT_ID -> "ADD_WI"
            ADD_WITHOUT_BACKSTACK -> "ADD_WB"
            REPLACE -> "REP"
            REPLACE_WITHOUT_BACKSTACK -> "REP_WB"
            SHOW -> "SHOW"
            HIDE -> "HIDE"
            ATTACH -> "ATT"
            DETACH -> "DET"
            SET_PRIMARY -> "SET_P"
            UNDECIDED -> ""
        }
    }
}

enum class OpSelect {
    TOP, BOTTOM, UNDECIDED
}