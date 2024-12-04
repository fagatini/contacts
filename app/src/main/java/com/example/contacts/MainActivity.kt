package com.example.contacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contacts.ui.theme.ContactsTheme
import com.example.contacts.ui.theme.Green

data class Contact(val name: String, val phone: String, val email: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactsTheme {
                Contacts()
            }
        }
    }
}

@Composable
fun Contacts() {
    val navController = rememberNavController()
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }

    NavHost(
        navController = navController,
        startDestination = "view_contacts"
    ) {
        composable("view_contacts") {
            ViewContactsScreen(
                contacts = contacts,
                onDeleteContact = { contact ->
                    contacts = contacts.filterNot { it == contact }
                },
                onAddContact = {
                    navController.navigate("add_contact")
                }
            )
        }
        composable("add_contact") {
            AddContactScreen(
                onAddContact = { contact ->
                    contacts = contacts + contact
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewContactsScreen(
    contacts: List<Contact>,
    onDeleteContact: (Contact) -> Unit,
    onAddContact: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Список контактов") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddContact) {
                Icon(Icons.Default.Add, contentDescription = "Добавить контакт")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(contacts) { contact ->
                ContactCard(contact = contact, onDelete = {
                    onDeleteContact(contact)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(onAddContact: (Contact) -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }

    val isNameValid = remember(name) { name.text.isNotEmpty() }
    val isPhoneValid = remember(phone) { phone.text.length == 11 && phone.text.all { it.isDigit() } }
    val isEmailValid = remember(email) { email.text.contains("@") && email.text.contains(".") }
    val isFormValid = isNameValid && isPhoneValid && isEmailValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить контакт") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("ФИО") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isNameValid) Green else MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = if (isNameValid) Green else MaterialTheme.colorScheme.outline,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isPhoneValid) Green else MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = if (isPhoneValid) Green else MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isEmailValid) Green else MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = if (isEmailValid) Green else MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onAddContact(Contact(name.text, phone.text, email.text))
                    name = TextFieldValue("")
                    phone = TextFieldValue("")
                    email = TextFieldValue("")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text("Сохранить контакт")
            }
        }
    }
}

@Composable
fun ContactCard(contact: Contact, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ФИО: ${contact.name}")
                Text("Телефон: ${contact.phone}")
                Text("Email: ${contact.email}")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить контакт")
            }
        }
    }
}
