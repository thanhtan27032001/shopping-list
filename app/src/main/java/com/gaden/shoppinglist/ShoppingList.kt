package com.gaden.shoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quality: Int,
    var isEditing: Boolean = false
)

class ShoppingList {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListApp() {
    var shoppingItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var isShowDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuality by remember { mutableStateOf("0") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { isShowDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(shoppingItems) { item ->
                if (item.isEditing) {
                    ShoppingItemEditor(
                        item,
                        onEditComplete = { editedName, editQuality ->
                            shoppingItems = shoppingItems.map { it.copy(isEditing = false) }
                            val editedItem = shoppingItems.find { it.id == item.id }
                            editedItem?.let {
                                it.name = editedName
                                it.quality = editQuality
                            }
                        }
                    )
                }
                else {
                    ShoppingListItem(
                        item = item,
                        onEditClicked = {
                            shoppingItems = shoppingItems.map {
                                it.copy(isEditing = it.id == item.id)
                            }
                        },
                        onDeleteClicked = {
                            shoppingItems = shoppingItems - item
                        }
                    )
                }
            }
        }
    }

    if (isShowDialog) {
        AlertDialog(
            onDismissRequest = { isShowDialog = false },
            confirmButton = {
                Button(onClick = {
                    if (itemName.isNotBlank()) {
                        val newItem = ShoppingItem(
                            id = shoppingItems.size + 1,
                            name = itemName,
                            quality = itemQuality.toIntOrNull() ?: 0
                        )
                        shoppingItems = shoppingItems + newItem
                        isShowDialog = false
                        itemName = ""
                        itemQuality = "0"
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = {}) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Add Shopping item")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        placeholder = { Text("Item name") },
                        onValueChange = {
                            itemName = it
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = itemQuality,
                        placeholder = { Text("Item quantity") },
                        onValueChange = {
                            itemQuality = it
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        )
                    )
                }
            }
        )
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Box {
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .border(
                    border = BorderStroke(
                        1.dp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item.name)
            Text(item.quality.toString())
            Row() {
                IconButton(
                    onClick = onEditClicked
                ) { Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit"
                ) }
                IconButton(
                    onClick = onDeleteClicked
                ) { Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete"
                ) }
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(
    shoppingItem: ShoppingItem,
    onEditComplete: (editedName: String, editQuality: Int) -> Unit,
) {
    var editedName by remember { mutableStateOf(shoppingItem.name) }
    var editQuality by remember { mutableStateOf(shoppingItem.quality.toString()) }
    var isEditting by remember { mutableStateOf(shoppingItem.isEditing) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column() {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier.wrapContentSize().padding(8.dp)
            )
            BasicTextField(
                value = editQuality,
                onValueChange = { editQuality = it },
                singleLine = true,
                modifier = Modifier.wrapContentSize().padding(8.dp)
            )
        }
        Button(
            onClick = {
                isEditting = false
                onEditComplete.invoke(
                    editedName,
                    editQuality.toIntOrNull() ?: 0
                )
            }
        ) {
            Text("Save")
        }
    }
}