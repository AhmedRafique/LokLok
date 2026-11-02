package com.loklok.app.feature.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loklok.app.domain.model.Contact
import com.loklok.app.domain.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactsUiState(
    val contacts: List<Contact> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            contactRepository.syncContacts()
        }
    }

    val uiState: StateFlow<ContactsUiState> = combine(
        contactRepository.getContacts(),
        _searchQuery
    ) { contacts, query ->
        val filtered = if (query.isBlank()) {
            contacts
        } else {
            contacts.filter {
                it.name.contains(query, ignoreCase = true) || it.phoneNumber.contains(query)
            }
        }
        ContactsUiState(
            contacts = filtered,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ContactsUiState(isLoading = true)
    )

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }
}
