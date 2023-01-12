package com.ispgr5.locationsimulator.presentation.edit;
import com.ispgr5.locationsimulator.domain.model.Configuration

sealed class EditEvent {
    data class addDuration(val duration : String ) : EditEvent()
    data class addPause(val pause : String) : EditEvent()
    object save : EditEvent()
}
