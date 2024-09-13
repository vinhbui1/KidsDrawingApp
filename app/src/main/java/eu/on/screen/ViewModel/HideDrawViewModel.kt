package eu.on.screen.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HideDrawViewModel private constructor() {
    private val _hideDraw = MutableLiveData<Boolean>(false)
    val hideDraw: LiveData<Boolean> = _hideDraw

    fun setHideDraw() {
        _hideDraw.value = _hideDraw.value?.not()
    }

    companion object {
        @Volatile
        private var instance: HideDrawViewModel? = null

        fun getInstance(): HideDrawViewModel {
            return instance ?: synchronized(this) {
                instance ?: HideDrawViewModel().also { instance = it }
            }
        }
    }
}