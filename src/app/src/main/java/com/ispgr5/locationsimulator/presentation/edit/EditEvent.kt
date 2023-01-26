package com.ispgr5.locationsimulator.presentation.edit

/**
 * The UI Events the View can call
 */
sealed class EditEvent {
    data class EnteredName(val name: String) : EditEvent()
    data class EnteredDescription(val description: String) : EditEvent()
    object SaveConfiguration : EditEvent()
}
