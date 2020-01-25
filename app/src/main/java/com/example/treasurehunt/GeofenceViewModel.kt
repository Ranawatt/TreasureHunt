package com.example.treasurehunt

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/*
 * This class contains the state of the game.  The two important pieces of state are the index
 * of the geofence, which is the geofence that the game thinks is active, and the state of the
 * hint being shown.  If the hint matches the geofence, then the Activity won't update the geofence
 * as it cycles through various activity states.
 *
 * These states are stored in SavedState, which matches the Android lifecycle.  Destroying the
 * associated Activity with the back action will delete all state and reset the game, while
 * the Home action will cause the state to be saved, even if the game is terminated by Android in
 * the background.
 */
class GeofenceViewModel(state: SavedStateHandle) : ViewModel() {
    private val _geofenceIndex = state.getLiveData(GEOFENCE_INDEX_KEY, -1)
    private val _hintIndex = state.getLiveData(HINT_INDEX_KEY, 0)
    val geofenceIndex: LiveData<Int>
        get() = _geofenceIndex

    val geofenceHintResourceId = Transformations.map(geofenceIndex) {
        val index = geofenceIndex.value ?: -1
        when {
            index < 0 -> R.string.not_started_hint
            index < GeofencingConstants.NUM_LANDMARKS -> GeofencingConstants.LANDMARK_DATA[geofenceIndex.value!!].hint
            else -> R.string.geofence_over
        }
    }

    val geofenceImageResourceId = Transformations.map(geofenceIndex) {
        val index = geofenceIndex.value ?: -1
        when {
            index < GeofencingConstants.NUM_LANDMARKS -> R.drawable.android_map
            else -> R.drawable.android_treasure
        }
    }

    fun updateHint(currentIndex: Int) {
        _hintIndex.value = currentIndex+1
    }

    fun geofenceActivated() {
        _geofenceIndex.value = _hintIndex.value
    }

    fun geofenceIsActive() =_geofenceIndex.value == _hintIndex.value
    fun nextGeofenceIndex() = _hintIndex.value ?: 0
}

private const val HINT_INDEX_KEY = "hintIndex"
private const val GEOFENCE_INDEX_KEY = "geofenceIndex"