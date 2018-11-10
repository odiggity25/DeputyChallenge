package com.orrie.deputychallenge.views

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.subjects.PublishSubject

open class BaseActivity : AppCompatActivity() {

    private val exitsSubject = PublishSubject.create<Unit>()
    protected var exits = exitsSubject.hide()

    override fun onDestroy() {
        super.onDestroy()
        exitsSubject.onNext(Unit)
    }
}