package com.gercha.scan_inv.interfaces

import android.view.KeyEvent

interface KeyEventListener {
    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean
}