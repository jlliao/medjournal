package com.example.medjournal.models

/**
 * A data class of medication events (reminder object)
 * @param info subtitle, which may include dosage and other relevant info
 * @param time time to take medicine
 * @param medName namd of the medicine
 * @param date date of taking the medicine
 */
data class MedEvent(
    val info: String, val time: String,
    val medName: String, val date: String
)