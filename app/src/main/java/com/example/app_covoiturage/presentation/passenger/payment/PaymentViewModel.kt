package com.example.app_covoiturage.presentation.passenger.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_covoiturage.domain.usecase.passenger.MarkReservationAsPaidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentUiState(
    val totalPrice: Double = 0.0,
    val selectedMethod: String? = null,
    val isProcessing: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val markAsPaidUseCase: MarkReservationAsPaidUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState

    private val reservationId: String = savedStateHandle.get<String>("reservationId") ?: ""

    init {
        val price = savedStateHandle.get<String>("totalPrice")?.toDoubleOrNull() ?: 0.0
        _uiState.value = _uiState.value.copy(totalPrice = price)
    }

    fun onMethodSelected(method: String) {
        _uiState.value = _uiState.value.copy(selectedMethod = method, error = null)
    }

    fun confirmPayment() {
        if (_uiState.value.selectedMethod == null) {
            _uiState.value = _uiState.value.copy(error = "Choisis un moyen de paiement")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            val result = markAsPaidUseCase(reservationId)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isProcessing = false, isSuccess = true)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isProcessing = false, error = e.message)
            }
        }
    }
}