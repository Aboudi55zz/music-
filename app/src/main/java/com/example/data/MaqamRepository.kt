package com.example.data

import com.example.model.Maqam
import com.example.model.MaqamFamily

object MaqamRepository {
    val allMaqamat: List<Maqam> =
        MaqamFamiliesA.rastAndBayatiAndHijaz + MaqamFamiliesB.kurdNahawandAjamSabaSikahNikriz

    fun getMaqamById(id: String): Maqam? = allMaqamat.find { it.id == id }

    fun getMaqamatByFamily(family: MaqamFamily): List<Maqam> =
        allMaqamat.filter { it.family == family }

    fun getFeaturedMaqam(): Maqam =
        allMaqamat.first { it.id == "rast" }
}
