package com.example.moneymindermobile.data.api.entities


import com.example.type.KeyValuePairOfGuidAndNullableOfDecimalInput
import java.util.UUID


data class ExpenseInsertInput(
    val amount : Float,
    val description : String,
    val groupId : UUID,
    val userAmountList: List<KeyValuePairOfGuidAndNullableOfDecimalInput>
)

/*data class KeyValuePairOfGuidAndNullableOfDecimalInput (
    val id : UUID,
    val amount : Float
)*/