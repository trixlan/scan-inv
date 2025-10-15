package com.gercha.scan_inv.Fragmentos

import android.view.KeyEvent

interface KeyEventListener {
    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean
}